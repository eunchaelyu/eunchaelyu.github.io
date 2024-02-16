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
- 그린서버나 블루 서버가 실행될 때 잘 돌아가고 있는지 아래와 같이 확인 용도로 사용한다    
- green 서버 실행시    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d5f0de42-400f-409d-a71d-b6136aeb4e20)    

- blue 서버 실행시    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/de06d9b5-567d-4a43-883a-da97f7b05093)    

- HealthCheckController 파일   
```java
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

### [9] application.yml 세팅 
- 기본 설정 (각각 다른 파일에 있는 내용이 아니라 ``---``로 한 파일 내에서 구분 가능)    
- 로컬 그룹은 로컬호스트의 8080 포트로 잡힌다            
- 블루, 그린 그룹은 각각 public ip의 8080,8081 포트로 잡힌다              
```yml
spring:
  profiles:
    active: local
    group:
      local: local, common, secret
      blue: blue, common, secret
      green: green, common, secret

server:
  env: blue
```

- local 서버 설정      
```yml
spring:
  config:
    activate:
      on-profile: local
kakao:
  redirect-uri: ---

server:
  port: 8080
  serverAddress: localhost

serverName: local_Server
```

- blue 서버 설정
```yml

spring:
  config:
    activate:
      on-profile: blue
kakao:
  redirect-uri: ---

server:
  port: 8080
  serverAddress: 44.219.159.74

serverName: blue_Server
```

- green 서버 설정
```yml
spring:
  config:
    activate:
      on-profile: green

kakao:
  redirect-uri: ---

server:
  port: 8081
  serverAddress: 44.219.159.74

serverName: green_Server
```

- common 공통 파일 설정
```yml
spring:
  config:
    activate:
      on-profile: common
kakao:
  client-id: ---
  client-secret: ---
```

- secret과 관련된 모든 파일
```yml
jwt:
  secret:
    key: ---

spring:
  data:
    redis:
      host: ${REDIS_HOSTNAME}
      port: ${REDIS_PORT}
  config:
    activate:
      on-profile: secret
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ---
    url: jdbc:mysql://---:3306/---
    password: ---
#    username: ---
#    url: jdbc:mysql://localhost:3306/---
#    password: ---
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: 'true'
        use_sql_comments: 'true'
        show_sql: 'true'
    hibernate:
      ddl-auto: update
  # Multipart
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# AWS S3
cloud:
  aws:
    s3:
      bucket: ---
    credentials:
      access-key: ---
      secret-key: ---
    region:
      static: us-east-1
      auto: false
    stack:
      auto: false
```


