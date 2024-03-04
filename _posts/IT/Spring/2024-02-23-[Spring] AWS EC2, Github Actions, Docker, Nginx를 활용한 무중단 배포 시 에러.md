---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포 시 에러"
author: eunchaelyu
date: 2024-02-23 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240223'
---

    
## 트러블슈팅     
- **AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포 시 에러**
    
    **Trouble Shooting 1.**
    
    - **./gradlew 스크립트에 실행 권한이 없어서 발생하는 에러**
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/30c4d311-7844-4a6c-83f3-eecf74c916cb
        
    
    > gradle build 되기 전 권한을 부여하는 스크립트 작성해서 해결한다
    > 
    
    ```bash
    # gradlew 스크립트에 실행 권한 부여 
    - name: gradlew 
    	run: | chmod +x ./gradlew  
    # gradle build
      - name: Build with Gradle
        run: ./gradlew build -x test
    ```
    
    **Trouble Shooting 2.**
    
    - **non-interactive 환경에서 Docker 명령어를 실행하려고 할 때 발생하는 에러**
    
    https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/698c78c5-986e-470f-bb0b-e98b051630e6
    
    > “Error: Cannot perform an interactive login from a non TTY device” 에러를 구글링
    > 
    - AWS CLI 자격증명이 안되어 있다는 것을 알게 되었다!!
        
        참고하면 좋을 블로그를 찾았다
        
        [AWSGithub-actions로-ECS를-통해-서비스-배포하기](https://japing.tistory.com/entry/AWSGithub-actions%EB%A1%9C-ECS%EB%A5%BC-%ED%86%B5%ED%95%B4-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%B0%B0%ED%8F%AC%ED%95%98%EA%B8%B0)
        
        `aws --version` 명령어를 통해 AWS CLI 가 최신버전으로 설치되어 있는지 확인 (2.XX.XX)
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c157c484-edf5-417e-8941-6caa991289fe
        
    - aws configure 명령어를 통해 AWS CLI에 사용될 AWS Access Key ID, AWS Secret Access Key 등을 설정한다
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b3c612a8-127b-48d8-ad4e-4d0e2870c5c3
        
    - 다시 GitHub Actions 작업을 실행한다
    
    **Trouble Shooting 3.**
    
    - 도커 이미지를 통해 pull 할 때 다음과 같은 에러가 생긴다면?
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6fd1b93f-3151-45fa-990e-468b249ff4ee
        
    - 도커가 실행중인지 확인하기
        
        `systemctl status docker`
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9d3f6cf6-332f-42db-b6a6-71e946d388ec
        
    - 실행되고 있지 않으므로 아래의 명령어를 실행한다
    - 도커 시작`sudo systemctl start docker`
    - 도커 설치 확인 `docker --version`
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/29e8cb48-4329-47be-af52-50cffdfbc6c0
        
    - root라는 사용자를 도커그룹에 추가 & 권한 설정 `sudo usermod -aG docker root`
    - 도커 재실행`sudo systemctl restart docker`
    - 도커 실행중인지 확인`systemctl status docker`
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8907e136-97f5-460f-855a-3cbc31e79420
        
    - **트러블 슈팅 해결!!**
    - 다시 Nginx 명령어 실행
        
        `docker pull nginx`
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/24330229-35d7-4f99-aa9a-db1f90ee9eab
        
    - 도커 컨테이너를 실행한다
        
        `docker container run --name nginxserver -d -p 80:80 nginx`
        
    
    **Trouble Shooting 4.**
    
    - 기존 컨테이너가 있을 경우 아래와 같은 에러 메세지가 발생
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6b2eb4c1-4c10-46fb-9087-2fc4905575a6
        
    - 먼저 아래 명령어로 현재 실행중인 도커 컨테이너 확인한다
        
        `docker ps -a`
        
    - 기존 컨테이너 제거
        
        `docker container rm 4c630c0cbcad`
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0d692cc4-8a6c-4310-864d-07d6eb8c7ac4
        
    
    **트러블 슈팅 해결!!**
    
    - 다시 도커를 해당 컨테이너에 올려서 Nginx를 실행시킨다
        
        `docker container run --name nginxserver -d -p 80:80 nginx`
        
        https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/de76e597-2449-4d63-aa0f-9a85878d28ac
        
    
    **Trouble Shooting 5.**
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/c0f55d17-a5bc-47fb-b573-6d79fac923f8/Untitled.png)
    
    - "blue"라는 이름의 Docker 컨테이너를 찾을 수 없어서 발생한 에러
    - $service_url 이 green 서버로 설정되어있으므로 첫 시도에는 stop 할 blue 서버가 없어서 에러가 발생한다. 다시 배포하면 실행되어 있던 green이 중지되고 blue 서버가 켜지면서 정상적으로 작동하게 된다. 첫 시도에만 발생하는 에러 사항
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/f3d4362b-4895-43f0-bf9d-5644ad3dda5d/Untitled.png)
    
    **Trouble Shooting 6.**
    
    - 서버가 포트 80에 연결을 거부하는 에러. 이는 서버에서 해당 포트로의 연결이 설정되어 있지 않다는 것을 의미
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/e17c062c-38ac-4162-89f6-d0dfc11b4087/Untitled.png)
    
    **서버 실행 상태 확인**
    
    ```java
    docker ps -a
    ```
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/62669491-d51a-49f6-9485-c1df38f30390/Untitled.png)
    
    - Docker 컨테이너 목록을 확인해보니 STATUS가 Exited된 것을 확인함
    
    **nginx 컨테이너 재실행** 
    
    ```java
    docker restart nginxserver
    ```
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/6d2f6e2c-0dcb-4d03-a0e6-5d68edf7a31f/Untitled.png)
    
    **blue 컨테이너 재실행**
    
    ```java
    docker restart blue
    ```
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/083ade50-be20-4e9d-aa0e-3dd2771ae515/Untitled.png)
    
    두 컨테이너 모두 정상적으로 재실행된 것을 확인할 수 있다
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/cb9a2ff5-6221-4ee6-815d-b8ba96deeff6/Untitled.png)
    
    **재시도 해결완료!**
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/f1ef3c6e-7598-490d-8f46-37707a6a972a/Untitled.png)
    
    **Trouble Shooting 7.**
    
    - Docker container stop 시에 permission denied 에러
    
    ```bash
    Error response from daemon: cannot stop container: 2fe2354f8578: permission denied
    ```
    
    - EC2 중지 후 시작했을 때 도커 컨테이너가 삭제가 안되는 경우가 종종 있다.
    - 아래의 명령어는 관련 apparmor 를 날려주고 삭제가 된다.
    이 때, apparmor는 리눅스의 보안 관련 커널이다.
    
    특히 docker 를 snap 으로 설치하면 많은 AppArmor profile 이 쌓이고, 충돌이 날 수 있다.
        
        ```java
        sudo aa-remove-unknown
        ```
        
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/4fe2fbea-fe01-43a1-8ec5-d419aa4082b4/Untitled.png)
    
    다시 도커 컨테이너 삭제 가능한 것을 확인할 수 있다.
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/18bc74ec-fac5-408a-98fb-fff72308ae1a/Untitled.png)
