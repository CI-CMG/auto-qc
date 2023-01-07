package edu.colorado.cires.wod.autoqc.test.enbgavail;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

@Component
public class EnBackgroundAvailableCheckTest implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnBackgroundAvailableCheckTest.class);

  private final EnBackgroundCheckParameters parameters;

  @Autowired
  public EnBackgroundAvailableCheckTest(
      TestProperties testProperties,
      @Value("${autoqc.data-dir}") String dataDir,
      RestTemplate restTemplate
  ) throws IOException {
    String url = testProperties.getTest().getEnBgAvailCheck().getInfoNetCdfUrl();
    Path saveTo = Paths.get(dataDir).resolve(testProperties.getTest().getEnBgAvailCheck().getInfoNetCdfFileName());
    if (!Files.exists(saveTo)) {
      LOGGER.info("File: {} did not exist. Downloading from {}", saveTo, url);
      Files.createDirectories(saveTo.getParent());
      DownloadUtil.download(restTemplate, url, saveTo);
    }
    parameters = readEnBackgroundCheckAux(saveTo);
    validateGrid();
  }

  @Override
  public String getName() {
    return "EN_background_available_check";
  }

  private static class EnBackgroundCheckParameters {

    private Array lon;
    private Array lat;
    private Array depth;
    private Array month;
    private Array clim;
    private Array bgev;
    private Array obev;
    private double lonGridSize;
    private double latGridSize;

    public double getLonGridSize() {
      return lonGridSize;
    }

    public void setLonGridSize(double lonGridSize) {
      this.lonGridSize = lonGridSize;
    }

    public double getLatGridSize() {
      return latGridSize;
    }

    public void setLatGridSize(double latGridSize) {
      this.latGridSize = latGridSize;
    }

    public Array getLon() {
      return lon;
    }

    public void setLon(Array lon) {
      this.lon = lon;
    }

    public Array getLat() {
      return lat;
    }

    public void setLat(Array lat) {
      this.lat = lat;
    }

    public Array getDepth() {
      return depth;
    }

    public void setDepth(Array depth) {
      this.depth = depth;
    }

    public Array getMonth() {
      return month;
    }

    public void setMonth(Array month) {
      this.month = month;
    }

    public Array getClim() {
      return clim;
    }

    public void setClim(Array clim) {
      this.clim = clim;
    }

    public Array getBgev() {
      return bgev;
    }

    public void setBgev(Array bgev) {
      this.bgev = bgev;
    }

    public Array getObev() {
      return obev;
    }

    public void setObev(Array obev) {
      this.obev = obev;
    }
  }

  private EnBackgroundCheckParameters readEnBackgroundCheckAux(Path bgcheckInfoFile) throws IOException {
    try (NetcdfFile nc = NetcdfFiles.open(bgcheckInfoFile.toString())) {
      EnBackgroundCheckParameters parameters = new EnBackgroundCheckParameters();
      parameters.setLon(Objects.requireNonNull(nc.findVariable("longitude")).read());
      parameters.setLat(Objects.requireNonNull(nc.findVariable("latitude")).read());
      parameters.setDepth(Objects.requireNonNull(nc.findVariable("depth")).read());
      parameters.setMonth(Objects.requireNonNull(nc.findVariable("month")).read());
      parameters.setClim(Objects.requireNonNull(nc.findVariable("potm_climatology")).read());
      //TODO are these needed, fix setter
//      parameters.setLon(Objects.requireNonNull(nc.findVariable("bgev")).read());
//      parameters.setLon(Objects.requireNonNull(nc.findVariable("obev")).read());
      return parameters;
    }
  }

  private static final double THRESHOLD = 0.0000000001;

  private void validateGrid() {
    double lonSize = parameters.getLon().getDouble(1) - parameters.getLon().getDouble(0);
    double latSize = parameters.getLat().getDouble(1) - parameters.getLat().getDouble(0);
    for (int i = 1; i < parameters.getLon().getSize(); i++) {
      double size = parameters.getLon().getDouble(i) - parameters.getLon().getDouble(i - 1);
      if (lonSize < 0 || size < 0 || Math.abs(lonSize - size) > THRESHOLD) {
        throw new IllegalArgumentException("Invalid Background Check File: Longitude not evenly spaced");
      }
    }
    for (int i = 1; i < parameters.getLat().getSize(); i++) {
      double size = parameters.getLat().getDouble(i) - parameters.getLat().getDouble(i - 1);
      if (latSize < 0 || size < 0 || Math.abs(latSize - size) > THRESHOLD) {
        throw new IllegalArgumentException("Invalid Background Check File: Latitude not evenly spaced");
      }
    }
    parameters.setLonGridSize(lonSize);
    parameters.setLatGridSize(latSize);
  }

  private static class GridCell {

    private final int iLon;
    private final int iLat;

    private GridCell(int iLon, int iLat) {
      this.iLon = iLon;
      this.iLat = iLat;
    }

    public int getiLon() {
      return iLon;
    }

    public int getiLat() {
      return iLat;
    }
  }

  private static int normalizeLatitude(double lat) {
    double latitude = lat * Math.PI / 180;
    return (int) Math.round(Math.asin(Math.sin(latitude)) / (Math.PI / 180D));
  }

  private GridCell findGridCell(Cast cast) {

    int iLon = (int) (Math.round((cast.getLongitude() - parameters.getLon().getDouble(0)) / parameters.getLonGridSize()) % parameters.getLon()
        .getSize());
    int iLat = (int) Math.round((normalizeLatitude(cast.getLatitude()) - parameters.getLat().getDouble(0)) / parameters.getLatGridSize());
    // Checks for edge case where lat is ~90.
    if (iLat == parameters.getLat().getSize()) {
      iLat = iLat - 1;
    }

    if (iLon < 0 || iLon > parameters.getLon().getSize()) {
      throw new IllegalStateException("Longitude is out of range: " + cast.getLongitude() + " " + iLon);
    }

    if (iLat < 0 || iLat > parameters.getLat().getSize()) {
      throw new IllegalStateException("Latitude is out of range: " + cast.getLatitude() + " " + iLat);
    }

    return new GridCell(iLon, iLat);
  }

  private static OptionalDouble interpolate(double z, double[] depths, double[] clim) {

    LinearInterpolator interp = new LinearInterpolator();
    PolynomialSplineFunction f = interp.interpolate(depths, clim);

    if (f.isValidPoint(z)) {
      return OptionalDouble.of(f.value(z));
    }

    return OptionalDouble.empty();
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {

    Cast cast = context.getCast();

    GridCell gridCell = findGridCell(cast);
    int month = cast.getMonth() - 1;
    double[] clim;
    try {
      clim = (double[]) parameters.getClim()
          .section(Arrays.asList(
              Range.EMPTY,
              Range.make(gridCell.getiLat(), gridCell.getiLat()),
              Range.make(gridCell.getiLon(), gridCell.getiLon()),
              Range.make(month, month)
          )).get1DJavaArray(DataType.DOUBLE);
    } catch (InvalidRangeException e) {
      throw new RuntimeException(e);
    }
    double[] ncDepths = (double[]) parameters.getDepth().get1DJavaArray(DataType.DOUBLE);

    List<Depth> depths = cast.getDepths();
    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());

    int zeroCount = 0;
    for (double v : clim) {
      if (v == 0D) {
        zeroCount++;
      }
    }
    if (zeroCount == clim.length) {
      for (int i = 0; i < depths.size(); i++) {
        depthResults.add(() -> true);
      }
      return () -> depthResults;
    }

    for (Depth depth : depths) {
      Optional<ProfileData> maybeTemp = depth.getTemperature();

      boolean failed;
      if (depth.getDepth() == null || maybeTemp.isEmpty()) {
        failed = false;
      } else {
        failed = interpolate(depth.getDepth(), ncDepths, clim).isEmpty();
      }

      depthResults.add(() -> failed);

    }

    return () -> depthResults;
  }
}
