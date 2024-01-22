---
title: "[TroubleShooting] HTTPS 배포하기"
author: eunchaelyu
date: 2024-1-15 1:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240115'
---


# SpringBoot 백엔드 서버를 HTTPS로 배포하기  

  - 작업 환경(Window)    
  : HTTPS는 암호화된 HTTP프로토콜로서 양방향 안전하게 데이터 통신이 가능함        
  로그인/회원가입과 같이 security 관련 데이터들을 다루기 위해서는  ``HTTPS``프로토콜이 필수적이다.        
  서버 또한 암호화된 서버로 운영하는 것이 중요하다.    
  그렇기 때문에 클라우드 서비스를 제공하는 AWS를 이용하려고 한다.        
  이미 수없이 많은 데이터, 서버를 다루고 있고 신뢰성이 있고 안전하기 때문에 선택했다.          
  도메인 생성부터 SSL 인증서 발급, HTTP 프로토콜 적용까지 한 번에 이 서비스들을 처리할 수 있어서 매우 편리하다.                
  여기서 다루는 로드밸런스는 클라이언트의 요청을 HTTPS(443)인지 HTTP(80)인지 판단한다.          
  HTTP 요청이라면 HTTPS 443으로 리다이렉트한다. HTTPS 요청이라면 대상 그룹의 포트 80으로 포워딩한다.            

**시작 전 주의사항: HTTP 배포를 버지니아 북부 region으로 설정하고 생성했는지 다시 확인**        
  ->  인증서 발급 받으려면 반드시 필요        
  [인증서 발급 필수 사항](https://repost.aws/ko/knowledge-center/migrate-ssl-cert-us-east){:target="_blank"}

## 1. 준비 단계    
  - RDS 구매하고 MYSQL 연결하기(데이터 베이스 이름과 사용자 이름, 비밀번호 기억해야함)    
  - EC2 서버 구매하기    
  - AWS EC2에 접속하기    
  - 배포 파일 Build하고 EC2 준비하기(HTTP 배포하기 게시글과 순서 동일)    

## 2. Spring Boot + AWS EC2 도메인 연결 및 HTTPS 적용        
  [도메인 연결 블로그](https://un-lazy-midnight.tistory.com/172#%EB%8F%84%EB%A9%94%EC%9D%B8%EA%B3%BC%20EC2%20%EC%97%B0%EA%B2%B0-1){:target="_blank"}     
  - Route53에 들어가서 호스팅 영역 생성(도메인 이름 입력하기)    
  - 레코드 세트 생성 ``값``에 EC2 IP 연결        
  - 레코드 생성하는 것까지만 참고 후 SSL 인증서 발급 단계로 넘어감    


## 3. EC2 HTTPS 및 로드밸런서 적용        
  [HTTPS 및 로드밸런스 블로그](https://jindevelopetravel0919.tistory.com/192){:target="_blank"}
  - ACM(amazon certificate manager) 에서 SSL 인증서 발급 및 호스팅 영역에 레코드 등록    
    : 외부 무료 도메인 사이트가 있지만 AWS DNS 유료 서비스를 이용하여 도메인 생성함 -> AWS에서 생성한 도메인이기 때문에 DNS 검증이 자동적으로 됨.
    : 따로 EC2서버에 nginx나 cerbot과 같은 인증서를 직접 설치하고 설정하는 과정을 생략하기 위해 직접 인증서 발급함.

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5cf7667f-19b7-4f11-a92a-193724af624e)
   
  - Target group 생성 (대상 그룹)
    
**참고한 블로그랑 다른 점**    
  -SPRING BOOT로 로컬환경에서 8080포트를 사용하고 있으므로 80포트 대상그룹으로 포워딩 해주는 작업을 생략하고 아래와 같이 설정    
 
  - HTTP 요청이면 이 요청을 HTTPS 요청으로 Redirection(EC2 > 로드 밸런서 > neeksLoadBalancer > HTTP: 8080 리스너)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b3b0e9c3-3253-40a5-bdc3-0dfd2d241557)

  - HTTPS 요청이면 기본값만 세팅(EC2 > 로드 밸런서 > neeksLoadBalancer > HTTPS: 443 리스너)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ea9328f1-b7c1-4450-a2fa-184b675a5a78)

     
  - application load balancer 생성 및 security 정책 설정        
  - route53의 domain A 레코드 변경ACM SSL 인증서 발급(region ``버지니아 북부``)        

  - HTTPS 도메인 연결 완료시 화면
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6228f09d-78ad-4f94-8d32-99ff54871067)


