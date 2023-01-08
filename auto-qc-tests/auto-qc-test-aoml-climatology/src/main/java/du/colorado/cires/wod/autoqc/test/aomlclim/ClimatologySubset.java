package du.colorado.cires.wod.autoqc.test.aomlclim;

import ucar.ma2.Array;

class ClimatologySubset {

  private Array latLonDepthTempList;
  private Array depthColumns;
  private Array latLonList;

  public Array getLatLonDepthTempList() {
    return latLonDepthTempList;
  }

  public void setLatLonDepthTempList(Array latLonDepthTempList) {
    this.latLonDepthTempList = latLonDepthTempList;
  }

  public Array getDepthColumns() {
    return depthColumns;
  }

  public void setDepthColumns(Array depthColumns) {
    this.depthColumns = depthColumns;
  }

  public Array getLatLonList() {
    return latLonList;
  }

  public void setLatLonList(Array latLonList) {
    this.latLonList = latLonList;
  }
}
