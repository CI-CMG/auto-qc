package edu.colorado.cires.wod.autoqc.config;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Configuration
@ConfigurationProperties("autoqc")
public class ServiceProperties {

  @NotBlank
  private String dataDir;

  @NotBlank
  private String pickupDir;

  @NotNull
  @Min(1)
  private Integer concurrency;

  public Integer getConcurrency() {
    return concurrency;
  }

  public void setConcurrency(Integer concurrency) {
    this.concurrency = concurrency;
  }

  public String getPickupDir() {
    return pickupDir;
  }

  public void setPickupDir(String pickupDir) {
    this.pickupDir = pickupDir;
  }

  public String getDataDir() {
    return dataDir;
  }

  public void setDataDir(String dataDir) {
    this.dataDir = dataDir;
  }
}
