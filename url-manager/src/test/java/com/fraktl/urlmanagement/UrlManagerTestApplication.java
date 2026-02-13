package com.fraktl.urlmanagement;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackages = {
    "com.fraktl.urlmanagement",
    "com.fraktl.common"
})
@EnableAutoConfiguration
public class UrlManagerTestApplication {
}