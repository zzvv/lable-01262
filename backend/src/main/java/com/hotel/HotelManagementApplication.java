package com.hotel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 酒店管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.hotel.mapper")
public class HotelManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelManagementApplication.class, args);
        System.out.println("====================================");
        System.out.println("  酒店管理系统启动成功！");
        System.out.println("  Swagger: http://localhost:8080/swagger-ui/");
        System.out.println("====================================");
    }
}
