package edu.colorado.cires.wod.autoqc.processor;

import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import java.time.LocalDateTime;
import javax.validation.ValidationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DlqPrepProcessor implements Processor {


  private static final String DEFAULT_ERROR = "An error occurred during processing";

  private static final Logger LOGGER = LoggerFactory.getLogger(DlqPrepProcessor.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    Throwable cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);


    String event = cause.getMessage();
    if (StringUtils.isBlank(event)) {
      event = DEFAULT_ERROR;
    }
    String details = null;
    if (cause instanceof ValidationException) {
      event = "file failed qc validation";
      details = cause.getMessage();
    }
    if (StringUtils.isBlank(details)) {
      details = ExceptionUtils.getStackTrace(cause);
    }
    if (StringUtils.isBlank(details)) {
      details = DEFAULT_ERROR;
    }


    AutoQcTestContextImpl testContext = exchange.getIn().getBody(AutoQcTestContextImpl.class);
    testContext.getCastTestResult().setException(event + ":\n" + details);

    LOGGER.error("Error caught: {}", details);

  }
}
