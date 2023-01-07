package edu.colorado.cires.wod.autoqc.test;

public interface AutoQcTest {

  String getName();
  AutoQcTestResult test(AutoQcTestContext context);

}
