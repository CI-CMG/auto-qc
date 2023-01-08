package du.colorado.cires.wod.autoqc.test.aomlclim;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.testutil.ArrayUtils;
import edu.colorado.cires.wod.autoqc.testutil.CastUtils;
import edu.colorado.cires.wod.autoqc.testutil.DownloadUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

//TODO complete me

//@Component
//@ConditionalOnProperty(prefix = "autoqc.test.aoml-climatology", name = "enabled", havingValue = "true")
public class AomlClimatologyCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AomlClimatologyCheck.class);

  private final Path netCdfFile;
  private final ClimatologyData climatologyData;

  @Autowired
  public AomlClimatologyCheck(QcProperties testProperties, @Value("${autoqc.data-dir}") String dataDir, RestTemplate restTemplate)
      throws IOException {
    String url = testProperties.getTest().getAomlClimatology().getNetCdfUrl();
    netCdfFile = Paths.get(dataDir).resolve(testProperties.getTest().getAomlClimatology().getNetCdfFileName());
    if (!Files.exists(netCdfFile)) {
      LOGGER.info("File: {} did not exist. Downloading from {}", netCdfFile, url);
      Files.createDirectories(netCdfFile.getParent());
      DownloadUtil.download(restTemplate, url, netCdfFile);
    }
    climatologyData = new ClimatologyData(netCdfFile);
  }

  @Override
  public String getName() {
    return "AOML_climatology_test";
  }


  private static int closestIndex(Array coordinateList, double point) {
    double dif = Math.abs(coordinateList.getDouble(0) - point);
    int closest = 0;
    for (int i = 1; i < coordinateList.getSize(); i++) {
      double difTmp = Math.abs(coordinateList.getDouble(i) - point);
      if (difTmp < dif) {
        dif = difTmp;
        closest = i;
      }
    }
    return closest;
  }

  /*
  def subset_data(x, y, netcdFile, cScope, clima, fieldType):
  """
    Function is expecting 6 arguments:
      Float for longitude
      Float for latitude
      String for path and filename to configuration file
      Float for range of coordinates to gather
      Boolean for evaluating if climatology data or not
      String for type of climatology data

    Process:
      Open netCDF as read only
      Get timestamp, depth measurements, longitudes and latitudes
      Get ranges of index numbers from latitude and longitude lists of
        points near coordinates to limit size
      Call functions to organize longitude and latitude in list of lists and
        map its index numbers with a list of temperatures index numbers

    Return list of lists of latitude and longitude points, list for depth
      measurements, list of lists with temperatures, timestamp in netCDF
    Return empty list, an empty list, an empty list, -1 if exception
  """

  nf = Dataset(netcdFile, "r")

  if clima:
    time = nf.variables["time"][0]
    deps = nf.variables["depth"][:]
    lats = nf.variables["lat"][:]
    lons = nf.variables["lon"][:]
  else:
    time = nf.variables["Time"][0]
    deps = nf.variables["zt_k"][:]
    lats = nf.variables["yt_j"][:]
    lons = nf.variables["xt_i"][:]
  temperatureType = fieldType

  minIndexLat = interp_helper.closest_index(lats, y - cScope)
  maxIndexLat = interp_helper.closest_index(lats, y + cScope)
  minIndexLon = interp_helper.closest_index(lons, x - cScope)
  maxIndexLon = interp_helper.closest_index(lons, x + cScope)

  latLonList = []
  latLonTemp = []
  latLonList, latLonTemp = lon_lat_temp_lists(nf, minIndexLat, maxIndexLat, minIndexLon, maxIndexLon, len(deps), lats, lons, temperatureType)
  nf.close()
  return latLonTemp, deps, latLonList, time
   */


  private ClimatologySubset subsetData(double longitude, double latitude, double coordRange, String fieldType) {
    int minIndexLat = closestIndex(climatologyData.getLats(), latitude - coordRange);
    int maxIndexLat = closestIndex(climatologyData.getLats(), latitude + coordRange);
    int minIndexLon = closestIndex(climatologyData.getLons(), longitude - coordRange);
    int maxIndexLon = closestIndex(climatologyData.getLats(), longitude + coordRange);
    return null;
  }

  private OrganizedData organizeData(
      int minLatIndexNumber,
      int maxLatIndexNumber,
      int minLonIndexNumber,
      int maxLonIndexNumber,
      int degrees,
      int dRange,
      String tType
  ) {
    List<Double> latLonList = new ArrayList<>();
    List<Double> latLonTemp = new ArrayList<>();
    LatLonTempDict latLonTempDict = new LatLonTempDict();
    int numberOfLongitudes = 1 + maxLonIndexNumber - minLatIndexNumber;
    List<XY> indicator = new ArrayList<>();
    for (int y = minLatIndexNumber; y <= maxLatIndexNumber; y++) {
      for (int x = minLonIndexNumber; x <= maxLonIndexNumber; x++) {
        indicator.add(new XY(x, y));
      }
    }

    Array oceanDepthData;
    try (NetcdfFile nc = NetcdfFiles.open(netCdfFile.toString())) {
      oceanDepthData = nc.findVariable(tType).read(
          Arrays.asList(
              Range.make(0, 0),
              Range.make(0, dRange),
              Range.make(minLatIndexNumber, maxLatIndexNumber + 1),
              Range.make(minLonIndexNumber, maxLonIndexNumber + 1)
          ));
    } catch (IOException | InvalidRangeException e) {
      throw new RuntimeException("Unable to read " + netCdfFile, e);
    }

//    for (int odi = 0; odi < oceanDepthData.getSize(); odi++) {
//      double oddata = oceanDepthData.getA
//    }
return null;
  }

  /*
  def organize_data(netFile, minLatIndexNumber, maxLatIndexNumber,
                  minLonIndexNumber, maxLonIndexNumber, degrees,
                  dRange, lats, lons, tType):
  """
    Function is expecting 10 arguments:
      Dataset object for NetCDF
      Integer for lowest index number in latitude points list
      Integer for highest index number in latitude points list
      Integer for lowest index number in longitude points list
      Integer for highest index number in longitude points list
      Integer for adding or subtracting longitude point to produce a new
        longitude point that is beyond file limits
      Integer for length of depth list
      List for latitude points in netCDF file
      List for longitude points in netCDF file
      String for name of depth temperatures (i.e. t_sd, t_an, temp)

    Process:
      Get the amount of longitude points that are going to be used
      Create a list of tuples that hold latitude and longitude index numbers
        and use it as an indicator map to locate coordinates in netcdf depth
        temperature
      Get longitude and latitude temperatures in netCDF which is returned as
        lists (each list represents a latitude point with longitude points
        from lowest to greatest) within lists (each list represents a depth
        from lowest to greatest) within one final list
      Loop through netcdf depth temperature list which is a list
        (longitudes for each latitude; lowest to greatest) within another
        list (depth starting at 0) encapsulated within another list
        If temperature is masked:
          replace it with nan (not a number)
        Find the matching coordinates to depth temperature and append it
          to list in dictionary
      Create new list of tuples that contains the actual latitude and
        longitude coordinates by looping through dictionary keys and using
        the index numbers within dictionary keys tuples

    Return list of lists of latitude and longitude points, list of lists of
      temperatures
  """
  latLonList = []
  latLonTemp = []
  latLonTempDict = {}
  numberOfLongitudes = len(range(minLonIndexNumber, maxLonIndexNumber+1))
  indicator = [ (y, x) for y in range(minLatIndexNumber, maxLatIndexNumber+1)
                       for x in range(minLonIndexNumber, maxLonIndexNumber+1)]
  oceanDepthData = netFile.variables[tType][0, 0:dRange,
                    minLatIndexNumber:maxLatIndexNumber+1,
                    minLonIndexNumber:maxLonIndexNumber+1]

  for oddata in oceanDepthData:
    for numY, ytemperatures in enumerate(oddata):
      for numX, xtemperature in enumerate(ytemperatures):
        indicatorNum = numY * numberOfLongitudes + numX
        indicatorKey = indicator[indicatorNum]

        if np.ma.is_masked(xtemperature):
          xtemperature = np.nan

        if indicatorKey in latLonTempDict:
          latLonTempDict[indicatorKey].append(xtemperature)
        else:
          latLonTempDict[indicatorKey] = [xtemperature]

  latLonList = [[lats[latLonIndexTuple[0]], lons[latLonIndexTuple[1]]+degrees] for latLonIndexTuple in list(latLonTempDict.keys())]

  return latLonList, list(latLonTempDict.values())
   */

  private LatLonTempLists latLonTempLists(
      int minIndexLat,
      int maxIndexLat,
      int minIndexLon,
      int maxIndexLon,
      int depthRange,
      String temperatureType
  ) {
    long lonsLength = climatologyData.getLons().getSize();
    List<Double> latLonList = new ArrayList<>();
    List<Double> latLonTemp = new ArrayList<>();
    List<Double> moreLatLonList = new ArrayList<>();
    List<Double> morelatLonTemp = new ArrayList<>();

    return null;
  }

  /*
  def lon_lat_temp_lists(netFile, minIndexLat, maxIndexLat, minIndexLon,
                       maxIndexLon, depthRange, lats, lons, temperatureType):
  """
    Function is expecting 9 arguments:
      Dataset object for NetCDF
      Integer for lowest index number in latitude points list
      Integer for highest index number in latitude points list
      Integer for lowest index number in longitude points list
      Integer for highest index number in longitude points list
      Integer for length of depth list
      List for latitude points in netCDF file
      List for longitude points in netCDF file
      String for name of depth temperatures (i.e. t_sd, t_an, temp)

    Objective:
      Call on a function based on the indices provided representing the
        minimum and maximum index number that will be used in the latitude
        list and longitude list, loops for every longitude/latitude points
        combination in the lists that will be used to find which points has
        any temperature data.
      After, check if longitude is near 0 point, call function again to
        append the opposite side of 0 longitude point along with their
        corresponding list of temperatures

    Return list of lists of latitude and longitude points, list of lists of
      temperatures
  """
  lonsLength = len(lons)
  latLonList = []
  latLonTemp = []
  moreLatLonList = []
  morelatLonTemp = []

  latLonList, latLonTemp = (
      organize_data(netFile, minIndexLat, maxIndexLat, minIndexLon,
                    maxIndexLon, 0, depthRange, lats, lons, temperatureType)
  )

  if (
        (0 <= minIndexLon <= 4) or
        (lonsLength - 5 <= maxIndexLon <= lonsLength - 1)
     ):
    if (0 <= minIndexLon <= 4):
      moreLatLonList, morelatLonTemp = (
          organize_data(netFile, minIndexLat, maxIndexLat, lonsLength - 5,
                        lonsLength - 2, -360, depthRange, lats, lons,
                        temperatureType)
      )
    elif (lonsLength - 5 <= maxIndexLon <= lonsLength - 1):
      moreLatLonList, morelatLonTemp = (
          organize_data(netFile, minIndexLat, maxIndexLat, 0, 4, 360,
                        depthRange, lats, lons, temperatureType)
      )
  return latLonList + moreLatLonList, latLonTemp + morelatLonTemp
   */

  private ClimatologySubset getClimatologySubset(double longitude, double latitude, String statType, double coordRange) {
    String fieldType;
    switch (statType) {
      case "analyzed mean":
        fieldType = "t_an";
        break;
      case "standard deviations":
        fieldType = "t_sd";
        break;
      default:
        LOGGER.warn("Cannot process climatology file with a statistical field as " + statType);
        return new ClimatologySubset();
    }

    return subsetData(longitude, latitude, coordRange, fieldType);
  }

  /*
  def subset_climatology_data(longitude, latitude, statType, coordRange=1, filePathName='data/woa13_00_025.nc'):
  """
    longitude: float
    latitude: float
    statType: either 'analyzed mean' or 'standard deviations'
    coordRange: degrees plus / minus around longitude and latitude to consider.
    filePathName: relative path from root of climatology file

    Return list of lists with temperatures that maps one to one with list
      of lists with tuples of latitude and longitude coordinates, list for
      depth measurements, and list of lists with tuples of latitude and
      longitude coordinates that maps one to one with list of lists with
      temperature
    Return an empty list, an empty list, and an empty list if exception
  """

  if statType == "analyzed mean":
    fieldType = "t_an"
  elif statType == "standard deviations":
    fieldType = "t_sd"
  else:
    sys.stderr.write("Cannot process climatology file with a statistical "
                     "field as " + statType + "\n")
    return [], [], []

  latLonDepthTempList, depthColumns, latLonList, time = read_netcdf.subset_data(longitude, latitude, filePathName, coordRange, True, fieldType)

  return latLonDepthTempList, depthColumns, latLonList
   */


  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }

    Cast cast = context.getCast();
    List<Depth> depths = cast.getDepths();

    double coordRange = 1D;

    ClimatologySubset climatologySubset1 = getClimatologySubset(cast.getLongitude(), cast.getLatitude(), "analyzed mean", coordRange);
    ClimatologySubset climatologySubset2 = getClimatologySubset(cast.getLongitude(), cast.getLatitude(), "standard deviations", coordRange);

    List<AutoQcTestDepthResult> depthResults = new ArrayList<>(depths.size());
    for (int i = 0; i < depths.size(); i++) {
      depthResults.add(() -> false);
    }

    for (int i = 0; i < depths.size(); i++) {
      Depth depth = depths.get(i);
      if (!depth.getTemperature().isPresent() || depth.getDepth() == null) {
        continue;
      }

      double interpTemp = 0D;
      double interpTempSD = 0D;
      boolean failed = climatologyCheck(depth.getTemperature().get(), interpTemp, interpTempSD) >= 4.0;

      depthResults.set(i, () -> failed);
    }

//    for i in range(p.n_levels()):
//
//        # find best interpolated temperature and standard deviation at this depth
//    if not isData[i]: continue
//
//        interpTemp = interp_helper.temperature_interpolation_process(p.longitude(), p.latitude(), p.z()[i], depthColumns1, latLonsList1, lonlatWithTempsList1, False, "climaInterpTemperature")
//    if interpTemp == 99999.99:
//    continue
//
//        interpTempSD = interp_helper.temperature_interpolation_process(p.longitude(), p.latitude(), p.z()[i], depthColumns2, latLonsList2, lonlatWithTempsList2, False, "climaInterpStandardDev")
//    if interpTempSD == 99999.99:
//    continue
//
//        # check if temperature at this depth is sufficiently close to the climatological expectation
//    qc[i] = climatology_check(p.t()[i], interpTemp, interpTempSD) >= 4

    return () -> depthResults;
  }

  private double climatologyCheck(ProfileData profileData, double interpTemp, double interpTempSD) {
    return 0D;
  }
}
