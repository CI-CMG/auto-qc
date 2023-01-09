package edu.colorado.cires.wod.autoqc.test.argoregion;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.testutil.CastUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.argo-regional-range", name = "enabled", havingValue = "true")
public class ArgoRegionalRangeCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgoRegionalRangeCheck.class);


  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

  private static final Polygon RED_SEA = GEOMETRY_FACTORY.createPolygon(new Coordinate[]{
      new CoordinateXY(40D, 10D), new CoordinateXY(50D, 20D), new CoordinateXY(30D, 30D), new CoordinateXY(40D, 10D)
  });

  private static final Polygon MEDITERRANEAN = GEOMETRY_FACTORY.createPolygon(new Coordinate[]{
      new CoordinateXY(6D, 30D),
      new CoordinateXY(40D, 30D),
      new CoordinateXY(35D, 40D),
      new CoordinateXY(20D, 42D),
      new CoordinateXY(15D, 50D),
      new CoordinateXY(5D, 40D),
      new CoordinateXY(6D, 30D)
  });

  @Override
  public String getName() {
    return "Argo_regional_range_test";
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }
    Cast cast = context.getCast();

    Point point = GEOMETRY_FACTORY.createPoint(new CoordinateXY(cast.getLongitude(), cast.getLatitude()));

    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(cast.getDepths().size());
    for (Depth depth : cast.getDepths()) {

      boolean failed = depth.getTemperature()
          .map(ProfileData::getValue)
          .map(temp -> {
            if (point.intersects(RED_SEA)) {
              return temp < 21.7 || temp > 40D;
            }
            if (point.intersects(MEDITERRANEAN)) {
              return temp < 10D || temp > 40D;
            }
            return false;
          })
          .orElse(false);
      depthResults.add(() -> failed);

    }

    return () -> depthResults;
  }
}
