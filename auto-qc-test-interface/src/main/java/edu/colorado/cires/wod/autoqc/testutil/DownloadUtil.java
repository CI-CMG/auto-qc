package edu.colorado.cires.wod.autoqc.testutil;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

public class DownloadUtil {

  public static void download(RestTemplate restTemplate, String url, Path saveTo) {
    restTemplate.execute(url, HttpMethod.GET, null, clientHttpResponse -> {
      try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(saveTo))) {
        StreamUtils.copy(clientHttpResponse.getBody(), outputStream);
      }
      return null;
    });
  }

}
