---
title: "[TroubleShooting] Jsoup 라이브러리를 이용한 웹 크롤링"
author: eunchaelyu
date: 2024-1-16 9:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240122'
---


#   Jsoup 라이브러리를 이용한 웹 크롤링

## **Issue** 
뉴닉 이라는 웹사이트를 클론코딩하는 프로젝트를 FE-BE와 진행하면서 로그인/회원가입 구현 스켈레톤 코딩 후 
메인페이지 CRUD와 웹 크롤링으로 자료를 띄우는 기능을 맡았다.   
처음에 뉴닉 웹 사이트에서 웹 크롤링을 할 수 있을 줄 알고 코드를 거의 다 작성해가는데 문제가 발생했다.    
웹 크롤링이 가능한지 판단할 때 ``웹 사이트/robots.txt``를 url 검색에 쳐봐야 한다는 걸 뒤늦게 깨달았다.    
아래 사진처럼 ``https://newneek.co/robots.txt`` 이라고 검색했을 때 모든 User에 대해서 ``User-agent: *Disallow:`` 허락하지 않는다. 
즉, 웹 크롤링이 불가능하다는 것을 알 수 있다.
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/000d5fa7-a5a7-44b3-b94e-eec05bdce8e9)


## **해결방법**    
웹 크롤링이 가능한 뉴스 웹사이트에서 카테고리별 뉴스 기사를 스크래핑 해온다    


## 1. Jsoup 라이브러리 설치     
### build.gradle    
``implementation 'org.jsoup:jsoup:1.14.3'``    
- Jsoup은 HTML 문서에서 데이터를 추출하고 조작하기 위한 편리한 API를 제공한다
- CSS Selector 문법을 사용하여 특정 HTML 요소를 선택해서 필요한 정보를 가져올 수 있게 한다
