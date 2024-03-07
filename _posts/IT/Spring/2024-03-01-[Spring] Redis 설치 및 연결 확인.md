---
title: "[Spring] Redis 설치 및 연결 확인"
author: eunchaelyu
date: 2024-03-01 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240301'
---

    
# Redis 설치 및 연결 확인        
    1. ElasticCache for Redis 인스턴스 생성하기
    2. EC2 인스턴스에서 ElasticCache 실행하기
    3. Spring으로 ElasticCache for Redis 사용하기
    4. Redis 연결 확인    

    
## [1] ElasticCache for Redis 인스턴스 생성하기

### 1. Redis 클러스터 생성

- 새롭게 디자인된 콘솔 화면으로 전환하여 [Redis 클러스터 생성] 버튼을 누른다      

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/1e93231d-ee80-44cd-b7c8-38c3c778f61a)

### 2. 클러스터 모드 비활성화 및 클러스터 정보(이름, 설명) 입력

- 클러스터 모드를 활성화하면 확장성과 가용성을 개선하지만 소규모 프로젝트이므로 비활성화를 유지한다       
- 그리고 이름과 설명를 입력해준다      
      
- 클러스터 모드를 활성화하면 여러 노드에 데이터를 분산하여 확장성과 가용성을 향상시킬 수 있지만, 관리 및 설정에 더 많은 노력이 필요할 수 있다     
- 클러스터 모드를 사용할지 여부는 다음과 같은 요소를 고려해야 한다         

1. **확장성**: 클러스터 모드를 사용하면 데이터베이스를 확장하여 대규모 작업 부하를 처리할 수 있다    
   따라서 사용자 수나 데이터 양이 늘어날 것으로 예상되는 경우 클러스터 모드를 고려할 수 있다        
2. **가용성**: 클러스터 모드를 사용하면 여러 노드 간에 데이터를 복제하여 고가용성을 제공할 수 있다    
   이는 데이터베이스 시스템의 신뢰성을 높이고 장애 시에도 서비스를 계속할 수 있도록 도와준다        
3. **복잡성**: 클러스터 모드를 사용하면 관리 및 설정이 더 복잡해질 수 있다    
   클러스터의 구성, 노드 간 통신 및 데이터 복제 설정 등을 고려해야 하므로 이에 대한 추가 작업이 필요하다        
4. **비용**: 클러스터 모드를 사용하면 더 많은 리소스가 필요하므로 비용이 더 많이 발생할 수 있다        

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f44febea-889d-443a-9cc8-7e592db8251c)


### 3. 위치(AWS 클라우드) 및 다중 AZ 설정

- 호스팅 위치 AWS 클라우드를 선택한다    
- ElasticCache는 오직 EC2 위에서만 실행이 가능하다         
- 로컬 PC에서는 사용할 수 없다
- 고가용성을 위해 다중 AZ를 설정한다
- 다중 AZ를 설정하면 자동 장애 조치도 디폴트로 설정된다
- 이 기능은 primary 노드가 장애 발생으로 다운되더라도 복제본 노드를 primary 노드로 승격시켜주어 장애를 대처해준다      

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c811301d-6a9f-43d7-9937-ef1e0032843c)


### 4. 클러스터 설정 - 노드 유형: ec2랑 동일한 버전

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b3a1c667-ebf2-4495-b998-c29cfbb0463e)


### 5. 서브넷 그룹 설정

- 기존 서브넷 그룹이 없으면 새로운 서브넷 그룹을 생성해준다.  다중 AZ 환경을 위해서 필요하다        

- 만약 한달 내내 풀가동을 돌리기 위해서는 위에 다중AZ, 자동 장애 조치를 취소하고 해당 설정을 넘겨야 한다    
- 자동 장애 조치를 설정하면 Replica 노드 2개가 필수로 필요하기 때문에 프리티어 3개의 노드를 구동하게 된다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ecceacd5-7a10-47c0-ad1e-f58b366bcafe)


### 6. Redis 클러스터 생성 완료      

