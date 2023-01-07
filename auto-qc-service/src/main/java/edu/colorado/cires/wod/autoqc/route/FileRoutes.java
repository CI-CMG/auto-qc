package edu.colorado.cires.wod.autoqc.route;

import edu.colorado.cires.wod.autoqc.processor.FileReadProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileRoutes extends RouteBuilder {

  private final FileReadProcessor fileReadProcessor;

  @Autowired
  public FileRoutes(FileReadProcessor fileReadProcessor) {
    this.fileReadProcessor = fileReadProcessor;
  }

  @Override
  public void configure() throws Exception {
    from("file:{{autoqc.pickup-dir}}"
        + "?recursive=true"
        + "&includeExt=gz"
        + "&doneFileName=${file:name}.autoqc"
        + "&noop=true"
        + "&idempotentKey=${file:name}-${date:now}")
        .bean(fileReadProcessor);
  }
}
