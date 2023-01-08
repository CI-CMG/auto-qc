package edu.colorado.cires.wod.autoqc.test.aomlspike;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.testutil.ArrayUtils;
import edu.colorado.cires.wod.autoqc.testutil.CastUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.aoml-spike", name = "enabled", havingValue = "true")
public class AomlSpikeCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AomlSpikeCheck.class);

  @Override
  public String getName() {
    return "AOML_spike";
  }

  /*
  def test(p, parameters):

    qc = numpy.zeros(p.n_levels(), dtype=bool)
    # this spike test only makes sense for 3 or more levels
    if p.n_levels() < 3:
        return qc

    t = p.t()

    for i in range(2, p.n_levels()-2):
        qc[i] = spike(t[i-2:i+3])

    qc[1] = spike(t[0:3])
    qc[-2] = spike(t[-3:])

    return qc

def spike(t):
	# generic spike check for a masked array of an odd number of consecutive temperature measurements

    if True in t.mask:
    	# missing data, decline to flag
    	return False

    centralTemp = t[int(len(t)/2)]
    medianDiff = numpy.round( abs(centralTemp - numpy.ma.median(t)),2)

    if medianDiff != 0:
        t = numpy.delete(t, int(len(t)/2))
        spikeCheck = numpy.round(abs(centralTemp-numpy.ma.mean(t)), 2)
        if spikeCheck > 0.3:
            return True

    return False
   */

  private static double round(double number, int decimalsToConsider) {
    return new BigDecimal(number).setScale(decimalsToConsider, RoundingMode.HALF_UP).doubleValue();
  }


  private boolean spike(List<Depth> depths) {
    double[] t = new double[depths.size()];
    for (int i = 0; i < depths.size(); i++) {
      Depth depth = depths.get(i);
      if (depth.getTemperature().isEmpty()) {
        return false;
      }
      t[i] = depth.getTemperature().get().getValue();
    }
    double centralTemp = t[t.length / 2];
    double medianDiff = round(Math.abs(centralTemp - ArrayUtils.median(t)), 2);

    if (!Precision.equals(0D, medianDiff)) {
      t = ArrayUtils.deleteElement(t, t.length / 2);
      double spikeCheck = round(Math.abs(centralTemp - ArrayUtils.mean(t)), 2);
      if (spikeCheck > 0.3) {
        return true;
      }

    }

    return false;
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }

    Cast cast = context.getCast();
    List<Depth> depths = cast.getDepths();

    if (depths.size() < 3) {
      return CastUtils.allGood(cast);
    }

    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
    for (int i = 0; i < depths.size(); i++) {
      depthResults.add(() -> false);
    }

    for (int i = 2; i < depths.size() - 2; i++) {
      boolean failure = spike(depths.subList(i - 2, i + 3));
      depthResults.set(i, () -> failure);
    }

    boolean f1 = spike(depths.subList(0, 3));
    depthResults.set(1, () -> f1);

    boolean f2 = spike(depths.subList(depths.size() - 3, depths.size()));
    depthResults.set(depths.size() - 2, () -> f2);

    return () -> depthResults;
  }
}
