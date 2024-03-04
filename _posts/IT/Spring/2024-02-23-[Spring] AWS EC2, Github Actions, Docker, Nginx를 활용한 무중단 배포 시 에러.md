---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포 시 에러"
author: eunchaelyu
date: 2024-02-23 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240223'
---

    
# 트러블슈팅     
# **AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포 시 에러**
    
## **Trouble Shooting 1.**
    
- **./gradlew 스크립트에 실행 권한이 없어서 발생하는 에러**
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f111d4c3-acfa-4b8d-b9aa-b81ed1a6a9a5)

- gradle build 되기 전 권한을 부여하는 스크립트 작성해서 해결한다
  
    
    ```bash
    # gradlew 스크립트에 실행 권한 부여 
    - name: gradlew 
    	run: | chmod +x ./gradlew  
    # gradle build
      - name: Build with Gradle
        run: ./gradlew build -x test
    ```


    
## **Trouble Shooting 2.**

- **non-interactive 환경에서 Docker 명령어를 실행하려고 할 때 발생하는 에러**
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/cebc2f28-a1a6-4ec7-a57b-bee7d38106bc)

- “Error: Cannot perform an interactive login from a non TTY device” 에러를 구글링  
    
- AWS CLI 자격증명이 안되어 있다는 것을 알게 되었다!!
        
- 참고하면 좋을 블로그를 찾았다
        
[AWSGithub-actions로-ECS를-통해-서비스-배포하기](https://japing.tistory.com/entry/AWSGithub-actions%EB%A1%9C-ECS%EB%A5%BC-%ED%86%B5%ED%95%B4-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%B0%B0%ED%8F%AC%ED%95%98%EA%B8%B0)
        
- `aws --version` 명령어를 통해 AWS CLI 가 최신버전으로 설치되어 있는지 확인 (2.XX.XX)

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e93ed596-ae86-456f-9149-7df855d0a816)

        
- aws configure 명령어를 통해 AWS CLI에 사용될 AWS Access Key ID, AWS Secret Access Key 등을 설정한다
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7fa7a5df-c3ff-4f8a-acd4-b8141a439f95)

        
- 다시 GitHub Actions 작업을 실행한다



## **Trouble Shooting 3.**
    
- 도커 이미지를 통해 pull 할 때 다음과 같은 에러가 생긴다면?
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9fe6174d-fced-4269-993c-77aa80c0377e)

        
- 도커가 실행중인지 확인하기        
        
`systemctl status docker`      
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/bbf46c5f-aeca-46ee-9c0f-949d11798fd1)      

        
- 실행되고 있지 않으므로 아래의 명령어를 실행한다      
- 도커 시작`sudo systemctl start docker`      
- 도커 설치 확인 `docker --version`        
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dabb6ac1-e862-4bc9-bcf2-1735980173b5)      

        
- root라는 사용자를 도커그룹에 추가 & 권한 설정 `sudo usermod -aG docker root`    
- 도커 재실행`sudo systemctl restart docker`    
- 도커 실행중인지 확인`systemctl status docker`    
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/762f021b-66fe-47f8-a330-153dde8f814d)      

        
- **트러블 슈팅 해결!!**    
- 다시 Nginx 명령어 실행    
            
`docker pull nginx`      
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9576082c-2913-4faf-a546-9317aeb23931)      

        
- 도커 컨테이너를 실행한다    
          
`docker container run --name nginxserver -d -p 80:80 nginx`      
        


    
## **Trouble Shooting 4.**      
    
- 기존 컨테이너가 있을 경우 아래와 같은 에러 메세지가 발생      
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/cd3a0367-52e7-462d-8a20-7b7cc7ea1e66)      

        
- 먼저 아래 명령어로 현재 실행중인 도커 컨테이너 확인한다      

`docker ps -a`      
        
- 기존 컨테이너 제거      
        
`docker container rm 4c630c0cbcad`      
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5c1a2983-3932-4920-a806-9de16cc0860f)      

        
    
**트러블 슈팅 해결!!**      
    
- 다시 도커를 해당 컨테이너에 올려서 Nginx를 실행시킨다      
        
`docker container run --name nginxserver -d -p 80:80 nginx`      
        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f024317f-a363-4048-a891-f712e91430c0)      



        
    
## **Trouble Shooting 5.**      
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/824bf008-130f-4310-969a-077118d8ee48)      

    
- "blue"라는 이름의 Docker 컨테이너를 찾을 수 없어서 발생한 에러          
- $service_url 이 green 서버로 설정되어있으므로 첫 시도에는 stop 할 blue 서버가 없어서 에러가 발생한다.      
- 다시 배포하면 실행되어 있던 green이 중지되고 blue 서버가 켜지면서 정상적으로 작동하게 된다. 첫 시도에만 발생하는 에러 사항        
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/165c53fb-6eb4-4d18-aafb-2b49df77ba69)      



    
## **Trouble Shooting 6.**      
    
- 서버가 포트 80에 연결을 거부하는 에러. 이는 서버에서 해당 포트로의 연결이 설정되어 있지 않다는 것을 의미    
    
![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/e17c062c-38ac-4162-89f6-d0dfc11b4087/Untitled.png)      
    
**서버 실행 상태 확인**      
    
```java
docker ps -a
```
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9f7b7d32-c72e-49db-bd55-0f9418673da5)      
    
- Docker 컨테이너 목록을 확인해보니 STATUS가 Exited된 것을 확인함      
    
**nginx 컨테이너 재실행**       
    
```java
docker restart nginxserver    
```
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/075c3b5e-4013-4478-adc2-87e025a00ec5)      

    
**blue 컨테이너 재실행**      
    
```java
docker restart blue
```
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ed3241b5-a5a1-4c5c-91c4-8ac582d9209f)      

    
- 두 컨테이너 모두 정상적으로 재실행된 것을 확인할 수 있다      
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8e7915c7-5301-4c61-93ce-7a6883662223)    

    
**재시도 해결완료!**      
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c6d46603-b9cf-4c83-8636-b2e7e8676100)    



    
## **Trouble Shooting 7.**    
    
- Docker container stop 시에 permission denied 에러      
    
```bash
Error response from daemon: cannot stop container: 2fe2354f8578: permission denied    
```
    
- EC2 중지 후 시작했을 때 도커 컨테이너가 삭제가 안되는 경우가 종종 있다.    
- 아래의 명령어는 관련 apparmor 를 날려주고 삭제가 된다.    
- 이 때, apparmor는 리눅스의 보안 관련 커널이다.    
    
- 특히 docker 를 snap 으로 설치하면 많은 AppArmor profile 이 쌓이고, 충돌이 날 수 있다.      
        
```java    
sudo aa-remove-unknown      
 ```
        
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3a67f0de-d714-40aa-893c-7a8e041f3687)      

    
- 다시 도커 컨테이너 삭제 가능한 것을 확인할 수 있다.      
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/51226009-4619-459d-8ab7-f4329ea4b500)      

