package edu.colorado.cires.wod.autoqc.test.aomlspike;

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

public class AomlSpikeCheckTest {

  /*
  def test_AOML_spike():
    '''
    general behavior test for spike function
    '''

    assert qctests.AOML_spike.spike(numpy.ma.MaskedArray([1,2,3], [0,0,0])) == False, 'flagged an obviously smooth point'
    assert qctests.AOML_spike.spike(numpy.ma.MaskedArray([1,200,3], [0,0,0])), 'failed to flag an obvious spike'
    assert qctests.AOML_spike.spike(numpy.ma.MaskedArray([1,200,3],[0,0,1])) == False, 'failed to account for missing value correctly'
    assert qctests.AOML_spike.spike(numpy.ma.MaskedArray([1,-200,3], [0,0,0])), 'failed to flag an obvious negative spike'

def test_AOML_spike_slice():
    '''
    AOML_spike is supposed to consider a 3-level series at the boundaries of the profile,
    and a 5-level series everywhere else.
    '''

    p = util.testingProfile.fakeProfile([20,199,200,201,16],[1,2,3,4,5])
    qc = qctests.AOML_spike.test(p, None)
    truth = numpy.zeros(5, dtype=bool)
    truth[2] = True
    truth[3] = True
    assert numpy.array_equal(qc, truth), 'mishandled spikes on interior of profile'

    p = util.testingProfile.fakeProfile([20,190,18,18,18,170,16],[1,2,3,4,5,5,7])
    qc = qctests.AOML_spike.test(p, None)
    truth = numpy.zeros(7, dtype=bool)
    truth[1] = True
    truth[5] = True
    print(qc)
    print(truth)
    assert numpy.array_equal(qc, truth), 'failed to flag spikes near edges of profile'

   */

  @Test
  public void testAomlSpikeSmooth() throws Exception {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(0D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1D)
                    .build()))
                .build(),
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(2D)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(3D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, false, false);

    AomlSpikeCheck test = new AomlSpikeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testAomlSpikeHasSpike() throws Exception {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(0D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1D)
                    .build()))
                .build(),
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(200D)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(3D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, true, false);

    AomlSpikeCheck test = new AomlSpikeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testAomlSpikeMissingTemp() throws Exception {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(0D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1D)
                    .build()))
                .build(),
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(200D)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D).build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, false, false);

    AomlSpikeCheck test = new AomlSpikeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testAomlSpikeHasNegativeSpike() throws Exception {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(0D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1D)
                    .build()))
                .build(),
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(-200D)
                    .build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(3D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, true, false);

    AomlSpikeCheck test = new AomlSpikeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  /*
     p = util.testingProfile.fakeProfile([20,190,18,18,18,170,16],[1,2,3,4,5,5,7])
    qc = qctests.AOML_spike.test(p, None)
    truth = numpy.zeros(7, dtype=bool)
    truth[1] = True
    truth[5] = True
    print(qc)
    print(truth)
   */

  @Test
  public void testAomlSpikeSlice1() throws Exception {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(1D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(20D)
                    .build()))
                .build(),
            Depth.builder().setDepth(2D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(199D)
                    .build()))
                .build(),
            Depth.builder().setDepth(3D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(200D).build()))
                .build(),
            Depth.builder().setDepth(4D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(201D).build()))
                .build(),
            Depth.builder().setDepth(5D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(16D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, false, true, true, false);

    AomlSpikeCheck test = new AomlSpikeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

  @Test
  public void testAomlSpikeSlice2() throws Exception {
    Cast cast = Cast.builder()
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(1D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(20D)
                    .build()))
                .build(),
            Depth.builder().setDepth(2D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(190D)
                    .build()))
                .build(),
            Depth.builder().setDepth(3D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(18D).build()))
                .build(),
            Depth.builder().setDepth(4D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(18D).build()))
                .build(),
            Depth.builder().setDepth(5D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(18D).build()))
                .build(),
            Depth.builder().setDepth(6D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(170D).build()))
                .build(),
            Depth.builder().setDepth(7D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(16D).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(false, true, false, false, false, true, false);

    AomlSpikeCheck test = new AomlSpikeCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }
}