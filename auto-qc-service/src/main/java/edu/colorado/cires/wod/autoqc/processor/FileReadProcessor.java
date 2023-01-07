package edu.colorado.cires.wod.autoqc.processor;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.reader.BufferedCharReader;
import edu.colorado.cires.mgg.wod.data.reader.CastFileReader;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.FileController;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileReadProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileReadProcessor.class);

  private final ProducerTemplate producerTemplate;
  private final FileController fileController;

  @Autowired
  public FileReadProcessor(ProducerTemplate producerTemplate, FileController fileController) {
    this.producerTemplate = producerTemplate;
    this.fileController = fileController;
  }

  public void process(File gzFile) throws Exception {
    // /wod/drop/CTD/OBS/CTDO1998.gz
    Path gzPath = gzFile.toPath().toAbsolutePath().normalize();
    LOGGER.info("Reading {}", gzPath);
    Path doneFile = gzPath.getParent().resolve(gzPath.getFileName().toString() + ".autoqc");
    Files.deleteIfExists(doneFile);
    // /wod/drop/CTD
    Path datasetDir = gzPath.getParent().getParent();
    // CTD
    String dataset = datasetDir.getFileName().toString();
    //   CTD/OBS/CTDO1998.gz        /wod/drop             /wod/drop/CTD/OBS/CTDO1998.gz
    String relativePath = datasetDir.getParent().relativize(gzPath).toString();



    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(gzFile.toPath()))))) {
      String filePathPrefix = relativePath.replaceAll("\\.gz$", "");

      CastFileReader reader = new CastFileReader(new BufferedCharReader(bufferedReader), dataset, relativePath);
      if (reader.hasNext()) {
        Path summaryFile = gzPath.getParent().resolve(FileNameUtils.getSummaryFileName(filePathPrefix));
        Files.deleteIfExists(summaryFile);
        fileController.onNewFile(filePathPrefix);
      }
      while (reader.hasNext()) {
        Cast cast = reader.next();
        boolean last = !reader.hasNext();

        AutoQcTestContextImpl testContext = new AutoQcTestContextImpl(filePathPrefix, last, cast);
        Path castFailFile = gzPath.getParent().resolve(FileNameUtils.getCastFailureFileName(testContext));
        Files.deleteIfExists(castFailFile);


        fileController.onNewCast(filePathPrefix, cast.getCastNumber(), last);
        producerTemplate.sendBody("seda:test-cast?blockWhenFull=true", testContext);
      }
    }
    LOGGER.info("Done reading {}", gzFile);
  }
}
