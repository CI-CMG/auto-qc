package edu.colorado.cires.wod.autoqc.model;

import java.util.HashMap;
import java.util.Map;

public class AutoQcFileResult {

  private final Map<String, Integer> failureCounts = new HashMap<>();
  private int totalCasts = 0;
  private String filePathPrefix;
  private boolean complete = false;
  private String exception;
  private int exceptions;

  // For Jackson deserialization
  private AutoQcFileResult() {

  }

  public AutoQcFileResult(String filePathPrefix) {
    this.filePathPrefix = filePathPrefix;
  }

  // For Jackson deserialization
  private void setFilePathPrefix(String filePathPrefix) {
    this.filePathPrefix = filePathPrefix;
  }

  public String getFilePathPrefix() {
    return filePathPrefix;
  }

  // For Jackson deserialization
  private void setTotalCasts(int totalCasts) {
    this.totalCasts = totalCasts;
  }

  // For Jackson deserialization
  private void setComplete(boolean complete) {
    this.complete = complete;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
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

  public int getExceptions() {
    return exceptions;
  }

  public void setExceptions(int exceptions) {
    this.exceptions = exceptions;
  }
}
