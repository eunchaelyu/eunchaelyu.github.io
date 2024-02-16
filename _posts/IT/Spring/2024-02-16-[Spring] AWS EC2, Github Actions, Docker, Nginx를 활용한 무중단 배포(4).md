---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(4)"
author: eunchaelyu
date: 2024-02-16 10:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (4)     
    10. 도커 컴포즈 파일 설정 -blue, green  


## [10] 도커 컴포즈 파일 설정 - blue, green      

### 1. docker-compose-blue 파일 설정
```docker-compose-blue.yml
version: '3.8'

services:
  blue:
   image: eunchaelyu/eroom-prod:latest
   container_name: blue
   ports:
    - "8080:8080"
   environment:
    - PROFILES=blue
    - ENV=blue
```

### 2. docker-compose-green 파일 설정
```docker-compose-green.yml
version: '3.8'

services:
  green:
   image: eunchaelyu/eroom-prod:latest
   container_name: green
   ports:
    - "8081:8081"
   environment:
    - PROFILES=green
    - ENV=green
```
    
