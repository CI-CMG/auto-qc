package edu.colorado.cires.wod.autoqc.test;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.aoml-gross", name = "enabled", havingValue = "true")
public class AomlGrossTest implements AutoQcTest {

  @Override
  public String getName() {
    return "AOML_gross";
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {
    Cast cast = context.getCast();
    List<Depth> depths = cast.getDepths();
    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
    for (Depth depth : depths) {

      boolean failed = false;

      if (depth.getDepth() != null) {
        failed = depth.getDepth() < 0 || depth.getDepth() > 2000; //TODO get from properties
      }

      if (!failed) {
        failed = depth.getTemperature()
            .map(ProfileData::getValue)
            .map(temp -> temp > -2.5 && temp < 40) //TODO get from properties
            .orElse(false);
      }

      final boolean f = failed;
      depthResults.add(() -> f);

    }

    return () -> depthResults;
  }
}
