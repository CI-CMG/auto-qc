package edu.colorado.cires.wod.autoqc.test.icdc09;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.testutil.CastUtils;
import edu.colorado.cires.wod.autoqc.testutil.DownloadUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ucar.ma2.Array;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.icdc-aqc-09-local-climatology-check", name = "enabled", havingValue = "true")
public class IcdcAqc09LocalClimatologyCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(IcdcAqc09LocalClimatologyCheck.class);

  private final QcParameters testParameters;

  @Autowired
  public IcdcAqc09LocalClimatologyCheck(QcProperties testProperties, @Value("${autoqc.data-dir}") String dataDir, RestTemplate restTemplate)
      throws IOException {
    String url = testProperties.getTest().getIcdcAqc09LocalClimatologyCheck().getNetCdfUrl();
    Path saveTo = Paths.get(dataDir).resolve(testProperties.getTest().getIcdcAqc09LocalClimatologyCheck().getNetCdfFileName());
    if (!Files.exists(saveTo)) {
      LOGGER.info("File: {} did not exist. Downloading from {}", saveTo, url);
      Files.createDirectories(saveTo.getParent());
      DownloadUtil.download(restTemplate, url, saveTo);
    }
    testParameters = new QcParameters(saveTo);
  }


  @Override
  public String getName() {
    return "ICDC_aqc_09_local_climatology_check";
  }

  private static class MinMax {
    private final Double min;
    private final Double max;

    private MinMax(Double min, Double max) {
      this.min = min;
      this.max = max;
    }

    public Double getMin() {
      return min;
    }

    public Double getMax() {
      return max;
    }

    boolean isValid() {
      return min != null && max != null;
    }
  }

  private Optional<List<MinMax>> getClimatologyRange(double lat, double lon, List<Depth> depths) {

//  Global ranges - data outside these bounds are assumed not valid.
    double parminover = -2.3;
    double parmaxover = 33.0;

//  Calculate grid indices.
    int iy = (int) Math.floor((90.0 - lat) / 0.5);
    int ix = (int) Math.floor((lon + 180.0) / 0.5);
    if (iy < 0 || iy > 360 || ix < 0 || ix > 720) {
      return Optional.empty();
    }

//    for (Depth depth : depths) {
//      Array zedqcSplice;
//      Array zedqcSplice;
//      double kisel = de
//    }

    /*
        # Find the climatology range.
    for k in range(nlevels):
        # Find the corresponding climatology level.
        arg = np.argwhere((z[k] >= paramsicdc09['zedqc'][:][:-1]) & (z[k] < paramsicdc09['zedqc'][:][1:]))
        if len(arg) > 0:
            kisel = arg[0]
        else:
            continue # No level found.
        # Check if using monthly or annual fields.
        if kisel <= 15:
            useAnnual = False
        else:
            useAnnual = True
        if month is None: useAnnual = True
        # Extract the temperature.
        if useAnnual == False:
            amd = paramsicdc09['tamdM'][ix, iy, kisel, month - 1][0]
            if amd < 0.0:
                useAnnual = True
            else:
                tmedian = paramsicdc09['tmedM'][ix, iy, kisel, month - 1][0]
                if tmedian < parminover:
                    useAnnual = True
        if useAnnual:
            amd = paramsicdc09['tamdA'][ix, iy, kisel][0]
            if amd < 0.0:
                continue
            else:
                tmedian = paramsicdc09['tmedA'][ix, iy, kisel][0]
                if tmedian < parminover:
                    continue
        if amd > 0.0 and amd < 0.05: amd = 0.05

        rnumamd = 3.0
        tmaxa   = tmedian + rnumamd * amd
        tmina   = tmedian - rnumamd * amd
        if tmina < parminover: tmina = parminover
        if tmaxa > parmaxover: tmaxa = parmaxover

        tmin[k] = tmina
        tmax[k] = tmaxa

    return tmin, tmax
     */

return Optional.empty();
  }

  /*
  def get_climatology_range(nlevels, z, lat, lon, month, paramsicdc09):

    # Define arrays for the results.
    tmin = np.ndarray(nlevels)
    tmax = np.ndarray(nlevels)
    tmin[:] = paramsicdc09['fillValue']
    tmax[:] = paramsicdc09['fillValue']

    # Global ranges - data outside these bounds are assumed not valid.
    parminover = -2.3
    parmaxover = 33.0

    # Calculate grid indices.
    iy = int(np.floor((90.0 - lat) / 0.5))
    ix = int(np.floor((lon + 180.0) / 0.5))
    if (iy < 0 or iy > 360 or ix < 0 or ix > 720):
        return None

    # Find the climatology range.
    for k in range(nlevels):
        # Find the corresponding climatology level.
        arg = np.argwhere((z[k] >= paramsicdc09['zedqc'][:][:-1]) & (z[k] < paramsicdc09['zedqc'][:][1:]))
        if len(arg) > 0:
            kisel = arg[0]
        else:
            continue # No level found.
        # Check if using monthly or annual fields.
        if kisel <= 15:
            useAnnual = False
        else:
            useAnnual = True
        if month is None: useAnnual = True
        # Extract the temperature.
        if useAnnual == False:
            amd = paramsicdc09['tamdM'][ix, iy, kisel, month - 1][0]
            if amd < 0.0:
                useAnnual = True
            else:
                tmedian = paramsicdc09['tmedM'][ix, iy, kisel, month - 1][0]
                if tmedian < parminover:
                    useAnnual = True
        if useAnnual:
            amd = paramsicdc09['tamdA'][ix, iy, kisel][0]
            if amd < 0.0:
                continue
            else:
                tmedian = paramsicdc09['tmedA'][ix, iy, kisel][0]
                if tmedian < parminover:
                    continue
        if amd > 0.0 and amd < 0.05: amd = 0.05

        rnumamd = 3.0
        tmaxa   = tmedian + rnumamd * amd
        tmina   = tmedian - rnumamd * amd
        if tmina < parminover: tmina = parminover
        if tmaxa > parmaxover: tmaxa = parmaxover

        tmin[k] = tmina
        tmax[k] = tmaxa

    return tmin, tmax
   */



  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }

    Cast cast = context.getCast();

//  No check for the Caspian Sea or Great Lakes.
    double lat = cast.getLatitude();
    double lon = cast.getLongitude();
    if ((lat >= 35.0 && lat <= 45.0 && lon >= 45.0 && lon <= 60.0) ||
        (lat >= 40.0 && lat <= 50.0 && lon >= -95.0 && lon <= -75.0)) {
      return CastUtils.allGood(cast);
    }

    List<Depth> depths = CastUtils.sortDepths(cast);

    Optional<List<MinMax>> maybeRanges = getClimatologyRange(lat, lon, depths);
    if (maybeRanges.isEmpty()) {
      return CastUtils.allGood(cast);
    }
    List<MinMax> ranges = maybeRanges.get();

    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
    for (int i = 0; i < depths.size(); i++) {

      Depth depth = depths.get(i);
      MinMax range = ranges.get(i);

      boolean failed = depth.getTemperature()
            .map(ProfileData::getValue)
            .map(t -> range.isValid() && (t < range.getMin() || t > range.getMax()))
            .orElse(false);

      depthResults.add(() -> failed);

    }

    return () -> depthResults;
  }
}
