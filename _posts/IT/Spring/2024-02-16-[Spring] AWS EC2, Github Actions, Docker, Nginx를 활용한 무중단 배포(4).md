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
    11. 도커파일 작성(Dockerfile)
    12. GitHub ACTIONS 워크플로우 생성 
    13. GitHub ACTIONS 워크플로우 작성
    14. GitHub ACTIONS secrets 주입

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

## [11] 도커파일 작성(Dockerfile)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e9bfac5d-940d-443a-bb22-3b5a703a057c)    
```
# open jdk 17 버전의 환경을 구성
FROM openjdk:17-alpine

# build가 되는 시점에 JAR_FILE이라는 변수 명에 build/libs/Eroom-Project-BE-0.0.1-SNAPSHOT.jar 선언
# build/libs - gradle로 빌드했을 때 jar 파일이 생성되는 경로
ARG JAR_FILE=build/libs/Eroom-Project-BE-0.0.1-SNAPSHOT.jar
ARG PROFILES
ARG ENV

# JAR_FILE을 app.jar로 복사
COPY ${JAR_FILE} Eroom-Project-BE-0.0.1-SNAPSHOT.jar

# 운영 및 개발에서 사용되는 환경 설정을 분리
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}" , "-Dserver.env=${ENV}", "-jar" , "/Eroom-Project-BE-0.0.1-SNAPSHOT.jar"]
```

- dockerfile 작성 필요(이미지를 만들기 위해서)    
- 프로젝트들을 그대로 가지고 DOCKER 이미지가 하나 만들어진다    
- 서버에서 DOCKER-COMPOSE 설정했던 profiles,env 를 넣어준다        

- ```"-Dspring.profiles.active=${PROFILES}"```의 의미는 "application.yml에서 작성해준 spring의 profiles 의 active"가 blue or green인지에 따라    
- "docker-compose에 설정된 profiles"가 달라지기 때문에 blue or green 서버가 열리게 된다는 뜻이다    

- ```"-Dserver.env=${ENV}"```의 의미는 "application.yml에서 작성해준 spring의 profiles 의 active"가 blue or green인지에 따라    
- "docker-compose에 설정된 env"가 달라지기 때문에 blue or green 서버가 열리게 된다는 뜻이다    

- ```ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILES}" , "-Dserver.env=${ENV}", "-jar" , "/Eroom-Project-BE-0.0.1-SNAPSHOT.jar"]```
- 이 코드는 우리가 ubuntu에서 jar 파일을 실행할 때 사용하는 ```java -jar Eroom-Project-BE-0.0.1-SNAPSHOT.jar``` 
- 이 명령어를 띄어쓰기별로 적어준 것이고 추가로 적어준 profiles와 env는 옵션이라고 할 수 있다    


  
## [12] GitHub ACTIONS 워크플로우 생성       
- Github의 Actions 탭 > set up a workflow yourself

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/126dd3e2-1e0c-48a2-a222-a6b399eaad7b)        

- gradle.yml 생성 완료!    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/268fdff3-0418-4035-b52d-0bd3c0ef5815)    
      


## [13] GitHub ACTIONS 워크플로우 작성
- 단계별로 뜯어서 코드를 이해해보자

### STEP 1            
```
# github repository actions 페이지에 나타날 이름
name: CI/CD

# event trigger
# master브랜치에 push가 되었을 때 실행
on:
  push:
    branches: [ "master" ]

permissions:
  contents: read
```
- master에 push하면 actions 활성화가 된다
- permissions는 읽기에 권한을 준다는 뜻! 

### STEP 2    
```
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # JDK setting
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
```
- jobs는 쉽게 말해서 하나의 일하는 단위!!
- build가 정상적으로 실행 되면 -> deploy 순차적으로 과정이 진행이 되고              
- runs-on 이 build, deploy에 각각 있는데 ubuntu-latest(가상 PC)가 실행이 될 것이라는 뜻이다                
- 가장 보편적이고 가볍기 때문에 이 운영체제(ubuntu)를 서버로 사용한다    

- Github Actions용 Java 프로젝트를 빌드하거나 실행하기 위한 JDK 버전 설정(JDK 17)        
- uses: actions/checkout@v3: 새로운 코드 변경사항을 가져오기 위한 액션    
         
### STEP 3   
```
      - name: Build with Gradle
        run: |
          mkdir -p ./src/main/resources
          echo ${{ secrets.YML }} | base64 --decode  > ./src/main/resources/application.yml
          cat ./src/main/resources/application.yml
          chmod +x ./gradlew
          ./gradlew build -x test
```
- /src/main/resources 디렉토리를 생성하여 YML 파일을 디코딩하여 가지고 온다           
- application.yml은 Secrets에 저장된 값으로 Base 64로 인코딩된 값이 들어가 있기 때문에 디코딩 작업을 한다        
- gradlew 스크립트에 실행 권한 부여 후 build 된다    
- x test 옵션은 테스트를 실행하지 않도록 설정        
- jar 파일이 만들어진다    

### STEP 4    
```
      - name: Login to DockerHub
        uses: docker/login-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
```
- jar 파일을 ubuntu에서 만들었기 때문에 도커 로그인을 ubuntu에서 한다    

### STEP 5    
```
      - name: Build Docker
        run: docker build --platform linux/amd64 -t ${{ secrets.DOCKER_USERNAME }}/eroom-prod .
      - name: Push Docker
        run: docker push ${{ secrets.DOCKER_USERNAME }}/eroom-prod:latest
```
- jar 파일을 스냅샷을 찍어서 이미지로 만든다    
> eroom-prod:latest라는 레포지토리로 도커 허브에 보낸다(push)

### STEP 6






    
## [14] GitHub ACTIONS secrets 주입    
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




