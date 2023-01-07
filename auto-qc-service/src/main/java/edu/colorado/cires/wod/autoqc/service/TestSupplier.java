package edu.colorado.cires.wod.autoqc.service;

import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestSupplier {

  private final List<AutoQcTest> tests;

  @Autowired
  public TestSupplier(List<AutoQcTest> tests) {
    this.tests = tests.stream().sorted(Comparator.comparing(t -> t.getName().toLowerCase(Locale.ENGLISH))).collect(Collectors.toList());
  }

  public List<AutoQcTest> getTests() {
    return tests;
  }

}
