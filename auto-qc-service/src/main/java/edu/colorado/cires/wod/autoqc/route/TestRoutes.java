package edu.colorado.cires.wod.autoqc.route;

import edu.colorado.cires.wod.autoqc.processor.DlqPrepProcessor;
import edu.colorado.cires.wod.autoqc.processor.FileDoneAggregationStrategy;
import edu.colorado.cires.wod.autoqc.processor.TestProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestRoutes extends RouteBuilder {

  private final TestProcessor testProcessor;
  private final FileDoneAggregationStrategy fileDoneAggregationStrategy;
  private final DlqPrepProcessor dlqPrepProcessor;

  @Autowired
  public TestRoutes(TestProcessor testProcessor, FileDoneAggregationStrategy fileDoneAggregationStrategy, DlqPrepProcessor dlqPrepProcessor) {
    this.testProcessor = testProcessor;
    this.fileDoneAggregationStrategy = fileDoneAggregationStrategy;
    this.dlqPrepProcessor = dlqPrepProcessor;
  }

  @Override
  public void configure() throws Exception {

    errorHandler(deadLetterChannel("seda:error?blockWhenFull=true").onPrepareFailure(dlqPrepProcessor));

    from("seda:test-cast?concurrentConsumers={{autoqc.concurrency}}")
        .routeId("test-cast")
        .bean(testProcessor)
        .to("seda:save-cast-failures?blockWhenFull=true")
        .to("seda:test-aggregate?blockWhenFull=true");


  }
}
