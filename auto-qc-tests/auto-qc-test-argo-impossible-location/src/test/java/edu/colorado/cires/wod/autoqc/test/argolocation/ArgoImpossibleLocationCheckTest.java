package edu.colorado.cires.wod.autoqc.test.argolocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.mgg.wod.data.model.Cast;
import edu.colorado.cires.mgg.wod.data.model.Depth;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestDepthResult;
import edu.colorado.cires.wod.autoqc.test.AutoQcTestResult;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ArgoImpossibleLocationCheckTest {

  @ParameterizedTest
  @CsvSource({"-180.1,0,true", "180.1,0,true", "0,-90.1,true", "0,90.1,true", "40,50,false"})
  public void test(double lon, double lat, boolean failed) throws Exception {
    Cast cast = Cast.builder()
        .setLongitude(lon)
        .setLatitude(lat)
        .setPrincipalInvestigators(Collections.emptyList())
        .setAttributes(Collections.emptyList())
        .setBiologicalAttributes(Collections.emptyList())
        .setTaxonomicDatasets(Collections.emptyList())
        .setCastNumber(123)
        .setDepths(Collections.singletonList(Depth.builder().setDepth(0D).build()))
        .build();

    List<Boolean> expected = Collections.singletonList(failed);

    ArgoImpossibleLocationCheck test = new ArgoImpossibleLocationCheck();
    AutoQcTestResult result = test.test(() -> cast);
    assertEquals(expected, result.getDepthResults().stream().map(AutoQcTestDepthResult::isFailed).collect(Collectors.toList()));
  }

}