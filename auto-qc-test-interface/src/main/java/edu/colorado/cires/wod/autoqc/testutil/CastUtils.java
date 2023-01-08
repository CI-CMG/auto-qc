package edu.colorado.cires.wod.autoqc.testutil;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CastUtils {

  public static AutoQcTestResult allGood(Cast cast) {
    return all(cast, false);
  }

  public static AutoQcTestResult allBad(Cast cast) {
    return all(cast, true);
  }

  public static AutoQcTestResult all(Cast cast, boolean failed) {
    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(cast.getDepths().size());
    for (int i = 0; i < cast.getDepths().size(); i++) {
      depthResults.add(() -> failed);
    }
    return () -> depthResults;
  }

  public static List<Depth> sortDepths(Cast cast) {
    return cast.getDepths().stream().sorted((d1, d2) -> Objects.compare(d1.getDepth(), d2.getDepth(), Double::compareTo)).collect(Collectors.toList());
  }

  private CastUtils () {

  }

}
