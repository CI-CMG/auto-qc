package edu.colorado.cires.wod.autoqc.test.argodate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class ArgoImpossibleDateCheckTest {

  @Test
  public void testArgoImpossibleDateTestYear() throws Exception {
    Cast cast = Cast.builder()
        .setYear((short) 1699)
        .setMonth((short) 1)
        .setDay((short) 1)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(true);

    ArgoImpossibleDateCheck test = new ArgoImpossibleDateCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoImpossibleDateTestMonth() throws Exception {
    Cast cast = Cast.builder()
        .setYear((short) 2001)
        .setMonth((short) 0)
        .setDay((short) 1)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(true);

    ArgoImpossibleDateCheck test = new ArgoImpossibleDateCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoImpossibleDateTestDayBasic() throws Exception {
    Cast cast = Cast.builder()
        .setYear((short) 2001)
        .setMonth((short) 2)
        .setDay((short) 29)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(true);

    ArgoImpossibleDateCheck test = new ArgoImpossibleDateCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoImpossibleDateTestDayLeapYear() throws Exception {
    Cast cast = Cast.builder()
        .setYear((short) 2004)
        .setMonth((short) 2)
        .setDay((short) 29)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(false);

    ArgoImpossibleDateCheck test = new ArgoImpossibleDateCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoImpossibleDateTestHour() throws Exception {
    Cast cast = Cast.builder()
        .setYear((short) 2004)
        .setMonth((short) 2)
        .setDay((short) 29)
        .setTime(24D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(true);

    ArgoImpossibleDateCheck test = new ArgoImpossibleDateCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoImpossibleDateTestHourMissing() throws Exception {
    Cast cast = Cast.builder()
        .setYear((short) 2004)
        .setMonth((short) 1)
        .setDay((short) 29)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(false);

    ArgoImpossibleDateCheck test = new ArgoImpossibleDateCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }
}