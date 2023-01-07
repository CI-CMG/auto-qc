package edu.colorado.cires.wod.autoqc;

import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    File svcHome = new ApplicationHome().getDir();
    String path = svcHome.getAbsolutePath();
    System.setProperty("svc.home", path);
    System.setProperty("available.processors", Integer.toString(Runtime.getRuntime().availableProcessors()));
    SpringApplication.run(Application.class, args);
  }
}
