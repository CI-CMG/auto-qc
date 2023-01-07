package edu.colorado.cires.wod.autoqc.processor;

import edu.colorado.cires.wod.autoqc.model.AutoQcFileResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;

public final class FileNameUtils {

  public static String getCastFailureFileName(AutoQcTestContextImpl r) {
    return r.getFilePathPrefix() + "-failures-" + r.getCast().getCastNumber() + ".json";
  }

  public static String getSummaryFileName(AutoQcFileResult r) {
    return getSummaryFileName(r.getFilePathPrefix());
  }

  public static String getSummaryFileName(String prefix) {
    return prefix + "-summary.json";
  }

  private FileNameUtils() {

  }

}
