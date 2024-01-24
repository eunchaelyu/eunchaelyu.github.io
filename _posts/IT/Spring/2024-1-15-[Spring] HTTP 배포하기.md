---
title: "[Spring] HTTP 배포하기"
author: eunchaelyu 
date: 2024-1-15 1:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240115'
---


# SpringBoot 백엔드 서버를 HTTP로 배포하기    

- 사용환경(Window > git bash)  
- AWS EC2 생성과 RDS 구매로 DB를 생성 및 연결 해놓은 상태     

## Step 1. RDS 구매하고 RDS 포트 열어주기  
- AWS -> RDS 접속    
- 데이터베이스 생성 > ``표준생성``과 ``MySQL`` 선택    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/274a9bbe-01dd-4ddd-8fbb-ed18a862b6df)    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f8828ad1-49dc-44a9-84ad-170b2b823b55)    

- ``프리티어`` 선택 > DB 인스턴스 식별자에 이름 적기 > 마스터 사용자 이름과 암호 적기(추후 DB 연결할 때도 사용)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b1fabbf4-106c-495b-8075-aee7f275f561)    

- 연결 > 추가 연결 구성 > 퍼블릭 액세스 기능 (``예``) > VPC 보안 그룹(``새로 생성``) > 새 VPC 보안 그룹 이름 설정 > 가용영역 선택   
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/315bda1a-ba9d-41d3-abf0-c5084fc4615e)

- 데이터 베이스 생성!!!    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3ff93d06-5978-42a6-acd0-44cab04dd94b)

- DB 클릭 > 연결 & 보안 > 보안 > VPC 보안 그룹 아래 링크 클릭    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/fd3acf0a-d390-415f-937c-96e8f497a809)

- 보안 그룹 ID > 인바운드 규칙 편집 
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/bbd865c8-f38c-4188-8be6-85f008cd2b5a)

- 소스 > 위치 무관 > 0.0.0.0/0, ::/0 생성 확인 후 ``규칙 저장``
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d6992528-f07d-4da4-b8f5-3b3c6bd4080e)

--------------------------------------------------------------------------------------------------------

## Step 2. MySQL이랑 연결        
- RDS 대시보드 > 엔드포인트 드래그 > 프로젝트 IntelliJ에서 열기    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/a3584672-4d47-4e1d-ba44-1f5c8c00d9e1)

- Data Source > MySQL > Name, Host(엔드포인트) User, Password, Database 이름 RDS와 동일한 설정으로 입력 > Test Connection    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ce4a55d3-e1b2-4ee5-8d72-e6c5f576878f)

- application.properties > ``spring.datasource.url=jdbc:mysql://엔드포인트:3306/Database이름`` > User, Password 설정

--------------------------------------------------------------------------------------------------------
## Step 3. AWS EC2 서버 사기    
- 서버(컴퓨터)의 설정을 할 예정 > OS로 리눅스의 Ubuntu를 설치
- 인스턴스 대시보드 > 인스턴스 시작 
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/a868fbf4-89d1-43be-99c5-3a7be02de116)

- Name 설정 > Ubuntu 선택    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/04649958-dffa-4234-9cdb-8de552bfa12a)

- 인스턴스 유형(프리티어)      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/37320ca8-156a-4d5d-8c4d-0cfb08cb36a4)

- 새 키페어 생성 > RSA > .pem > 키 페어 저장 위치 꼭 기억하기 > 인스턴스 시
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7f5a90d1-1783-433f-a3c6-3be61f7b9c2f)

--------------------------------------------------------------------------------------------------------    

## Step 4. AWS EC2에 접속하기 ( WINDOW ) 
- 접속할 컴퓨터가 22번 포트가 열려있어야 접속 가능하다. AWS EC2의 경우, 이미 22번 포트가 열려있다  
- gitbash 실행 > 아래 입력
-  ``ssh -i [키페어 끌어다 놓기] ubuntu@[IP주소]``

--------------------------------------------------------------------------------------------------------      

## Step 5. EC2 준비하고 배포하기
- 배포파일 빌드하기 > Intellij에서 우측 Gradle 클릭 & build 더블 클릭     

- (ssh로 접속한)Ubuntu에서 OpenJDK 설치하기(반드시 17v)    
    ``sudo apt-get update``    
    ``sudo apt-get install openjdk-17-jdk``    
    ``java -version``    

- Filezilla 이용해서 배포 파일 업로드하기    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dd43644f-4732-4fe0-ba98-86873bdd0abd)
        
- 호스트: IP 번호, 포트:22, 키파일: 키페어 경로 입력 후 연결
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/96cc863f-a04c-49ec-962e-7aa1c6b18c4c)    
- 좌측: 실행할 ``jar``파일 경로로 들어감(``-plain.jar`` 파일이 아닌 ``.jar``파일 더블 클릭)    
- 우측: 서버와 연결 후 ``ubuntu`` 경로로 자동 설정됨.      

- 스프링 부트 작동시키기
- ``java -jar house_backend-0.0.1-SNAPSHOT.jar``

- 웹에 접속하기
- 스프링부트가 정상 작동되면 아래의 주소 입력
- ``http://[IP주소]``

- 포트 포워딩(뒤에 포트번호 떼고 사용하기 위함)
- ``ctrl + c`` 사용해서 스프링 부트 실행 종료     
- ``sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080``

- 재시작
- ``java -jar house_backend-0.0.1-SNAPSHOT.jar``
- 접속하기 
- ``http://[IP주소]``
- 원격접속 종료하더라도 서버 계속 돌게 하기
  - ``nohup java -jar house_backend-0.0.1-SNAPSHOT.jar &``
## 서버 종료하기
  - 아래 명령어로 미리 pid 값(프로세스 번호)을 본다
  - ``ps -ef | grep java``
  - 아래 명령어로 특정 프로세스를 죽인다
  - ``kill -9 [pid값]``
## 다시 켜기 
  - ``nohup java -jar house_backend-0.0.1-SNAPSHOT.jar &``
