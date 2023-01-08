package du.colorado.cires.wod.autoqc.test.aomlclim;

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
    private Props aomlClimatology;

    public Props getAomlClimatology() {
      return aomlClimatology;
    }

    public void setAomlClimatology(Props aomlClimatology) {
      this.aomlClimatology = aomlClimatology;
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
