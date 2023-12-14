---
title: Spring Boot 3일차(STS4 설치 및 설정)
author: eunchaelyu
date: 2023-12-13 22:30:00 +0900
categories: [주특기, Spring Boot]
tags: [Spring Boot, 개발]
pin: true
img_path: '/posts/20231213'
---

# '야금야금' 스프링 부트 3일차

### Spring Initializr 설치    
> Spring Initializr를 사용하면 스프링부트 개발을 쉽게 시작 가능, 스프링부트 프로젝트 생성 도와줌

[Spring Initializr 설치](https://start.spring.io/)

### STS4 설치 및 설정하기(Spring Boot Suit4)    

[설치 참조 블로그](https://kjchoi.co.kr/17)

### 인텔리제이 사용하기     

[인텔리제이 사용방법 링크](https://wikidocs.net/164891)

### src/main/java 디렉터리    
- src/main/java 디렉터리의 com.mysite.sbb 패키지는 자바 파일을 작성하는 공간이다.    
- 자바 파일로는 HelloController와 같은 스프링부트의 컨트롤러, 폼과 DTO, 데이터 베이스 처리를 위한 엔티티, 서비스 파일등이 있다.    

### SbbApplication.java 파일    
- 모든 프로그램에는 시작을 담당하는 파일이 있다.    
- 스프링부트 애플리케이션에도 시작을 담당하는 파일이 있는데 그 파일이 바로 <프로젝트명> + Application.java 파일이다.    
- 스프링부트 프로젝트를 생성할때 "Sbb"라는 이름을 사용하면 다음과 같은 SbbApplication.java 파일이 자동으로 생성된다.    
``[파일명:/sbb/src/main/java/com/mysite/sbb/SbbApplication.java]``    





