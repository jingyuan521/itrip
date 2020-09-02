package cn.itrip.itripauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("cn.itrip")
@MapperScan("cn.itrip.dao")
public class ItripauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItripauthApplication.class, args);
    }

}