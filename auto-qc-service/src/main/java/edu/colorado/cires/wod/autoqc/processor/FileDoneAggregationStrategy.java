package edu.colorado.cires.wod.autoqc.processor;

import edu.colorado.cires.wod.autoqc.model.AutoQcFileResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.FileController;
import java.util.Map;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileDoneAggregationStrategy implements AggregationStrategy, Predicate {

  private final FileController fileController;

  @Autowired
  public FileDoneAggregationStrategy(FileController fileController) {
    this.fileController = fileController;
  }

  private void updateCounts(AutoQcFileResult fileResult, AutoQcTestContextImpl testContext) {
    fileResult.incrementTotalCasts();
    if(fileController.onDoneCast(testContext.getFilePathPrefix(), testContext.getCast().getCastNumber())) {
      fileResult.setComplete();
    }
    Map<String, Integer> failureCounts = fileResult.getFailureCounts();
    for (String testName : testContext.getCastTestResult().getCastFailures()) {
      Integer count = failureCounts.get(testName);
      if (count == null) {
        count = 0;
      }
      failureCounts.put(testName, count + 1);
    }
    if (testContext.getCastTestResult().getException() != null) {
      fileResult.setExceptions(fileResult.getExceptions() + 1);
    }
  }

  @Override
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    AutoQcTestContextImpl testContext = newExchange.getIn().getBody(AutoQcTestContextImpl.class);
    if (oldExchange == null) {
      AutoQcFileResult fileResult = new AutoQcFileResult(testContext.getFilePathPrefix());
      updateCounts(fileResult, testContext);
      newExchange.getIn().setBody(fileResult);
      return newExchange;
    } else {
      AutoQcFileResult fileResult = oldExchange.getIn().getBody(AutoQcFileResult.class);
      updateCounts(fileResult, testContext);
      return oldExchange;
    }
  }

  @Override
  public boolean matches(Exchange exchange) {
    AutoQcFileResult fileResult = exchange.getIn().getBody(AutoQcFileResult.class);
    return fileResult.isComplete();
  }
}
