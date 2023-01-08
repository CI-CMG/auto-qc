package edu.colorado.cires.wod.autoqc.testutil;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.ResizableDoubleArray;

public final class ArrayUtils {

  public static boolean[] getMask(double[] a, double fillValue) {
    boolean[] mask = new boolean[a.length];
    for (int i = 0; i < a.length; i++) {
      mask[i] = !Precision.equals(a[i], fillValue);
    }
    return mask;
  }

  public static double[] mask(double[] a, boolean[] mask) {
    if (a.length != mask.length) {
      throw new IllegalArgumentException("Array and mask must be of equal length");
    }
    ResizableDoubleArray result = new ResizableDoubleArray();
    for (int i = 0; i < a.length; i++) {
      if (mask[i]) {
        result.addElement(a[i]);
      }
    }
    return result.getElements();
  }

  private ArrayUtils() {

  }
}
