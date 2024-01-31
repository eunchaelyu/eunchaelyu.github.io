---
title: "[Spring] Docker을 활용한 EC2 배포"
author: eunchaelyu
date: 2024-1-30 07:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240130'
---

# Docker을 활용한 EC2 배포 
# 배포 Flow
    1. dockerfile을 작성한 후, 빌드하여 docker image를 생성.    
    2. docker image 파일을 docker hub에 push.     
    3. 서버(AWS EC2)에서 docker hub에 있는 docker image를 pull.    
    4. docker run 명령어로 docker image를 실행.    

## [1] Spring Boot 프로젝트 생성    

## [2] Dockerfile 생성    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/49b94445-f179-4166-a667-c7ff8ab2b6d9)    

  - 아래와 같이 파일 내용을 작성한다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e42929a3-f748-4209-a7fd-3c76eb4447e3)    

## [3] gradle build     
  - Intellij 터미널에서 다음 명령어 실행
  - ``./gradlew build -x test (-x test: 테스트 실행 X)`` 명령어를 통해서 build 실행
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d4405252-7f99-40e5-bd49-7d143df8195f)


  - build > libs > jar 파일 생성된 것을 볼 수 있다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f355d296-bb75-46ae-b86f-ac3cd8866e7d)    

## [4] Docker Hub > 회원가입 > 로그인        
[Doker Hub 페이지](https://hub.docker.com/)    

## [5] Docker Hub Repository 생성      
  - Repositories > Create repository    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e49749ab-3096-428a-8688-859fa56ee323)

  - Repository 이름 > Public 설정 > Create 를 클릭    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7c349b52-2df8-4a15-ba90-d78b0211a87c)

## [6] Docker Image Build    
  - Intellij 터미널에서 다음 명령어 실행
```
# docker build --build-arg DEPENDENCY=build/dependency -t (도커 허브 ID)/(Repository 이름) .
docker build --build-arg DEPENDENCY=build/dependency -t eunchaelyu/eroom .
```

### 트러블 슈팅 1  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/72eeebf8-ce76-4c99-a51c-821ad6ea594b)
  - Docker Hub에 인증된 이미지로 명시하여 작성 (Dokerfile 아래와 같이 수정)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e459a9b3-0b02-4d15-b557-f2ee9b193200)


  - 수정 후     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/be01f42d-ad33-40bd-833d-0ad2c8db650d)


## [7] Docker Image push    
  - Intellij 터미널에서 다음 명령어 실행
  - ``docker push (도커 허브 ID)/(Repository 이름)``    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/330df5f6-8269-4ae6-8ab7-4904aab4ff79)

  - 아까 생성한 Repositorydml Tag 부분을 통해 push가 잘 된 것을 확인    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/cc5092d2-91df-45a2-a956-ee6f7ce590fa)

## [8] EC2에 Docker 설치 및 실행   
  - AWS EC2 인스턴스가 있다고 가정하고 진행

    |Ubuntu 환경|명령어|
    |:---:|:---:|
    패키지 업데이트 | sudo apt-get update -y
    기존에 있던 도커 삭제 | sudo apt-get remove docker docker-engine docker.io -y
    도커 설치 | sudo apt-get install docker.io -y
    docker 서비스 실행 | sudo service docker start
    /var/run/docker.sock 파일의 권한을 666으로 변경하여 그룹 내 다른 사용자도 접근 가능하게 변경 | sudo chmod 666 /var/run/docker.sock
    eunchaelyu 유저를 docker 그룹에 추가 | sudo usermod -a -G docker eunchaelyu

## [9] Docker image pull 및 애플리케이션 배포    
    sudo docker pull (도커 허브 ID)/(Repository 이름)    
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c0073a5f-cb8c-4a18-89ec-ae19fe40cd97)        

    sudo docker run -p 8080:8080 (도커 허브 ID)/(Repository 이름)       
    
### 트러블 슈팅 2  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ee680f65-9964-4dae-8719-e81428669ea0)   

  - 1) 사용자 지정 Dockerfile을 생성하여 Java를 포함하여 필요한 종속성을 갖춘 이미지를 사용해야함            
  - 2) 애플리케이션을 실행하는 데 필요한 실제 JAR 파일로 바꿔줘야 한다
       
```
# open jdk 17 버전의 환경을 구성
FROM openjdk:17-alpine

# build가 되는 시점에 JAR_FILE이라는 변수 명에 build/libs/Eroom-Project-BE-0.0.1-SNAPSHOT.jar 선언
# build/libs - gradle로 빌드했을 때 jar 파일이 생성되는 경로
ARG JAR_FILE=build/libs/Eroom-Project-BE-0.0.1-SNAPSHOT.jar

# JAR_FILE을 app.jar로 복사
COPY ${JAR_FILE} Eroom-Project-BE.jar

# 운영 및 개발에서 사용되는 환경 설정을 분리
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/Eroom-Project-BE.jar"]
```
    Dokerfile 위와 같이 수정해서 적음
    - open jdk java17 버전의 환경 구성
    - build 되는 시점에 JAR_FILE 경로에 jar 파일 생성
    - JAR_FILE을 sejongmate.jar에 복사
    - jar 파일 실행 명령 (여기서 -Dspring.profiles.active=prod 옵션은 application.yml을 개발 환경에서 따로 분리한 것)

  - 최종 배포 화면        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/87ee1c4e-0aa7-4041-a5e7-c0545c410313)    


