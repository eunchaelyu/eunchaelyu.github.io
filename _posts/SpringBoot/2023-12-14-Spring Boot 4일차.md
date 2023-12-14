---
title: Spring Boot 4일차(디렉터리 종류)
author: eunchaelyu
date: 2023-12-14 19:30:00 +0900
categories: [주특기, Spring Boot]
tags: [Spring Boot, 개발]
pin: true
img_path: '/posts/20231213'
---

# '야금야금' 스프링 부트 3일차src/main/resources 디렉터리    

|src/main/java|  
|:------:|
|컨트롤러, 폼과 DTO, 데이터 베이스 처리를 위한 엔티티, 서비스 파일|

|src/main/resources| 
|:-----:|
|자바 파일을 제외한 HTML, CSS, Javascript, 환경파일 등을 작성|  

|templates|
|:-----:|
|템플릿, SBB의 질문 목록, 질문 상세 등의 HTML 파일 저장|

|static|
|:-----:|
|``.css``, ``.js``,``.jpg``, ``.png``) 등을 저장|

|application.properties|
|:-----:|
|SBB 프로젝트의 환경, 데이터베이스 등의 설정|

|src/test/java|
|:-----:|
|서버를 실행하지 않은 상태에서 src/main/java 디렉터리에 작성한 코드를 테스트|

|build.gradle|
|:-----:|
|필요한 플러그인과 라이브러리 등을 기술|