- 생성하면 1~2분 소요 후 정상적으로 운영되는 것을 확인할 수 있다        

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/52ccc2c9-6200-4d6f-9ef8-49655aed709e)    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5008c2dd-3eb0-48a1-ae71-4834d53f93db)    


## [2] EC2 인스턴스에서 ElasticCache 실행하기

- 위에서 ElasticCache를 생성할 때 AWS 클라우드를 선택하였다면 무조건 EC2 위에서만 실행이 가능하다 

### 1. EC2 인스턴스 생성 및 실행    

- 이미 있으니 생략    

### 2. ElasticCache 보안그룹 생성 (내 IP, EC2)    

- EC2 인스턴스가 정상적으로 구동되었으면 ElasticCache 보안 그룹을 만들어서 인바운드에 다음과 같이 두 개의 규칙을 추가해준다    

1. 포트번호 6379 입력, 내 IP 선택    
2. 포트번호 6379 입력, EC2 보안그룹 선택    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6bc63fd8-8e50-4ee0-8dbc-e5b720dce7c8)


### 3. ElasticCache Redis 클러스터에 보안 그룹 적용

- ElasticCache 콘솔로 돌아가서 [Redis 클러스터 클릭 → 작업 버튼 클릭 → 수정] 순으로 진행한 후 다음과 같이 보안 항목에 2번에서 생성한 보안 그룹을 넣어준다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/fce83de0-e24b-4db7-9c4a-aa705675c7d9)    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/00710ed3-ec9b-4da8-8b6d-4603b54d2aee)    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b24bfb37-fcaa-419d-9250-a72c5148de56)    

### 4. EC2에 Redis-cli 설치하기

- ec2 인스턴스 내부에 Redis-cli를 설치하여 ElasticCache 인스턴스가 정상적으로 동작되는지 확인한다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e7b4e4ab-6a35-40b2-bc87-7b9dc3c29440)        
    

#### EC2 접속
```ssh -i ~/.ssh/eroom_key.pem ubuntu@44.219.159.74```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e59d8209-1962-45f8-802b-f7d2ba0d87c4)    


#### make 명령어를 실행하기 위해 gcc를 다운한다    

```
sudo apt update
sudo apt install gcc
```
  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c4f47e6d-e027-40b7-90ad-e930cce97bc3)    


#### Redis를 컴파일하려면 먼저 **`make`**를 설치해야 한다    

```
sudo apt install make
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/460598fc-3386-4501-9f7a-28de7c5c6520)


#### redis-cli 설치 및 make 명령어를 실행한다        

```
sudo wget http://download.redis.io/redis-stable.tar.gz && tar xvzf redis-stable.tar.gz && cd redis-stable && make
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b9ba5fee-5372-40f4-b2bf-5b75f959fd66)


#### redis-cli를 bin에 추가해 어느 위치서든 사용 가능하게 등록한다    

```
sudo cp src/redis-cli /usr/bin/
```

- 정상적으로 설치가 완료되었으면 redis-cli 명령어로 ElasticCahce를 실행한다        



#### 기본 엔드포인트 사용하기

- primary 노드 1개와 replica 노드 2개 엔드포인트 3개 중 현재 primary 노드가 기본 엔드 포인트와 매핑된다  
- 만약 장애가 발생하여 자동 장애 조치(failover) 기능으로 primary 노드가 바뀌면 새롭게 선출된 primary 노드 엔드 포인트가 자동으로 기본 엔드포인트에 매핑된다
- 그러니 primary나 replica 엔드포인트가 아닌 기본 엔드포인트를 사용해줘야 한다    


#### make 명령어를 실행하기 위해 gcc를 다운받는다       

```
redis-cli -h {ElasticCache 엔드포인트}
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/44106cc5-c8fe-4d6a-97e8-e00b6b113046)

```
redis-cli -h redis-eroomchallenge.zeyf14.ng.0001.use1.cache.amazonaws.com
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8dad1c10-2857-499c-b832-88c532b809f5)



## [3] Spring으로 ElasticCache for Redis 사용하기

- 그럼 이제 Spring 프로젝트를 실행 시켜 Redis를 사용해보자. 먼저 기본적으로 EC2에 jdk와 git을 설치해줘야 한다.

