---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(3)"
author: eunchaelyu
date: 2024-02-16 09:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (3)     
    7. 스프링부트(Springboot) 프로젝트 설정      
    8. HealthCheck 컨트롤러 생성  
    9. application.yml 세팅    

                  

## [7] 스프링부트(Springboot) 프로젝트 설정      
- 스프링부트 gradle로 프로젝트 생성 및 진행한다          
- buid.gradle > 아래 코드 추가(build 시 plain 스냅샷 생성 방지 위함)            
```
jar {
	enabled = false
}
```

build.gradle의 dependencies 설정        
```
dependencies {
	implementation 'javax.xml.bind:jaxb-api:2.3.1'
	implementation 'org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE'

	//validation
	implementation 'org.springframework.boot:spring-boot-starter-validation'


	// JWT
	compileOnly group: 'io.jsonwebtoken', name: 'jjwt-api', version: '0.11.5'
	runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-impl', version: '0.11.5'
	runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-jackson', version: '0.11.5'


	// Security
	implementation 'org.springframework.boot:spring-boot-starter-security'

	runtimeOnly 'com.h2database:h2'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok'
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'

	implementation 'mysql:mysql-connector-java:8.0.27'
```


## [8] HealthCheck 컨트롤러 생성
- 그린서버나 블루 서버가 실행될 때 잘 돌아가고 있는지 확인 용도로 사용한다    
- green 서버 실행시    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d5f0de42-400f-409d-a71d-b6136aeb4e20)    

- blue 서버 실행시    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/de06d9b5-567d-4a43-883a-da97f7b05093)    

- HealthCheckController 파일   
```
package com.sparta.eroomprojectbe.domain.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.serverAddress}")
    private String serverAddress;

    @Value("${serverName}")
    private String serverName;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> responseData = new TreeMap<>();
        responseData.put("serverName", serverName);
        responseData.put("serverPort", serverPort);
        responseData.put("serverAddress", serverAddress);
        responseData.put("env", env);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }
}
```    







