package edu.colorado.cires.wod.autoqc.route;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.reader.BufferedCharReader;
import edu.colorado.cires.mgg.wod.data.reader.CastFileReader;
import edu.colorado.cires.wod.autoqc.model.AutoQcCastTestResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcFileResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.FileController;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CamelSpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
@ActiveProfiles("test")
public class OutputRoutesTest {


  @Autowired
  private ProducerTemplate producerTemplate;

  @Autowired
  public FileController fileController;

  @EndpointInject("mock:file:{{autoqc.pickup-dir}}")
  private MockEndpoint pickupDirMock;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    fileController.reset();
  }

  @AfterEach
  public void teardown() {
    fileController.reset();
  }

  @Test
  public void testAggregateComplete() throws Exception {

    pickupDirMock.expectedMessageCount(1);

    Path summaryFile = Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020-summary.json");
    Files.deleteIfExists(summaryFile);

    try {
      try (BufferedReader bufferedReader =
          new BufferedReader(
              new InputStreamReader(
                  new GZIPInputStream(Files.newInputStream(Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020.gz")))))) {
        CastFileReader reader = new CastFileReader(new BufferedCharReader(bufferedReader), "CTD", "CTD/OBS/CTDO2020.gz");
        fileController.onNewFile("CTD/OBS/CTDO2020");
        while (reader.hasNext()) {
          Cast cast = reader.next();
          boolean last = !reader.hasNext();
          AutoQcTestContextImpl testContext = new AutoQcTestContextImpl("CTD/OBS/CTDO2020", last, cast);

          if (last) {
            testContext.getCastTestResult().getCastFailures().add("test1");
          }

          //TODO need to use a better error report object
          List<Set<String>> expected = new ArrayList<>();
          for (int i = 0; i < cast.getDepths().size(); i++) {
            expected.add(last ? Collections.emptySet() : Collections.singleton("test1"));
          }

          fileController.onNewCast("CTD/OBS/CTDO2020", cast.getCastNumber(), last);
          producerTemplate.sendBody("seda:test-aggregate?blockWhenFull=true", testContext);
        }
      }

      assertIsSatisfied(pickupDirMock);
      Thread.sleep(1000);

      assertTrue(Files.isRegularFile(summaryFile));

      AutoQcFileResult summary = objectMapper.readValue(summaryFile.toFile(), AutoQcFileResult.class);


      assertEquals(1, summary.getFailureCounts().size());
      assertEquals(3, summary.getTotalCasts());
      assertEquals("CTD/OBS/CTDO2020", summary.getFilePathPrefix());
      assertTrue(summary.isComplete());


    } finally {
      Files.deleteIfExists(summaryFile);
    }

  }

  @Test
  public void testError() throws Exception {
    pickupDirMock.expectedMessageCount(3);

    Path summaryFile = Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020-summary.json");
    Path failureFile1 = Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020-failures-20314995.json");
    Path failureFile2 = Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020-failures-20314996.json");
    Path failureFile3 = Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020-failures-20314997.json");
    Files.deleteIfExists(summaryFile);
    Files.deleteIfExists(failureFile1);
    Files.deleteIfExists(failureFile2);
    Files.deleteIfExists(failureFile3);

    try {
      try (BufferedReader bufferedReader =
          new BufferedReader(
              new InputStreamReader(
                  new GZIPInputStream(Files.newInputStream(Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020.gz")))))) {
        CastFileReader reader = new CastFileReader(new BufferedCharReader(bufferedReader), "CTD", "CTD/OBS/CTDO2020.gz");
        fileController.onNewFile("CTD/OBS/CTDO2020");
        while (reader.hasNext()) {
          Cast cast = reader.next();
          boolean last = !reader.hasNext();
          AutoQcTestContextImpl testContext = new AutoQcTestContextImpl("CTD/OBS/CTDO2020", last, cast);

          if (last) {
            //TODO need to use a better error report object
            List<Set<String>> expected = new ArrayList<>();
            for (int i = 0; i < cast.getDepths().size(); i++) {
              expected.add(Collections.emptySet());
            }
          } else {
            testContext.getCastTestResult().setException("Test Exception");
          }



          fileController.onNewCast("CTD/OBS/CTDO2020", cast.getCastNumber(), last);
          if (last) {
            producerTemplate.sendBody("seda:test-aggregate?blockWhenFull=true", testContext);
          } else {
            producerTemplate.sendBody("seda:error?blockWhenFull=true", testContext);
          }

        }
      }

      assertIsSatisfied(pickupDirMock);
      Thread.sleep(1000);

      assertTrue(Files.isRegularFile(summaryFile));
      assertTrue(Files.isRegularFile(failureFile1));
      assertTrue(Files.isRegularFile(failureFile2));

      AutoQcFileResult summary = objectMapper.readValue(summaryFile.toFile(), AutoQcFileResult.class);

      assertEquals(0, summary.getFailureCounts().size());
      assertEquals(3, summary.getTotalCasts());
      assertEquals(2, summary.getExceptions());
      assertEquals("CTD/OBS/CTDO2020", summary.getFilePathPrefix());
      assertTrue(summary.isComplete());


      AutoQcCastTestResult file1 = objectMapper.readValue(failureFile1.toFile(), AutoQcCastTestResult.class);
      AutoQcCastTestResult file2 = objectMapper.readValue(failureFile2.toFile(), AutoQcCastTestResult.class);

      assertEquals("Test Exception", file1.getException());
      assertEquals("Test Exception", file2.getException());

    } finally {
      Files.deleteIfExists(summaryFile);
      Files.deleteIfExists(failureFile1);
      Files.deleteIfExists(failureFile2);
      Files.deleteIfExists(failureFile3);
    }
  }


}