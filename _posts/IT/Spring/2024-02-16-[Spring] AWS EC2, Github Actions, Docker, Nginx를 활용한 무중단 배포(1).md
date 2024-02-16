---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(1)"
author: eunchaelyu
date: 2024-02-16 07:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 과정      
    1. AWS EC2 인스턴스 생성 및 접속         
    2. EC2 Ubuntu에 도커 설치(docker, docker-compose)    
    3.     

## [1] AWS EC2 인스턴스 생성 및 접속- 완료한 상황      
- AWS에서 인스턴스 생성 완료한 상황 (ubuntu, 프리티어)   
- 키페어는 아래와 같이 .ssh 폴더에 관리    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/eb582e79-2dfd-457e-ac4b-32bee33320b3)    

- Ubuntu 22.04.3 LTS > 마우스 우측 클릭 > 관리자 권한으로 실행
- ssh -i 키페어 위치 끌어다 놓기 ubuntu@탄력적public-ip    
- ```ssh -i ~/.ssh/eroom_key.pem ubuntu@44.219.159.74```        
- 명령어 사용해서 EC2에 접속함

## [2] EC2 Ubuntu에 도커 설치
### 1. 관리자 권한     
- 매번 sudo 안 치기 위해서 권한을 부여하면 된다       
```sudo su```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/237f254b-5142-4c8c-b2f4-0dc6d1a9469d)        

### 2. ubuntu 시스템 패키지 설치
- 업데이트  
```apt-get update```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f43e3789-94d2-4597-ab30-b38162bc36db)          

- 업그레이드        
``apt-get upgrade``    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5fc7aa90-f059-4da0-a8f6-0a098ebf2e39)    

### 3. 
