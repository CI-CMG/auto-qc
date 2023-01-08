package edu.colorado.cires.wod.autoqc.test.argodate;


import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.wod.autoqc.test.AutoQcTest;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestContext;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import edu.colorado.cires.wod.autoqc.testutil.CastUtils;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "autoqc.test.argo-impossible-date", name = "enabled", havingValue = "true")
public class ArgoImpossibleDateCheck implements AutoQcTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgoImpossibleDateCheck.class);

  @Override
  public String getName() {
    return "Argo_impossible_date_test";
  }

  private static class HourMin {

    private final int hour;
    private final int min;


    private HourMin(int hour, int min) {
      this.hour = hour;
      this.min = min;
    }

    public int getHour() {
      return hour;
    }

    public int getMin() {
      return min;
    }
  }

  private static HourMin getTime(Cast cast) {
    double hoursWithFractionalHours = cast.getTime();
    int wholeHours = (int) hoursWithFractionalHours;

    double fractionalHours = hoursWithFractionalHours - (double) wholeHours;
    int minutes = (int) (60D * fractionalHours);

    return new HourMin(wholeHours, minutes);
  }

  private boolean isInvalidDate(Cast cast) {
    try {

      if (cast.getTime() != null) {
        HourMin hourMin = getTime(cast);
        LocalDateTime.of(
            cast.getYear(),
            cast.getMonth(),
            cast.getDay() == null ? 1 : cast.getDay(),
            hourMin.getHour(),
            hourMin.getMin()
        );
      } else {
        LocalDate.of(
            cast.getYear(),
            cast.getMonth(),
            cast.getDay() == null ? 1 : cast.getDay()
        );
      }
    } catch (DateTimeException e) {
      return true;
    }

    return false;
  }

  @Override
  public AutoQcTestResult test(AutoQcTestContext context) {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Testing ({}): {}", getName(), context.getCast().getCastNumber());
    }

    Cast cast = context.getCast();

    if (cast.getYear() < 1700) {
      return CastUtils.allBad(cast);
    }

    return CastUtils.all(cast, isInvalidDate(cast));

  }
}
