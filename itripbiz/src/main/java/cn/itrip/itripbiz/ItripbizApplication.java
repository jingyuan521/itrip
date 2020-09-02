package cn.itrip.itripbiz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("cn.itrip")
@MapperScan("cn.itrip.dao")
@SpringBootApplication
public class ItripbizApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItripbizApplication.class, args);
    }

}
