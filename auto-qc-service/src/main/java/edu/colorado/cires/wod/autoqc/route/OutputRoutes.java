package edu.colorado.cires.wod.autoqc.route;

import edu.colorado.cires.wod.autoqc.model.AutoQcFileResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.processor.FileDoneAggregationStrategy;
import edu.colorado.cires.wod.autoqc.processor.FileNameUtils;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutputRoutes extends RouteBuilder {

  private final FileDoneAggregationStrategy fileDoneAggregationStrategy;

  @Autowired
  public OutputRoutes(FileDoneAggregationStrategy fileDoneAggregationStrategy) {
    this.fileDoneAggregationStrategy = fileDoneAggregationStrategy;
  }

  @Override
  public void configure() throws Exception {

    from("seda:test-aggregate?concurrentConsumers={{autoqc.concurrency}}")
        .routeId("test-aggregate")
        .aggregate(ExpressionBuilder.bodyExpression(AutoQcTestContextImpl.class, AutoQcTestContextImpl::getFilePathPrefix),
            fileDoneAggregationStrategy)
        .completionTimeout(60000L * 60L) //TODO
        .to("seda:save-summary?blockWhenFull=true");

    from("seda:error")
        .routeId("error")
        .to("seda:save-cast-failures?blockWhenFull=true")
        .to("seda:test-aggregate?blockWhenFull=true");

    from("seda:save-summary")
        .routeId("save-summary")
        .setHeader(Exchange.FILE_NAME, ExpressionBuilder.bodyExpression(AutoQcFileResult.class, FileNameUtils::getSummaryFileName))
        .marshal().json(JsonLibrary.Jackson)
        .to("file:{{autoqc.pickup-dir}}?charset=utf-8");

    from("seda:save-cast-failures?concurrentConsumers={{autoqc.concurrency}}")
        .routeId("save-cast-failures")
        .filter(PredicateBuilder.toPredicate(ExpressionBuilder.bodyExpression(AutoQcTestContextImpl.class, r -> r.getCastTestResult().isFailed())))
        .setHeader(Exchange.FILE_NAME, ExpressionBuilder.bodyExpression(AutoQcTestContextImpl.class, FileNameUtils::getCastFailureFileName))
        .setBody(ExpressionBuilder.bodyExpression(AutoQcTestContextImpl.class, AutoQcTestContextImpl::getCastTestResult)) //TODO better body?
        .marshal().json(JsonLibrary.Jackson)
        .to("file:{{autoqc.pickup-dir}}?charset=utf-8");
  }
}
