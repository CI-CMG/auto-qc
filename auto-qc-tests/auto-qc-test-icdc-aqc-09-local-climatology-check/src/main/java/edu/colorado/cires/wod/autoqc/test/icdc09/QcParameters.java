package edu.colorado.cires.wod.autoqc.test.icdc09;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

class QcParameters {

  /*
      datadict = {}
    nc = Dataset('data/climatological_t_median_and_amd_for_aqc.nc', 'r')
    datadict['zedqc'] = nc.variables['zedqc'][:]
    datadict['tamdM'] = nc.variables['tamdM'][:]
    datadict['tmedM'] = nc.variables['tmedM'][:]
    datadict['tamdA'] = nc.variables['tamdA'][:]
    datadict['tmedA'] = nc.variables['tmedA'][:]
    datadict['fillValue'] = nc.fillValue
    nc.close()
    parameterStore['icdc09'] = datadict
   */

  private final Array zedqc;
  private final Array tamdM;
  private final Array tmedM;
  private final Array tamdA;
  private final Array tmedA;
//  private final Array fillValue;

  public QcParameters(Path netCdfFile) {
    try (NetcdfFile nc = NetcdfFiles.open(netCdfFile.toString())) {

      zedqc = Objects.requireNonNull(nc.findVariable("zedqc")).read();
      tamdM = Objects.requireNonNull(nc.findVariable("tamdM")).read();
      tmedM = Objects.requireNonNull(nc.findVariable("tmedM")).read();
      tamdA = Objects.requireNonNull(nc.findVariable("tamdA")).read();
      tmedA = Objects.requireNonNull(nc.findVariable("tmedA")).read();

    } catch (IOException e) {
      throw new RuntimeException("Unable to read " + netCdfFile, e);
    }
  }

  public Array getZedqc() {
    return zedqc;
  }

  public Array getTamdM() {
    return tamdM;
  }

  public Array getTmedM() {
    return tmedM;
  }

  public Array getTamdA() {
    return tamdA;
  }

  public Array getTmedA() {
    return tmedA;
  }
}
