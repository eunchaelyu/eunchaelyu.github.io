---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(4)"
author: eunchaelyu
date: 2024-02-16 11:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (4)     
    10. 도커 컴포즈 파일 설정 -blue, green  
    11. GitHub ACTIONS 워크플로우 생성 
    12. GitHub ACTIONS 워크플로우 작성
    13. GitHub ACTIONS secrets 주입

## [10] 도커 컴포즈 파일 설정 - blue, green      

### 1. docker-compose-blue 파일 설정
```docker-compose-blue.yml
version: '3.8'

services:
  blue:
   image: eunchaelyu/eroom-prod:latest
   container_name: blue
   ports:
    - "8080:8080"
   environment:
    - PROFILES=blue
    - ENV=blue
```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7408e414-f1cf-47d8-a042-71b5ea8ea765)

### 2. docker-compose-green 파일 설정
```docker-compose-green.yml
version: '3.8'

services:
  green:
   image: eunchaelyu/eroom-prod:latest
   container_name: green
   ports:
    - "8081:8081"
   environment:
    - PROFILES=green
    - ENV=green
```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/72c846e9-a48f-455f-898e-29c6f9870626)

  
## [11] GitHub ACTIONS 워크플로우 생성       
- Github의 Actions 탭 > set up a workflow yourself      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/126dd3e2-1e0c-48a2-a222-a6b399eaad7b)        

- gradle.yml            
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7ef4f4e6-32e7-4afd-b1ad-660058400d37)        


## [12] GitHub ACTIONS 워크플로우 작성










    
## [13] GitHub ACTIONS secrets 주입    
- Github에 public 레포지토리 생성 > Setting > Secrets and variables > Actions 탭       
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8ebe5c94-ccb2-40d2-b641-b023ab25dd89)    
    
- New repository secret에 각각 추가 (Settings > Secrets and variables > Actions)        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/eaf7c92e-3170-407b-a096-3655dad36df9)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/43287c49-b8ed-46fd-9375-568b316f79f6)    

-  DOCKER_USERNAME: 도커 ID    
-  DOCKER_PASSWORD: 도커 계정 패스워드(토큰)    
-  HOST_PROD: prod 환경의 EC2 인스턴스 ip (EC2 탄력적 IP)    
-  PRIVATE_KEY: key.pem(ec2 생성시 발급받은 키페어)    
-  USERNAME: EC2 인스턴스 계정 ID(ec2-user)    
-  YML: application.yml 파일을 생성할 때 사용되는 값(Base64로 인코딩)    




