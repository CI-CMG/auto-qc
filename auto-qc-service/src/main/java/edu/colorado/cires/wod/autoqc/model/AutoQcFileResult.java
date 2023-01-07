package edu.colorado.cires.wod.autoqc.model;

import java.util.HashMap;
import java.util.Map;

public class AutoQcFileResult {

  private final Map<String, Integer> failureCounts = new HashMap<>();
  private int totalCasts = 0;
  private final String filePathPrefix;
  private boolean complete = false;

  public AutoQcFileResult(String filePathPrefix) {
    this.filePathPrefix = filePathPrefix;
  }

  public String getFilePathPrefix() {
    return filePathPrefix;
  }

  public Map<String, Integer> getFailureCounts() {
    return failureCounts;
  }

  public int getTotalCasts() {
    return totalCasts;
  }

  public void incrementTotalCasts() {
    totalCasts++;
  }

  public boolean isComplete() {
    return complete;
  }

  public void setComplete() {
    complete = true;
  }
}
