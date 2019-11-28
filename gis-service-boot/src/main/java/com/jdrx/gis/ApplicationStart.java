package com.jdrx.gis;

import com.jdrx.gis.config.MySwaggersConfig;
import com.jdrx.gis.config.PGConfigProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * spring boot 启动类
 * Created by wjm on 2017/5/5.
 */

@ComponentScan(basePackages = "com.jdrx")
@SpringBootApplication
@MapperScan({"com.jdrx.gis.dao.*","com.jdrx.share.*"})
@ImportAutoConfiguration({PGConfigProperties.class, MySwaggersConfig.class})
public class ApplicationStart {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStart.class, args);
    }
}
