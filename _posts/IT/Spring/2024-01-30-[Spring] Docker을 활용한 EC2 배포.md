---
title: "[Spring] Docker을 활용한 EC2 배포"
author: eunchaelyu
date: 2024-1-30 07:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240129'
---

# Docker을 활용한 EC2 배포     

## [1] Spring Boot 프로젝트 생성    

## [2] Dockerfile 생성    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/49b94445-f179-4166-a667-c7ff8ab2b6d9)    

  - 아래와 같이 파일 내용을 작성한다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e42929a3-f748-4209-a7fd-3c76eb4447e3)    

## [3] gradle build     
  - 터미널에서 ``./gradlew build -x test (-x test: 테스트 실행 X)`` 명령어를 통해서 build 실행
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







