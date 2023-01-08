package edu.colorado.cires.wod.autoqc.test.aomlgradient;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.aoml-gradient", name = "enabled", havingValue = "true")
public class AomlGradientCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AomlGradientCheck.class);

  @Override
  public String getName() {
    return "AOML_gradient";
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }

    Cast cast = context.getCast();
    List<Depth> depths = cast.getDepths();
    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
    for (int i = 0; i < depths.size(); i++) {
      depthResults.add(() -> false);
    }

    for (int i = 0; i < depths.size() - 1; i++) {
      Depth d1 = depths.get(i);
      Depth d2 = depths.get(i + 1);
      if (d1.getTemperature().isPresent() && d2.getTemperature().isPresent() && d1.getDepth() != null && d2.getDepth() != null) {
        double dz = d2.getDepth() - d1.getDepth();
        if (Precision.equals(0D, dz)) {
          continue;
        }
        double td = d2.getTemperature().get().getValue() - d1.getTemperature().get().getValue();
        double gradTest = td / dz;
        if (td < 0D) {
          if (Math.abs(gradTest) > 1.0) {
            depthResults.set(i, () -> true);
            depthResults.set(i + 1, () -> true);
          }
        } else if (gradTest > 0.2) {
          depthResults.set(i, () -> true);
          depthResults.set(i + 1, () -> true);
        }

      }

    }

    return () -> depthResults;
  }
}
