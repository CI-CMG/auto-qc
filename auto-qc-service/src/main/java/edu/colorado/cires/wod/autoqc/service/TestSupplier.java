package edu.colorado.cires.wod.autoqc.service;

import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestSupplier {

  private static Logger LOGGER = LoggerFactory.getLogger(TestSupplier.class);

  private final List<AutoQcTest> tests;

  @Autowired
  public TestSupplier(List<AutoQcTest> tests) {
    this.tests = tests.stream().sorted(Comparator.comparing(t -> t.getName().toLowerCase(Locale.ENGLISH))).collect(Collectors.toList());
    LOGGER.info("Found {} QC tests", this.tests.size());
    for (AutoQcTest test : this.tests) {
      LOGGER.info("QC Test Ready: {}", test.getName());
    }
  }

  public List<AutoQcTest> getTests() {
    return tests;
  }

}
