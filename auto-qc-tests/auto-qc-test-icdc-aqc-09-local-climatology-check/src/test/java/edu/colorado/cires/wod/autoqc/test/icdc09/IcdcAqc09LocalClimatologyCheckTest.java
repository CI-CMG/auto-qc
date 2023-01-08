package edu.colorado.cires.wod.autoqc.test.icdc09;

import edu.colorado.cires.wod.autoqc.test.icdc09.QcProperties.Props;
import edu.colorado.cires.wod.autoqc.test.icdc09.QcProperties.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class IcdcAqc09LocalClimatologyCheckTest {

  private final QcProperties properties;
  private final String dataDir = "../../test-data";
  private final RestTemplate restTemplate = new RestTemplate();

  public IcdcAqc09LocalClimatologyCheckTest() {
    Props props = new Props();
    props.setNetCdfUrl("https://s3-us-west-2.amazonaws.com/autoqc/climatological_t_median_and_amd_for_aqc.nc");
    props.setNetCdfFileName("climatological_t_median_and_amd_for_aqc.nc");
    TestConfig testConfig = new TestConfig();
    testConfig.setIcdcAqc09LocalClimatologyCheck(props);
    this.properties = new QcProperties();
    this.properties.setTest(testConfig);
  }

  @Test
  public void testHappyPath() throws Exception {
    IcdcAqc09LocalClimatologyCheck test = new IcdcAqc09LocalClimatologyCheck(properties, dataDir, restTemplate);
  }
}