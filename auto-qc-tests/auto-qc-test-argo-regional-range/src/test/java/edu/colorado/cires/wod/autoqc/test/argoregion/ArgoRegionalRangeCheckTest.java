package edu.colorado.cires.wod.autoqc.test.argoregion;

import static edu.colorado.cires.mgg.wod.data.model.VariableConsts.TEMPERATURE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class ArgoRegionalRangeCheckTest {


  @Test
  public void testArgoRegionalRangeTestNotInRangeRed() throws Exception {
    Cast cast = Cast.builder()
        .setLatitude(30.540632)
        .setLongitude(34.705133)
        .setYear((short) 1900)
        .setMonth((short) 1)
        .setDay((short) 15)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(21.6)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(21.8)
                    .build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, false);

    ArgoRegionalRangeCheck test = new ArgoRegionalRangeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoRegionalRangeTestNotInRangeMed() throws Exception {
    Cast cast = Cast.builder()
        .setLatitude(43.808479)
        .setLongitude(7.445307)
        .setYear((short) 1900)
        .setMonth((short) 1)
        .setDay((short) 15)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(40.1)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(39.9)
                    .build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, false);

    ArgoRegionalRangeCheck test = new ArgoRegionalRangeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoRegionalRangeTestMediterraneanHot() throws Exception {
    Cast cast = Cast.builder()
        .setLatitude(35D)
        .setLongitude(18D)
        .setYear((short) 1900)
        .setMonth((short) 1)
        .setDay((short) 15)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(40.1)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(39.9)
                    .build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(true, false);

    ArgoRegionalRangeCheck test = new ArgoRegionalRangeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testArgoRegionalRangeTestRedCold() throws Exception {
    Cast cast = Cast.builder()
        .setLatitude(22D)
        .setLongitude(38D)
        .setYear((short) 1900)
        .setMonth((short) 1)
        .setDay((short) 15)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(21.6)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(21.8)
                    .build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(true, false);

    ArgoRegionalRangeCheck test = new ArgoRegionalRangeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

}