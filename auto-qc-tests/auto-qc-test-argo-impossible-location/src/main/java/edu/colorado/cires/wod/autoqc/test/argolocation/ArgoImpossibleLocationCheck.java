package edu.colorado.cires.wod.autoqc.test.argolocation;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.testutil.CastUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.argo-impossible-location", name = "enabled", havingValue = "true")
public class ArgoImpossibleLocationCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgoImpossibleLocationCheck.class);

  @Override
  public String getName() {
    return "Argo_impossible_location_test";
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }
    Cast cast = context.getCast();
    return CastUtils.all(cast, cast.getLatitude() < -90D || cast.getLatitude() > 90D || cast.getLongitude() < -180D || cast.getLongitude() > 180D);
  }
}
