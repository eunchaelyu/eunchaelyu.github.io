---
title: "[Spring] TroubleShooting, Doker 설치 후 DB 연결이 안되는 경우"
author: eunchaelyu
date: 2024-02-02 06:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240202'
---

# TroubleShooting
> 어제부터 너무 힘들게 하는 "Doker 설치 후 DB 연결이 안되는 문제" 발생 ...    
> 차근차근 해결해보고 과정을 기록하고자 다시 글을 작성한다  
> HTTPS 배포 및 연결까지는 RDS 연결이 잘 됐다  
> CI/CD 구축하면서 Doker 설치 후 갑자기!!! DB 연결이 안되면서 서버도 자꾸 중단됐다
> 다행히 서버는 Ubuntu 22.04 LTS를 다시 설치하면서 
  ``C:\Users\LOVE\AppData\Local\Packages\CanonicalGroupLimited.Ubuntu22.04LTS_79rhkp1fndgsc\LocalState``의 ext4를 되찾고 다시 실행이 가능했다  

  - 서버가 잘 실행되는 듯 했으나.....      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/58070f8e-7eba-467b-bfdb-aa787f5b2e0e)    

  - 여전히 괴롭히는 Unknown database 오류...!!!    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/631b2245-a28f-4080-8be6-048666419359)

  - 팀원들과 에러 메세지 공유하면서 다른 해결책을 도움 받았다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f826b296-1d0c-4015-9989-802102a9f2f7)

  - **팀원 1의 의견**: 아래와 같은 블로그 참고    
[docker-compose로 mysql 올렸는데 unknown database 에러 발생](https://coco-log.tistory.com/185)    

  - & 체크해야 할 5가지   
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/81b512c3-5c91-4bec-bc62-18591d37ebae)

  - **팀원 2의 의견**: 아래의 순서대로 하나씩 짚어보기           
  - "Docker 컨테이너 상태 확인"    
  - "Docker 네트워크 설정"    
  - "MySQL 컨테이너 로그 확인"    
  - "Docker 컨테이너 내부에서 MySQL 데이터베이스 확인"    
  - "연결 설정 확인"    
  - "Spring Boot 어플리케이션 재시작"    

  - 천사같은 팀원들 덕분에 다시 차근차근 트러블 슈팅 도전한다    

## [1] "Docker 컨테이너 상태 확인"    
  - MySQL Docker 컨테이너가 실행 중인지 확인해보자.
  - cmd 창을 열고 다음 명령어 실행 ``docker ps``
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e4e68cda-31e2-4b09-9e07-2614061c6a74)
  - eunchaelyu/eroom ID로 된 컨테이너가 실행 중인 것을 알 수 있다

## [2] "Docker 네트워크 설정"   
  - Spring Boot 어플리케이션과 MySQL Docker 컨테이너가 동일한 Docker 네트워크에 속하는지 확인해보자
  - 동일한 네트워크에 속한 컨테이너끼리 통신할 수 있기 때문에 이 사항은 반드시 체크해야 한다
    
### 1. Docker 네트워크 생성:      
  - 아래의 명령어를 실행한다      
  - ``docker network create my-network``    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/2c5a3aec-476b-41e5-ad72-d1c4f0ffcedc)
  - 여러 영문자와 숫자가 조합돼서 나오는 것을 확인할 수 있다    


### 2. Spring Boot 어플리케이션 Docker 컨테이너 설정:        
  - "Dockerfile"이 있는 디렉토리로 이동한 후 아래의 빌드 명령어를 실행한다       
  - ``docker build -t spring-app .``    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/91cb723e-1e7f-4b6a-a114-5e9a2ecdeae7)        

  - ``docker run --name spring-container --network my-network -p 8080:8080 -d spring-app``    
  - 명령어를 실행하면 Spring Boot 컨테이너가 뜨게 된다    
  - Spring Boot 컨테이너를 my-network라는 네트워크에 속하도록 지정된다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dda1af8f-57d8-4f88-995d-9d6399888076)    

### 3. MySQL Docker 컨테이너 설정:    
  - MySQL 컨테이너를 생성하고 해당 컨테이너를 my-network 네트워크에 속하도록 한다
  - 다음 명령어를 실행한다
  - ``docker run --name mysql-container --network my-network -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=mydatabase -p 3306:3306 -d mysql:latest``

  - 이 때 아래와 같은 오류가 뜬다     
  - 이미 사용 중인 포트를 Docker가 사용하려고 시도하고 있을 때 발생하는 오류다  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/70db3949-df09-46f8-963c-4b5efe9ee67d)

  - 현재 실행 중인 다른 MySQL 컨테이너나 프로세스가 3306 포트를 이미 사용하고 있는지 확인하기 위해서 아래의 명령어를 실행    
  - ``netstat -an | find "3306"``
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b0ea5def-eb5d-4991-beef-10fceba70d03)

  - 여기서 2가지의 방법이 있다 3307 포트로 돌리느냐 / 3306 프로세스를 종료시키고 다시 MySQL 컨테이너를 생성 및 연결을 하느냐
  - 본인은 3306 포트를 계속 사용할 예정이기 때문에 과감하게 3306 프로세스 종료 후 다시 실행하는 방법을 택한다    
  - 현재 3306 포트를 사용 중인 프로세스의 PID(프로세스 식별자)를 표시하는 명령어 실행  
  -  ``netstat -ano | find "3306"`` 
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c99e2021-a686-4c3b-a1a9-2d245c1712f5)

  - PID 6044 프로세스 종료시킨다
  - ``taskkill /F /PID 6044``


