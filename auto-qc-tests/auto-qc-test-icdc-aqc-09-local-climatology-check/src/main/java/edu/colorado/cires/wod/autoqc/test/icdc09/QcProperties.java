package edu.colorado.cires.wod.autoqc.test.icdc09;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Configuration
@ConfigurationProperties("autoqc")
class QcProperties {

  @NotNull
  @Valid
  private TestConfig test;

  public TestConfig getTest() {
    return test;
  }

  public void setTest(TestConfig test) {
    this.test = test;
  }

  public static class TestConfig {
    @NotNull
    @Valid
    private Props icdcAqc09LocalClimatologyCheck;

    public Props getIcdcAqc09LocalClimatologyCheck() {
      return icdcAqc09LocalClimatologyCheck;
    }

    public void setIcdcAqc09LocalClimatologyCheck(Props icdcAqc09LocalClimatologyCheck) {
      this.icdcAqc09LocalClimatologyCheck = icdcAqc09LocalClimatologyCheck;
    }
  }

  public static class Props {
    @NotBlank
    private String netCdfUrl;
    @NotBlank
    private String netCdfFileName;

    public String getNetCdfUrl() {
      return netCdfUrl;
    }

    public void setNetCdfUrl(String netCdfUrl) {
      this.netCdfUrl = netCdfUrl;
    }

    public String getNetCdfFileName() {
      return netCdfFileName;
    }

    public void setNetCdfFileName(String netCdfFileName) {
      this.netCdfFileName = netCdfFileName;
    }
  }

}
