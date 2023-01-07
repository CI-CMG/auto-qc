package edu.colorado.cires.wod.autoqc.processor;

import static edu.colorado.cires.mgg.wod.data.model.VariableConsts.TEMPERATURE;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.wod.autoqc.model.AutoQcCastTestResult;
import edu.colorado.cires.wod.autoqc.model.AutoQcTestContextImpl;
import edu.colorado.cires.wod.autoqc.service.TestSupplier;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestProcessor {

  private final TestSupplier testSupplier;

  @Autowired
  public TestProcessor(TestSupplier testSupplier) {
    this.testSupplier = testSupplier;
  }


  private static boolean hasTemperature(Cast cast) {
      /*
  def catchFlags(profile):
  '''
  In some IQuOD datasets temperature values of 99.9 or 99.99 are special values to
  signify not to use the data value. These are flagged here so they are not
  sent to the quality control programs for testing.
  '''
  index = profile.var_index()
  assert index is not None, 'No temperatures in profile {}'.format(profile.uid())
  for i in range(profile.n_levels()):
      if profile.profile_data[i]['variables'][index]['Missing']:
          continue
      if profile.profile_data[i]['variables'][index]['Value'] >= 99 and profile.profile_data[i]['variables'][index]['Value'] < 100:
          profile.profile_data[i]['variables'][index]['Missing'] = True
   */
    return cast.getVariables().stream().filter(v -> v.getCode() == TEMPERATURE).findFirst().isPresent();
  }

  private boolean assessProfile() {

    // TODO
    /*
    def assessProfile(p, check_originator_flag_type, months_to_use):
    'decide whether this profile is acceptable for QC or not; False = skip this profile'

    # not interested in standard levels
    if int(p.primary_header['Profile type']) == 1:
        return False

    # no temperature data in profile
    if p.var_index() is None:
        return False

    # temperature data is in profile but all masked out
    if np.sum(p.t().mask == False) == 0:
        return False

    # all depths are less than 10 cm and there are at least two levels (ie not just a surface measurement)
    if np.sum(p.z() < 0.1) == len(p.z()) and len(p.z()) > 1:
        return False

    # no valid originator flag type
    if check_originator_flag_type:
        o_flag = p.originator_flag_type()
        if o_flag is not None and int(o_flag) not in range(1,15):
            return False

    # check month
    if p.month() not in months_to_use:
        return False

    temp = p.t()
    tempqc = p.t_level_qc(originator=True)

    for i in range(len(temp)):
        # don't worry about levels with masked temperature
        if temp.mask[i]:
            continue

        # if temperature isn't masked:
        # it had better be a float
        if not isinstance(temp.data[i], np.float):
            return False
        # needs to have a valid QC decision:
        if tempqc.mask[i]:
            return False
        if not isinstance(tempqc.data[i], np.integer):
            return False
        if not tempqc.data[i] > 0:
            return False

    return True
     */
    return true;
  }

  public void process(AutoQcTestContextImpl testContext) throws Exception {
    Cast cast = testContext.getCast();
    AutoQcCastTestResult result = testContext.getCastTestResult();

    if (hasTemperature(cast)) {

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
      result.setSkipReason("No temperature in profile");

    }


  }
}
