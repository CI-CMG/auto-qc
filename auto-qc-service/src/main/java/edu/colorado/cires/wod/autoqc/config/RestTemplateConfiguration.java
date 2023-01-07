package edu.colorado.cires.wod.autoqc.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) throws NoSuchAlgorithmException, KeyManagementException {
// TODO make configurable
//    TrustManager[] trustAllCerts = new TrustManager[] {
//        new X509TrustManager() {
//          public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
//          }
//          public void checkClientTrusted(
//              X509Certificate[] certs, String authType) {
//          }
//          public void checkServerTrusted(
//              X509Certificate[] certs, String authType) {
//          }
//        }
//    };
//    SSLContext sslContext = SSLContext.getInstance("SSL");
//    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
    CloseableHttpClient httpClient = HttpClients.custom()
//        .setSSLContext(sslContext)
//        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        .build();
    HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory();
    customRequestFactory.setHttpClient(httpClient);
    customRequestFactory.setConnectionRequestTimeout(60000); //TODO make configurable
    customRequestFactory.setReadTimeout(10 * 60000); //TODO make configurable
    return builder.requestFactory(() -> customRequestFactory).build();

  }

}