### 1. jdk17 설치하기
    
    ```    
    **apt install openjdk-17-jre-headless  # version 17.0.8.1+1~us1-0ubuntu1~22.04**
    ```
    
### 2. Java 버전 확인
    
    ```
    java -version
    ```
    

## [4] Redis 연결 확인

- 먼저 응용 프로그램이 Redis에 성공적으로 연결되었는지 확인한다    
- Redis 로그를 확인하거나 모니터링 도구를 사용하여 응용 프로그램에서 Redis로의 연결 여부를 확인 할 수 있다    

### 1. EC2 보안그룹 인바운드 규칙 6379 포트 열어준다

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f95a9958-68bd-4097-abd8-6e3a73eb664f)

### 2. Redis 로그 확인 
- Redis의 로그 파일을 확인하여 응용 프로그램에서의 연결 시도와 상태를 확인할 수 있다    
- 보통 Redis의 로그 파일은 **`/var/log/redis/`** 또는 **`/var/log/`** 디렉토리에 위치하고     
- 로그 파일을 열어서 응용 프로그램에서의 연결 시도와 관련된 메시지를 찾아볼 수 있다    

- Redis Command Line Interface(CLI)를 사용하여 응용 프로그램이 Redis와의 연결을 확인할 수 있다    
- 다음 명령을 사용하여 Redis에 결하고 PING 명령을 보내서 응답을 확인할 수 있다    

### 3. EC2 접속

```
ssh -i [키페어 끌어다놓기] ubuntu@[public id]
sudo su
redis-cli

```

```
> PING
PONG

```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/5740a77c-0a12-4100-a9ab-6ae45305a685)

- 위 명령을 실행한 결과가 **`PONG`**으로 나오면 Redis와의 연결이 정상적으로 이루어진 것이다    
- 로컬 호스트 뿐 아니라 Redis 클라이언트를 사용해 Elasticache 클러스터에 연결해서도 확인한다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0fdf831f-63e9-4843-b907-4638edd36d2e)

- 모니터링 도구를 사용하여 실시간으로 Redis와의 연결 상태를 모니터링할 수 있다
- 예를 들어, Redis의 내장 모니터링 기능을 사용하거나 서드 파티 모니터링 도구를 이용할 수 있다    

```
> MONITOR
OK
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6623b125-481b-448b-970a-fc4cd2447791)


![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ba73700a-6f54-459b-ac84-662b267d8f18)

- 로컬 호스트 뿐 아니라 Redis 클라이언트를 사용해 Elasticache 클러스터에 연결해서도 확인한다

### 4. 자격증명 실행

```
sudo apt update // 패키지 업데이트
sudo apt install awscli // AWS CLI 설치
aws configure // AWS 계정에 대한 인증 정보를 설정
aws elasticache describe-cache-clusters --region us-east-1 //Elasticache 클러스터에 대한 정보를 반환
```

- host는 클러스터의 엔드포인트 주소를 의미한다    
- localhost로 설정한 host를 엔드포인트 주소로 바꿔준다    

### 5. application.yml 수정 전

```java
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 6. application.yml 수정 후

```java
spring:
  data:
    redis:
      host: [기본 클러스터 엔드 포인트:  "redis ~~~ .amazonaws.com"]
      port: 6379
```

### 7. 6379 포트를 사용하는 연결의 상태와 속성 확인

```java
netstat -nap | grep 6379
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/df7c2ca8-b23d-4790-82e5-955c0bdc7e42)

- tcp 0 0 172.31.8.23:58792 172.31.12.141:6379 TIME_WAIT    
  로컬 주소와 외부 주소 간의 TIME_WAIT 상태의 연결을 보여준다    
  연결은 이미 종료되었지만, 일정 시간 동안 커널에서 기다리는 중인 것을 알 수있다    

- tcp 0 0 0.0.0.0:6379 0.0.0.0:* LISTEN 144597/redis-server    
  모든 네트워크 인터페이스에 대한 연결을 수신하는 것을 의미하고, 상태는 LISTEN이다    


