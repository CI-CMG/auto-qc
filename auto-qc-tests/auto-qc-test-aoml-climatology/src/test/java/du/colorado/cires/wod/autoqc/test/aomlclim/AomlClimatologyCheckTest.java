package du.colorado.cires.wod.autoqc.test.aomlclim;

import static edu.colorado.cires.mgg.wod.data.model.VariableConsts.TEMPERATURE;
import static org.junit.jupiter.api.Assertions.*;

import du.colorado.cires.wod.autoqc.test.aomlclim.QcProperties.Props;
import du.colorado.cires.wod.autoqc.test.aomlclim.QcProperties.TestConfig;
import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class AomlClimatologyCheckTest {

  private final QcProperties properties;
  private final String dataDir = "../../test-data";
  private final RestTemplate restTemplate = new RestTemplate();

  public AomlClimatologyCheckTest() {
    Props props = new Props();
    props.setNetCdfUrl("https://auto-qc-data.s3.us-west-2.amazonaws.com/woa13_00_025.nc");
    props.setNetCdfFileName("woa13_00_025.nc");
    TestConfig testConfig = new TestConfig();
    testConfig.setAomlClimatology(props);
    this.properties = new QcProperties();
    this.properties.setTest(testConfig);
  }

  //TODO
  @Disabled
  @Test
  public void test() throws Exception {
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

    AomlClimatologyCheck test = new AomlClimatologyCheck(properties, dataDir, restTemplate);
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }
}