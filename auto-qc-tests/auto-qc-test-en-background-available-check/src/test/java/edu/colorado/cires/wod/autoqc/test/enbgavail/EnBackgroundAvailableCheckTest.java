package edu.colorado.cires.wod.autoqc.test.enbgavail;

import static edu.colorado.cires.mgg.wod.data.model.VariableConsts.TEMPERATURE;
import static org.junit.jupiter.api.Assertions.*;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.test.enbgavail.QcProperties.EnBgAvailCheck;
import edu.colorado.cires.wod.autoqc.test.enbgavail.QcProperties.TestConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class EnBackgroundAvailableCheckTest {

  /*
import qctests.EN_background_available_check
import qctests.EN_background_check
from util import main
import util.testingProfile
import numpy

##### EN_background_check ---------------------------------------------------

class TestClass:

    parameters = {
        'db': 'iquod.db',
        "table": 'unit'
    }
    qctests.EN_background_check.loadParameters(parameters)

    def setUp(self):
        # this qc test will go looking for the profile in question in the db, needs to find something sensible
        main.faketable('unit')
        main.fakerow('unit')

    def tearDown(self):
        main.dbinteract('DROP TABLE unit;')

    def test_EN_background_available_check_depth(self):
        '''
        Make sure EN_background_check is flagging depths where the background is not defined.
        '''

        p = util.testingProfile.fakeProfile([1.8, 1.8, 1.8, 1.8], [0.0, 2.5, 5.0, 5600.0], latitude=55.6, longitude=12.9, date=[1900, 1, 15, 0], probe_type=7)
        qc = qctests.EN_background_available_check.test(p, self.parameters)
        expected = [False, False, False, True]
        assert numpy.array_equal(qc, expected), 'mismatch between qc results and expected values'

    def test_EN_background_available_check_location(self):
        '''
        Make sure EN_background_check is flagging land locations.
        '''

        p = util.testingProfile.fakeProfile([1.8, 1.8, 1.8, 1.8], [0.0, 2.5, 5.0, 7.5], latitude=0.0, longitude=20.0, date=[1900, 1, 15, 0], probe_type=7)
        qc = qctests.EN_background_available_check.test(p, self.parameters)
        expected = [True, True, True, True]
        assert numpy.array_equal(qc, expected), 'mismatch between qc results and expected values'
   */

  private final QcProperties properties;
  private final String dataDir = "../../test-data";
  private final RestTemplate restTemplate = new RestTemplate();

  public EnBackgroundAvailableCheckTest() {
    EnBgAvailCheck props = new EnBgAvailCheck();
    props.setInfoNetCdfUrl("https://www.metoffice.gov.uk/hadobs/en4/data/EN_bgcheck_info.nc");
    props.setInfoNetCdfFileName("EN_bgcheck_info.nc");
    TestConfig testConfig = new TestConfig();
    testConfig.setEnBgAvailCheck(props);
    this.properties = new QcProperties();
    this.properties.setTest(testConfig);
  }


  @Test
  public void testEnBackgroundAvailableCheckDepth() throws Exception {
    Cast cast = Cast.builder()
        .setLatitude(55.6)
        .setLongitude(12.9)
        .setYear((short)1900)
        .setMonth((short)1)
        .setDay((short)15)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(0.0)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8)
                    .build()))
                .build(),
            Depth.builder().setDepth(2.5)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8).build()))
                .build(),
            Depth.builder().setDepth(5.0)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8).build()))
                .build(),
            Depth.builder().setDepth(5600.0)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(
        false, false, false, true
    );

    EnBackgroundAvailableCheck test = new EnBackgroundAvailableCheck(properties, dataDir, restTemplate);
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }


  @Test
  public void testEnBackgroundAvailableCheckLocation() throws Exception {
    Cast cast = Cast.builder()
        .setLatitude(0D)
        .setLongitude(20D)
        .setYear((short)1900)
        .setMonth((short)1)
        .setDay((short)15)
        .setTime(0D)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Arrays.asList(
            Depth.builder().setDepth(0.0)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8)
                    .build()))
                .build(),
            Depth.builder().setDepth(2.5)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8).build()))
                .build(),
            Depth.builder().setDepth(5.0)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8).build()))
                .build(),
            Depth.builder().setDepth(7.5)
                .setData(Collections.singletonList(ProfileData.builder()
                    .setOriginatorsFlag(0).setQcFlag(0)
                    .setVariable(TEMPERATURE).setValue(1.8).build()))
                .build()
        ))
        .build();

    List<Boolean> expected = Arrays.asList(
        true, true, true, true
    );

    EnBackgroundAvailableCheck test = new EnBackgroundAvailableCheck(properties, dataDir, restTemplate);
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }
}