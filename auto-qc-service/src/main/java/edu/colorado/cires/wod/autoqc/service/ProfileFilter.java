package edu.colorado.cires.wod.autoqc.service;

import edu.colorado.cires.mgg.wod.data.model.Attribute;
import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.mgg.wod.data.model.ProfileData;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.springframework.stereotype.Service;

@Service
public class ProfileFilter {

  public boolean allowProfile(Cast cast) {

    // not interested in standard levels
    if (cast.getProfileType() == (short) 1) {
      return false;
    }

    for (Depth depth : cast.getDepths()) {
      if (depth.getTemperature().isPresent()) {
        break;
      }
      // no temperature data in profile
      return false;
    }

    if (cast.getDepths().size() > 1) {
      for (Depth depth : cast.getDepths()) {
        if (depth.getDepth() >= 0.1) {
          break;
        }
        // all depths are less than 10 cm and there are at least two levels (ie not just a surface measurement)
        return false;
      }
    }

    // TODO make this conditional ?
//    if check_originator_flag_type:
    Optional<Long> maybeFlag = cast.getAttributes().stream()
        .filter(a -> a.getCode() == 96)
        .map(Attribute::getValue)
        .map(Math::round)
        .findFirst();
    if (maybeFlag.isPresent() && (maybeFlag.get() < 1 || maybeFlag.get() > 14)) {
      // no valid originator flag type
      return false;
    }

    // TODO make this configurable ?
    if (cast.getMonth() < 1 || cast.getMonth() > 12) {
      return false;
    }

            /*



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

    // TODO verify this is correct
    for (Depth depth : cast.getDepths()) {
      Optional<ProfileData> maybeTemp = depth.getTemperature();
      if (maybeTemp.isPresent()) {
        ProfileData tempData = maybeTemp.get();
        if (tempData.getOriginatorsFlag() != 0) {
          return false;
        }
      }
    }

    //TODO ?
    /*
        # temperature data is in profile but all masked out
    if np.sum(p.t().mask == False) == 0:
        return False
     */

    return true;

  }
}
