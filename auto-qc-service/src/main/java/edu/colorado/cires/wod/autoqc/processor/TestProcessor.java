package edu.colorado.cires.wod.autoqc.processor;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.wod.autoqc.model.AutoQcCastTestResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.ProfileFilter;
import edu.colorado.cires.wod.autoqc.service.TestSupplier;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestProcessor {

  private final TestSupplier testSupplier;
  private final ProfileFilter profileFilter;

  @Autowired
  public TestProcessor(TestSupplier testSupplier, ProfileFilter profileFilter) {
    this.testSupplier = testSupplier;
    this.profileFilter = profileFilter;
  }


  public void process(AutoQcTestContextImpl testContext) throws Exception {
    Cast cast = testContext.getCast();
    AutoQcCastTestResult result = testContext.getCastTestResult();

    if (profileFilter.allowProfile(cast)) {

      for (AutoQcTest test : testSupplier.getTests()) {
        String testName = test.getName();
        AutoQcTestResult testResult = test.test(testContext);
        for (int i = 0; i < cast.getDepths().size(); i++) {
          AutoQcTestDepthResult depthResult = testResult.getDepthResults().get(i);
          if (depthResult.isFailed()) {
            result.getCastFailures().add(testName);
            result.getDepthFailures().get(i).add(testName);
          }
        }
      }

    } else {

      result.setSkipped(true);

    }


  }
}
