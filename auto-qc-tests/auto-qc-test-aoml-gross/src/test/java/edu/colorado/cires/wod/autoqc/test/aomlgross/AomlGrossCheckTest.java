package edu.colorado.cires.wod.autoqc.test.aomlgross;

import static edu.colorado.cires.mgg.wod.data.model.VariableConsts.TEMPERATURE;
import static org.junit.jupiter.api.Assertions.*;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;

public class AomlGrossCheckTest {

  /*
    p = util.testingProfile.fakeProfile([40.0000001, 40, 30, 20, 10, 0, -2.5, -2.5000001], [0, 10, -10, 20, 3000, 40, 50, 60])
    qc = qctests.AOML_gross.test(p, None)
    truth = numpy.zeros(8, dtype=bool)
    truth[0] = True
    truth[2] = True
    truth[4] = True
    truth[7] = True
    assert numpy.array_equal(qc, truth), 'incorrectly temperature and depth ranges.'

        def __init__(self, temperatures, depths, latitude=None, longitude=None, date=[1999, 12, 31, 0], probe_type=None, salinities=None, pressures=None, uid=None, cruise=None, qcflag=False):

   */

  @Test
  public void test() throws Exception {

    //TODO update builders to make building sparse casts easier and normalize either primitives or wrappers

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
                    .setVariable(TEMPERATURE).setValue(40.0000001)
                    .build()))
                .build(),
            Depth.builder().setDepth(10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(40D).build()))
                .build(),
            Depth.builder().setDepth(-10D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(30D).build()))
                .build(),
            Depth.builder().setDepth(20D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(20D).build()))
                .build(),
            Depth.builder().setDepth(3000D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(10D).build()))
                .build(),
            Depth.builder().setDepth(40D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(0D).build()))
                .build(),
            Depth.builder().setDepth(50D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(-2.5).build()))
                .build(),
            Depth.builder().setDepth(60D)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(-2.5000001).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(
        true, false, true, false, true, false, false, true
    );

    AomlGrossCheck test = new AomlGrossCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }
}
