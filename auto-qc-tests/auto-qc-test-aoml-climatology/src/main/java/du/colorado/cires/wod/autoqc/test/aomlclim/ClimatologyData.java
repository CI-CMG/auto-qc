package du.colorado.cires.wod.autoqc.test.aomlclim;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import ucar.ma2.Array;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

class ClimatologyData {

  private final Array time;
  private final Array deps;
  private final Array lats;
  private final Array lons;
//  private final Array fillValue;

  public ClimatologyData(Path netCdfFile) {
    try (NetcdfFile nc = NetcdfFiles.open(netCdfFile.toString())) {

      time = Objects.requireNonNull(nc.findVariable("time")).read();
      deps = Objects.requireNonNull(nc.findVariable("depth")).read();
      lats = Objects.requireNonNull(nc.findVariable("lat")).read();
      lons = Objects.requireNonNull(nc.findVariable("lon")).read();

    } catch (IOException e) {
      throw new RuntimeException("Unable to read " + netCdfFile, e);
    }
  }

  public Array getTime() {
    return time;
  }

  public Array getDeps() {
    return deps;
  }

  public Array getLats() {
    return lats;
  }

  public Array getLons() {
    return lons;
  }
}
