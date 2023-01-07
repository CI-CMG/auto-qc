package edu.colorado.cires.wod.autoqc.model;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;

public class AutoQcTestContextImpl implements AutoQcTestContext {

  private final Cast cast;
  private final AutoQcCastTestResult castTestResult;
  private final String filePathPrefix;
  private final boolean lastCast;

  public AutoQcTestContextImpl(String filePathPrefix, boolean lastCast, Cast cast) {
    this.filePathPrefix = filePathPrefix;
    this.cast = cast;
    castTestResult = new AutoQcCastTestResult(cast);
    this.lastCast = lastCast;
  }

  @Override
  public Cast getCast() {
    return cast;
  }

  public AutoQcCastTestResult getCastTestResult() {
    return castTestResult;
  }

  public String getFilePathPrefix() {
    return filePathPrefix;
  }

  public boolean isLastCast() {
    return lastCast;
  }
}
