package cn.itrip.itripsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("cn.itrip")
public class ItripsearchApplication {

    public static void main(String[] args) {

        SpringApplication.run(ItripsearchApplication.class, args);
    }

}
