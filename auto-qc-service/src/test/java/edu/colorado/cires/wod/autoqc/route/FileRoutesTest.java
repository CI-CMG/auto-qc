package edu.colorado.cires.wod.autoqc.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.FileController;
import edu.colorado.cires.wod.autoqc.service.FileController.CastProcessingContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
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
@MockEndpointsAndSkip("seda:.*")
@ActiveProfiles("test")
public class FileRoutesTest {


  @EndpointInject("mock:seda:test-cast")
  private MockEndpoint testCastQueue;

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
  public void testConsumeFile() throws Exception {
    Path doneFile = Paths.get("src/test/resources/wod18/MRB/OBS/MRBO2022.gz.autoqc");
    try {
      testCastQueue.expectedMessageCount(3);
      Files.createFile(doneFile);
      testCastQueue.assertIsSatisfied();
      List<AutoQcTestContextImpl> messages = testCastQueue.getReceivedExchanges().stream()
          .map(e -> e.getIn().getBody(AutoQcTestContextImpl.class))
          .sorted(Comparator.comparing(tc -> tc.getCast().getCastNumber()))
          .collect(Collectors.toList());

      assertFalse(Files.exists(doneFile));
      assertTrue(Files.exists(Paths.get("src/test/resources/wod18/MRB/OBS/MRBO2022.gz")));

      assertEquals(21418924, messages.get(0).getCast().getCastNumber());
      assertEquals(21418925, messages.get(1).getCast().getCastNumber());
      assertEquals(21418926, messages.get(2).getCast().getCastNumber());

      assertEquals("MRB/OBS/MRBO2022", messages.get(0).getFilePathPrefix());
      assertEquals("MRB/OBS/MRBO2022", messages.get(1).getFilePathPrefix());
      assertEquals("MRB/OBS/MRBO2022", messages.get(2).getFilePathPrefix());

      assertFalse(messages.get(0).isLastCast());
      assertFalse(messages.get(1).isLastCast());
      assertTrue(messages.get(2).isLastCast());

      CastProcessingContext context = fileController.getContext("MRB/OBS/MRBO2022");
      assertEquals(new HashSet<>(Arrays.asList(21418924, 21418925, 21418926)), context.getCasts());
      assertTrue(context.isComplete());

    } finally {
      Files.deleteIfExists(doneFile);
    }
  }

}