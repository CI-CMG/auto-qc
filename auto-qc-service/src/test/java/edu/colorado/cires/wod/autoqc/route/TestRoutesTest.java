package edu.colorado.cires.wod.autoqc.route;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.reader.BufferedCharReader;
import edu.colorado.cires.mgg.wod.data.reader.CastFileReader;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.FileController;
import edu.colorado.cires.wod.autoqc.service.TestSupplier;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
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
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CamelSpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("seda:save-cast-failures.*|seda:test-aggregate.*|seda:error.*")
@ActiveProfiles("test")
public class TestRoutesTest {

  @EndpointInject("mock:seda:save-cast-failures")
  private MockEndpoint saveCastFailuresQueue;

  @EndpointInject("mock:seda:test-aggregate")
  private MockEndpoint testAggregateQueue;

  @EndpointInject("mock:seda:error")
  private MockEndpoint errorQueue;

  @MockBean
  private TestSupplier testSupplier;

  @Autowired
  private ProducerTemplate producerTemplate;

  @Autowired
  public FileController fileController;

  @BeforeEach
  public void setup() {
    fileController.reset();
  }

  @AfterEach
  public void teardown() {
    fileController.reset();
  }

  @Test
  public void testError() throws Exception {
    when(testSupplier.getTests()).thenReturn(Arrays.asList(
        new AutoQcTest() {
          @Override
          public String getName() {
            return "test1";
          }

          @Override
          public AutoQcTestResult test(AutoQcTestContext context) {
            throw new RuntimeException("Test Exception");
          }
        }
    ));

    saveCastFailuresQueue.expectedMessageCount(0);
    saveCastFailuresQueue.setAssertPeriod(2000);
    testAggregateQueue.expectedMessageCount(0);
    testAggregateQueue.setAssertPeriod(2000);
    errorQueue.expectedMessageCount(3);


    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(Files.newInputStream(Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020.gz")))))) {

      CastFileReader reader = new CastFileReader(new BufferedCharReader(bufferedReader), "CTD", "CTD/OBS/CTDO2020.gz");

      while (reader.hasNext()) {
        Cast cast = reader.next();
        boolean last = !reader.hasNext();

        AutoQcTestContextImpl testContext = new AutoQcTestContextImpl("CTD/OBS/CTDO2020", last, cast);
        producerTemplate.sendBody("seda:test-cast?blockWhenFull=true", testContext);
      }
    }

    assertIsSatisfied(saveCastFailuresQueue, testAggregateQueue, errorQueue);

    List<AutoQcTestContextImpl> messages = errorQueue.getReceivedExchanges().stream()
        .map(e -> e.getIn().getBody(AutoQcTestContextImpl.class))
        .collect(Collectors.toList());

    for (AutoQcTestContextImpl message : messages) {
      assertEquals(Collections.emptySet(), message.getCastTestResult().getCastFailures());

      //TODO need to use a better error report object
      List<Set<String>> expected = new ArrayList<>();
      for (int i = 0; i < message.getCast().getDepths().size(); i++) {
        expected.add(Collections.emptySet());
      }

      assertEquals(expected, message.getCastTestResult().getDepthFailures());
      assertFalse(message.getCastTestResult().isSkipped());
      assertNull(message.getCastTestResult().getSkipReason());
      assertTrue(message.getCastTestResult().getException().startsWith("Test Exception"));
    }
  }

  @Test
  public void testNoFailures() throws Exception {
    when(testSupplier.getTests()).thenReturn(Arrays.asList(
        new AutoQcTest() {
          @Override
          public String getName() {
            return "test1";
          }

          @Override
          public AutoQcTestResult test(AutoQcTestContext context) {
            Cast cast = context.getCast();
            List<Depth> depths = cast.getDepths();
            List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
            for (Depth depth : depths) {
              depthResults.add(() -> false);
            }
            return () -> depthResults;
          }
        }
    ));

    saveCastFailuresQueue.expectedMessageCount(3);
    testAggregateQueue.expectedMessageCount(3);
    errorQueue.expectedMessageCount(0);
    errorQueue.setAssertPeriod(2000);

    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(Files.newInputStream(Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020.gz")))))) {

      CastFileReader reader = new CastFileReader(new BufferedCharReader(bufferedReader), "CTD", "CTD/OBS/CTDO2020.gz");

      while (reader.hasNext()) {
        Cast cast = reader.next();
        boolean last = !reader.hasNext();

        AutoQcTestContextImpl testContext = new AutoQcTestContextImpl("CTD/OBS/CTDO2020", last, cast);
        producerTemplate.sendBody("seda:test-cast?blockWhenFull=true", testContext);
      }
    }

    assertIsSatisfied(saveCastFailuresQueue, testAggregateQueue, errorQueue);

    List<AutoQcTestContextImpl> messages = saveCastFailuresQueue.getReceivedExchanges().stream()
        .map(e -> e.getIn().getBody(AutoQcTestContextImpl.class))
        .collect(Collectors.toList());

    List<AutoQcTestContextImpl> messages2 = testAggregateQueue.getReceivedExchanges().stream()
        .map(e -> e.getIn().getBody(AutoQcTestContextImpl.class))
        .collect(Collectors.toList());

    assertEquals(messages, messages2);

    for (AutoQcTestContextImpl message : messages) {
      assertEquals(Collections.emptySet(), message.getCastTestResult().getCastFailures());

      //TODO need to use a better error report object
      List<Set<String>> expected = new ArrayList<>();
      for (int i = 0; i < message.getCast().getDepths().size(); i++) {
        expected.add(Collections.emptySet());
      }

      assertEquals(expected, message.getCastTestResult().getDepthFailures());
      assertFalse(message.getCastTestResult().isSkipped());
      assertNull(message.getCastTestResult().getSkipReason());
      assertNull(message.getCastTestResult().getException());
    }
  }

  @Test
  public void testFailures() throws Exception {
    when(testSupplier.getTests()).thenReturn(Arrays.asList(
        new AutoQcTest() {
          @Override
          public String getName() {
            return "test1";
          }

          @Override
          public AutoQcTestResult test(AutoQcTestContext context) {
            Cast cast = context.getCast();
            List<Depth> depths = cast.getDepths();
            List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
            for (Depth depth : depths) {
              depthResults.add(() -> true);
            }
            return () -> depthResults;
          }
        }
    ));

    saveCastFailuresQueue.expectedMessageCount(3);
    testAggregateQueue.expectedMessageCount(3);
    errorQueue.expectedMessageCount(0);
    errorQueue.setAssertPeriod(2000);

    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(Files.newInputStream(Paths.get("src/test/resources/wod18/CTD/OBS/CTDO2020.gz")))))) {

      CastFileReader reader = new CastFileReader(new BufferedCharReader(bufferedReader), "CTD", "CTD/OBS/CTDO2020.gz");

      while (reader.hasNext()) {
        Cast cast = reader.next();
        boolean last = !reader.hasNext();

        AutoQcTestContextImpl testContext = new AutoQcTestContextImpl("CTD/OBS/CTDO2020", last, cast);
        producerTemplate.sendBody("seda:test-cast?blockWhenFull=true", testContext);
      }
    }

    assertIsSatisfied(saveCastFailuresQueue, testAggregateQueue, errorQueue);

    List<AutoQcTestContextImpl> messages = saveCastFailuresQueue.getReceivedExchanges().stream()
        .map(e -> e.getIn().getBody(AutoQcTestContextImpl.class))
        .collect(Collectors.toList());

    List<AutoQcTestContextImpl> messages2 = testAggregateQueue.getReceivedExchanges().stream()
        .map(e -> e.getIn().getBody(AutoQcTestContextImpl.class))
        .collect(Collectors.toList());

    assertEquals(messages, messages2);

    for (AutoQcTestContextImpl message : messages) {
      assertEquals(Collections.singleton("test1"), message.getCastTestResult().getCastFailures());

      //TODO need to use a better error report object
      List<Set<String>> expected = new ArrayList<>();
      for (int i = 0; i < message.getCast().getDepths().size(); i++) {
        expected.add(Collections.singleton("test1"));
      }

      assertEquals(expected, message.getCastTestResult().getDepthFailures());
      assertFalse(message.getCastTestResult().isSkipped());
      assertNull(message.getCastTestResult().getSkipReason());
      assertNull(message.getCastTestResult().getException());
    }


  }
}