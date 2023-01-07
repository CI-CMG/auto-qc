package edu.colorado.cires.wod.autoqc.model;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class AutoQcCastTestResult {

  private final Set<String> castFailures = new LinkedHashSet<>();
  private List<Set<String>> depthFailures;
  private boolean skipped;
  private String skipReason;
  private String exception;

  // For Jackson deserialization
  private AutoQcCastTestResult() {

  }

  public AutoQcCastTestResult(Cast cast) {
    depthFailures = new ArrayList<>(cast.getDepths().size());
    for (int i = 0; i < cast.getDepths().size(); i++) {
      depthFailures.add(new LinkedHashSet<>());
    }
  }

  // For Jackson deserialization
  private void setDepthFailures(List<Set<String>> depthFailures) {
    this.depthFailures = depthFailures;
  }

  public List<Set<String>> getDepthFailures() {
    return depthFailures;
  }

  public Set<String> getCastFailures() {
    return castFailures;
  }

  public boolean isFailed() {
    return !castFailures.isEmpty() || exception != null;
  }

  public boolean isSkipped() {
    return skipped;
  }

  public void setSkipped(boolean skipped) {
    this.skipped = skipped;
  }

  public String getSkipReason() {
    return skipReason;
  }

  public void setSkipReason(String skipReason) {
    this.skipReason = skipReason;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }
}
