package edu.colorado.cires.wod.autoqc.test.aomlgradient;

import static edu.colorado.cires.mgg.wod.data.model.VariableConsts.TEMPERATURE;
import static org.junit.jupiter.api.Assertions.*;

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

public class AomlGradientCheckTest {

  /*
  def test_AOML_gradient_boundaries():
    '''
    Test critical values in AOML check
    unphysical giant numbers to avoid some floating point errors
    '''

    p = util.testingProfile.fakeProfile([500000,400000,299999], [100000,200000,300000])
    qc = qctests.AOML_gradient.test(p, None)
    truth = numpy.zeros(3, dtype=bool)
    truth[1] = True
    truth[2] = True
    assert numpy.array_equal(qc, truth), 'incorrectly flagging boundaries of decreasing temperature gradient.'

    p = util.testingProfile.fakeProfile([480000,500000,520000], [100000,200000,299999])
    qc = qctests.AOML_gradient.test(p, None)
    truth = numpy.zeros(3, dtype=bool)
    truth[1] = True
    truth[2] = True
    assert numpy.array_equal(qc, truth), 'incorrectly flagging boundaries of increasing temperature gradient.'

def test_AOML_gradient_edge():
    '''
    check the edge case pointed out in
    https://github.com/IQuOD/AutoQC/pull/228
    '''

    p = util.testingProfile.fakeProfile([1.8,1], [2,1])
    qc = qctests.AOML_gradient.test(p, None)
    truth = numpy.zeros(2, dtype=bool)
    assert numpy.array_equal(qc, truth), 'flagged a moderate gradient even though temperature was decreasing'
   */

  @Test
  public void testAomlGradientBoundaries1() {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(100000D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(500000D)
                    .build()))
                .build(),
            Depth.builder().setDepth(200000D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(400000D).build()))
                .build(),
            Depth.builder().setDepth(300000D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(299999D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, true, true);

    AomlGradientCheck test = new AomlGradientCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testAomlGradientBoundaries2() {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(100000D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(480000D)
                    .build()))
                .build(),
            Depth.builder().setDepth(200000D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(500000D).build()))
                .build(),
            Depth.builder().setDepth(299999D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(520000D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, true, true);

    AomlGradientCheck test = new AomlGradientCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testAomlGradientEdge() {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(2D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8)
                    .build()))
                .build(),
            Depth.builder().setDepth(1D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, false);

    AomlGradientCheck test = new AomlGradientCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

}