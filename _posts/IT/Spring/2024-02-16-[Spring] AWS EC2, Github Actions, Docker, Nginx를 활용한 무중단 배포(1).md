---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(1)"
author: eunchaelyu
date: 2024-02-16 07:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (1)     
 > 1. AWS EC2 인스턴스 생성 및 접속         
 > 2. EC2 Ubuntu에 도커 설치(docker, docker-compose)    
 > 3. 도커 허브 회원가입 후 토큰 발급 
 > 4. 도커 데스크탑 설치    

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
- 매번 sudo 명령어 입력을 생략하기 위해서 권한 부여한다             
```sudo su```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/237f254b-5142-4c8c-b2f4-0dc6d1a9469d)            

### 2. ubuntu 시스템 패키지 설치
- 업데이트  
```apt-get update```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f43e3789-94d2-4597-ab30-b38162bc36db)          

- 업그레이드        
``apt-get upgrade``    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5fc7aa90-f059-4da0-a8f6-0a098ebf2e39)    

### 3. 필요한 패키지 설치    
```apt-get install apt-transport-https ca-certificates curl gnupg-agent software-properties-common```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/1552c5ef-0a24-469b-ab5c-ddedc274207c)        

### 4. Docker의 공식 GPG키를 추가        
```curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -```      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4d5c7ead-fff7-4227-8ab0-c61125db57dd)    

### 5. Docker의 공식 apt 저장소를 추가    
```add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"```    

### 6. 시스템 패키지 업데이트    
```apt-get update```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5e3e26bd-ca63-4d64-8ce7-f66d3c3f5b9c)    

### 7. Docker 설치    
```apt-get install docker-ce docker-ce-cli containerd.io```    

### 8. Docker-Compose 설치    
```curl \ ``` 
    ```-L "https://github.com/docker/compose/releases/download/1.26.2/docker-compose-$(uname -s)-$(uname -m)" \
    -o /usr/local/bin/docker-compose```    
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f018ffb1-8efe-46bc-b68c-bf6a8363ee0f)    

### 9. Docker-Compose 실행 권한 주기    
```chmod +x /usr/local/bin/docker-compose```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d2edc2a8-deb0-4d31-b338-3b4246b12f16)    

- 기본적인 도커 설치가 완료됐다    

### 10. docker 버전 확인
```docker -v```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/969b9efa-221c-42fb-ac1e-9bbc50cc430f)    

### 11.  docker-compose 버전 확인    
```docker-compose -v```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/1d6b9830-d37a-4b9a-afe5-bf7cbf20a2df)


## [3] 도커 허브 회원가입 후 토큰 발급
- 회원가입- 완료된 상황
- 도커 허브의 패스워드로 사용될 Access Token 을 발급한다
- My account > Security
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7be682f6-8868-4bfc-87a2-c690fae8db0e)

- New Access Token > 토큰 이름 설정         
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/75ed5629-0dd5-48bc-a841-fb3f8f38cb9e)    

- 토큰은 유출되지 않게 지정된 폴더에 저장해둬야한다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8d925f73-6830-4213-8e5f-7907cd36731a)

- 본인은 deploy 폴더에 발급받은 토큰을 저장함     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8044bb17-5a68-40f3-8dee-0d600359c695)

  
## [4] 도커 데스크탑 설치        
- 도커 허브 하단에 Download Docker >         
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/15db1904-c6de-496f-af4e-4e1c299e0a67)    

- Docker Desktop for Windows 설치(환경에 맞게 설치)        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0cfe72d4-aa53-4b5f-9c94-51c8d9ce635a)            

- 도커 데스크탑 실행 후 로그인     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3dfae8d1-392d-4486-9e24-248d1f5962c9)    

- 터미널로 돌아와서 도커 로그인      
```docker login```      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d9be1578-f7e5-42ce-84d0-1a6523f786b7)

- 패스워드는 터미널창에서 보이지 않음 > 아까 발급받은 토큰 넣음    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/769cab2c-380f-4d16-8915-7b0c312c915a)




