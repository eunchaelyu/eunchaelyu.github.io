---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(6)"
author: eunchaelyu
date: 2024-02-16 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (6)     
     15. AWS 설정
     16. 

    
## [15] AWS 설정   
### 1. 인바운드 규칙
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/066b3762-191c-453e-9039-7cb99d717215)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d0f145cf-0a24-4c9e-be72-f7b71cfcba5c)    

- 이 요청을 날리는 대상이 GitHubActions ubuntu ! 즉, 외부 서버이다.     
- 외부 서버에서는 해당 8080, 8081 접속이 안된다    
- 왜냐하면 EC2에서 포트 설정을 안해줬기 때문이다

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/916ba4ca-d530-4f48-b9de-cfb8aeed27c8)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9e9a2cf1-683e-4e2b-92f7-57f88d01aea5)

- 현재 보안그룹 > 해당 인스턴스가 launcha-wizard-2로 잡혀있다( HTTP 80은 열려있다, HTTP 8081, HTTP 8080 열어줘야한다 )    

### 2. Route 53  
