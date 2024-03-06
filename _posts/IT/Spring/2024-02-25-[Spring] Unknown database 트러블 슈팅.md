---
title: "[Spring] Unknown database 트러블 슈팅"
author: eunchaelyu
date: 2024-02-25 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240225'
---

    
# DB가 RDS와 연결되지 않는 error    

## Issue
- 엔드포인트:3306/RDS 생성시 DB이름 & USER & PASSWORD 제대로 입력했음에도 RDS와 DB 연결이 계속 실패        
- HTTP/HTTPS 배포시에는 DB연결에 아무 문제가 없었다    

• **서버가 잘 실행되는 듯 했으나**

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/45d60713-72e3-4831-ac9a-8e1b93fa79f1)

**Unknown database 오류가 계속발생했다**

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/35dad31f-0c07-41f5-ab57-956c6f98628d)


** 갑자기 Error 뜨는 이유가 CI/CD 구축하면서 DOKER 설치 등 여러 설정하면서 잘못 건드린게 있다고 생각해서 계속 “Doker 설치 후 RDS DB와 연결이 안되는 이유”에 대해서 이틀동안 구글링했다.. DB 연결이 안되는 것을 확인한 시점이 CI/CD 설정을 다 하고 나서 였기 때문이다.** 

** 나의 로직은 단순하게 아래와 같이 흘러갔다.    
DB 연결이 되지 않음(Unknown database 에러)         
→ 로컬 DB 확인, BUT eroom 데이터베이스 생성돼있음         
→ 엔드포인트 확인, BUT 문제없음         
→ 사용자 이름 및 비밀번호 확인, BUT 문제없음     
→ DOKER 설치 후 설정을 잘못 건드려서 RDS와 연결이 끊어진게 아닐까        
→ “Docker 컨테이너 상태 확인”       
→ “Docker 네트워크 설정”         
→ “MySQL 컨테이너 로그 확인”         
→ “Docker 컨테이너 내부에서 MySQL 데이터베이스 확인”         
→ “연결 설정 확인”         
→ “Spring Boot 어플리케이션 재시작”    
이런 식으로 하나하나 뜯어가면서 체크했지만 같은 오류가 무한 반복 되었다    

하지만 오늘 기술 멘토님과 멘토링을 진행하면서 DB 연결 했던 방식을 보여드리고 CI/CD 후 DB가 연결이 안됐던 이유에 대해서 내 나름대로 트러블 슈팅 해온 것들도 설명 드리면서     
DOKER와 RDS의 개념 분리(DB 연결 문제와 상관이 없음)와 로컬에 DB를 생성 및 연결하는 것과 RDS DB에 생성 및 연결하는 것의 차이를 깊게 생각하고 있지 않았다는 것이 문제였음을 깨닫게 되었다.     

- DB를 로컬에 연결하는 방식(내 로컬에만 저장된다 RDS와 연결되는 것이 X)**    

- cmd 창에 ``mysql -u root -p`` 명령어 입력 > 비밀번호 입력    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f6bb4761-4d8a-4c73-b727-2fb2f6791028)
 


```bash
mysql -u root -p
CREATE DATABASE mydatabase;
show databases;
use academy;
```

**- DB를 RDS와 연결하는 방식 (AWS RDS와 연결된다)**

- 연결이 되지 않는 DB 우측 클릭 > New > Query Console

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e7ea87f1-99f2-4489-b283-4402fccee0fc)

- create database eroom; (unknown 에러가 떴으니 우선 DB를 생성)

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7b46fb51-f66e-4021-b6bf-828240ca144b)

- 1 of 5 되어있는 부분 체크 > eroom DB 사용하도록 체크

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/93134365-a643-4e13-836b-00eecdfec89d)

- 호스트: 엔드포인트, USER, PASSWORD, PORT 확인 후 properties 파일과 DB에     
RDS에서 설정해준 내용과 동일하게 설정 후 TEST CONNECTION → Succeeded    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/58ead874-fd3a-421f-8d65-0e95173010b1)
