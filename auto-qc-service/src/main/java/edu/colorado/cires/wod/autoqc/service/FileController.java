package edu.colorado.cires.wod.autoqc.service;

import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FileController {

  private final Map<String, CastProcessingContext> castsInFlight = new HashMap<>();

  @VisibleForTesting
  public synchronized void reset() {
    castsInFlight.clear();
  }

  @VisibleForTesting
  public synchronized CastProcessingContext getContext(String filePathPrefix) {
    return castsInFlight.get(filePathPrefix);
  }

  public synchronized void onNewFile(String filePathPrefix) {
    castsInFlight.put(filePathPrefix, new CastProcessingContext());
  }

  public synchronized void onNewCast(String filePathPrefix, Integer castNum, boolean last) {
    CastProcessingContext context = castsInFlight.get(filePathPrefix);
    if (context != null) {
      context.getCasts().add(castNum);
      if (last) {
        context.setComplete();
      }
    }
  }

  public synchronized boolean onDoneCast(String filePathPrefix, Integer castNum) {
    CastProcessingContext context = castsInFlight.get(filePathPrefix);
    if (context == null) {
      return true;
    }
    boolean done = false;
    context.getCasts().remove(castNum);
    if (context.isComplete() && context.getCasts().isEmpty()) {
      done = true;
      castsInFlight.remove(filePathPrefix);
    }
    return done;
  }

  @VisibleForTesting
  public static class CastProcessingContext {

    private final Set<Integer> casts = new HashSet<>();
    private boolean complete = false;

    @VisibleForTesting
    public Set<Integer> getCasts() {
      return casts;
    }

    private void setComplete() {
      complete = true;
    }

    @VisibleForTesting
    public boolean isComplete() {
      return complete;
    }
  }
}
