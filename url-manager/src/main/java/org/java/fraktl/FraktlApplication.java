package org.java.fraktl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.java.fraktl")
public class FraktlApplication {

  public static void main(String[] args) {
    SpringApplication.run(FraktlApplication.class, args);
  }

}
