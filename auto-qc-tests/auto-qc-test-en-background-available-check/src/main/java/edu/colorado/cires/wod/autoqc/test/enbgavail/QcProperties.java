package edu.colorado.cires.wod.autoqc.test.enbgavail;

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
    private EnBgAvailCheck enBgAvailCheck;

    public EnBgAvailCheck getEnBgAvailCheck() {
      return enBgAvailCheck;
    }

    public void setEnBgAvailCheck(EnBgAvailCheck enBgAvailCheck) {
      this.enBgAvailCheck = enBgAvailCheck;
    }
  }

  public static class EnBgAvailCheck {
    @NotBlank
    private String infoNetCdfUrl;
    @NotBlank
    private String infoNetCdfFileName;

    public String getInfoNetCdfUrl() {
      return infoNetCdfUrl;
    }

    public void setInfoNetCdfUrl(String infoNetCdfUrl) {
      this.infoNetCdfUrl = infoNetCdfUrl;
    }

    public String getInfoNetCdfFileName() {
      return infoNetCdfFileName;
    }

    public void setInfoNetCdfFileName(String infoNetCdfFileName) {
      this.infoNetCdfFileName = infoNetCdfFileName;
    }
  }

}
