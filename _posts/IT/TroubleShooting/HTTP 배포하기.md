---
title: "[TroubleShooting] HTTP 배포하기"
author: eunchaelyu
date: 2024-1-15 1:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240115'
---


# SpringBoot 백엔드 서버를 HTTP로 배포하기    

- 사용환경(Window -> git bash)  
- AWS EC2 생성과 RDS 구매로 DB를 생성 및 연결 해놓은 상

## 1. AWS EC2에 접속하기    
  ssh -i [키페어 끌어다 놓기] ubuntu@[IP주소]    
  
## 2. 배포파일 빌드하기     
  Intellij에서 우측 Gradle 클릭 & build 더블 클릭     

## 3. (ssh로 접속한)Ubuntu에서 OpenJDK 설치하기(반드시 17v)    
    ``sudo apt-get update``    
    ``sudo apt-get install openjdk-17-jdk``    
    ``java -version``    

4. Filezilla 이용해서 배포 파일 업로드하기    
  ![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dd43644f-4732-4fe0-ba98-86873bdd0abd)        
  - 호스트: IP 번호, 포트:22, 키파일: 키페어 경로 입력 후 연결      
  ![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/96cc863f-a04c-49ec-962e-7aa1c6b18c4c)    
  - 좌측: 실행할 ``jar``파일 경로로 들어감(``-plain.jar`` 파일이 아닌 ``.jar``파일 더블 클릭)    
  - 우측: 서버와 연결 후 ``ubuntu`` 경로로 자동 설정됨.      

5. 스프링 부트 작동시키기
  - ``java -jar house_backend-0.0.1-SNAPSHOT.jar``

6. 웹에 접속하기
  - 스프링부트가 정상 작동되면 아래의 주소 입력
  - ``http://[IP주소]``

7. 포트 포워딩(뒤에 포트번호 떼고 사용하기 위함)
  - ``ctrl + c`` 사용해서 스프링 부트 실행 종료     
  - ``sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080``

8. 재시작
  - ``java -jar house_backend-0.0.1-SNAPSHOT.jar``
9. 접속하기 
  - ``http://[IP주소]``
10. 원격접속 종료하더라도 서버 계속 돌게 하기
  - ``nohup java -jar house_backend-0.0.1-SNAPSHOT.jar &``
11. 서버 종료하기
  - 아래 명령어로 미리 pid 값(프로세스 번호)을 본다
  - ``ps -ef | grep java``
  - 아래 명령어로 특정 프로세스를 죽인다
  - ``kill -9 [pid값]``
12. 다시 켜기 
  - ``nohup java -jar house_backend-0.0.1-SNAPSHOT.jar &``
