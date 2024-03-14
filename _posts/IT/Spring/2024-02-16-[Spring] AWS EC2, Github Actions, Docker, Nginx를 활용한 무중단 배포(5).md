---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(5)"
author: eunchaelyu
date: 2024-02-16 12:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (5)     
> 13. GitHub ACTIONS 워크플로우 생성 
> 14. GitHub ACTIONS 워크플로우 작성

    
## [13] GitHub ACTIONS 워크플로우 생성       
- Github의 Actions 탭 > set up a workflow yourself

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/126dd3e2-1e0c-48a2-a222-a6b399eaad7b)        

- gradle.yml 생성 완료!    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/268fdff3-0418-4035-b52d-0bd3c0ef5815) 

  
## [14] GitHub ACTIONS 워크플로우 작성
- 단계별로 뜯어서 코드를 이해해보자

### STEP 1 CI/CD 기본 설정           

```yml
# github repository actions 페이지에 나타날 이름
name: CI/CD

# event trigger
# master브랜치에 push가 되었을 때 실행
on:
  push:
    branches: [ "master" ]

permissions:
  contents: read
```
- master에 push하면 actions 활성화가 된다
- permissions는 읽기에 권한을 준다는 뜻! 

### STEP 2 JDK setting    

```yml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # JDK setting
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
```
- jobs는 쉽게 말해서 하나의 일하는 단위!!
- build가 정상적으로 실행 되면 -> deploy 순차적으로 과정이 진행이 되고              
- runs-on 이 build, deploy에 각각 있는데 ubuntu-latest(가상 PC)가 실행이 될 것이라는 뜻이다                
- 가장 보편적이고 가볍기 때문에 이 운영체제(ubuntu)를 서버로 사용한다    

- Github Actions용 Java 프로젝트를 빌드하거나 실행하기 위한 JDK 버전 설정(JDK 17)        
- uses: actions/checkout@v3: 새로운 코드 변경사항을 가져오기 위한 액션    
         
### STEP 3 Build with Gradle       

```yml
      - name: Build with Gradle
        run: |
          mkdir -p ./src/main/resources
          echo ${{ secrets.YML }} | base64 --decode  > ./src/main/resources/application.yml
          cat ./src/main/resources/application.yml
          chmod +x ./gradlew
          ./gradlew build -x test
```
- /src/main/resources 디렉토리를 생성하여 YML 파일을 디코딩하여 가지고 온다           
- application.yml은 Secrets에 저장된 값으로 Base 64로 인코딩된 값이 들어가 있기 때문에 디코딩 작업을 한다        
- gradlew 스크립트에 실행 권한 부여 후 build 된다        
- x test 옵션은 테스트를 실행하지 않도록 설정            
- jar 파일이 만들어진다    

### STEP 4    
```yml
      - name: Login to DockerHub
        uses: docker/login-action@v1
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
```
- jar 파일을 ubuntu에서 만들었기 때문에 도커 로그인을 ubuntu에서 한다        

### STEP 5 Build Docker    

```yml
      - name: Build Docker
        run: docker build --platform linux/amd64 -t ${{ secrets.DOCKER_USERNAME }}/eroom-prod .

      - name: Push Docker
        run: docker push ${{ secrets.DOCKER_USERNAME }}/eroom-prod:latest
```

- jar 파일을 스냅샷을 찍어서 이미지로 만든다        
- eroom-prod:latest라는 레포지토리로 도커 허브에 보낸다(push)    

### STEP 6 deploy    

```yml
  deploy:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Set target IP
        run: |
          STATUS=$(curl -o /dev/null -w "%{http_code}" "http://${{ secrets.HOST_PROD }}/env")
          echo $STATUS
          if [ $STATUS = 200 ]; then
            CURRENT_UPSTREAM=$(curl -s "http://${{ secrets.HOST_PROD }}/env")
          else
            CURRENT_UPSTREAM=green
          fi
          echo CURRENT_UPSTREAM=$CURRENT_UPSTREAM >> $GITHUB_ENV
          if [ $CURRENT_UPSTREAM = blue ]; then
            echo "CURRENT_PORT=8080" >> $GITHUB_ENV
            echo "STOPPED_PORT=8081" >> $GITHUB_ENV
            echo "TARGET_UPSTREAM=green" >> $GITHUB_ENV
          elif [ $CURRENT_UPSTREAM = green ]; then
            echo "CURRENT_PORT=8081" >> $GITHUB_ENV
            echo "STOPPED_PORT=8080" >> $GITHUB_ENV
            echo "TARGET_UPSTREAM=blue" >> $GITHUB_ENV
          fi
```

- needs: build는 위의 파일이 정상적으로 실행되면 build 하겠다라는 뜻~    
- STATUS=$(curl -o /dev/null -w "%{http_code}" "http://${{ secrets.HOST_PROD }}/env") / echo $STATUS는 요청되는 코드와 상태를 반환해보기(200ok 인지 아닌지 확인)       
- $STATUS = 200 이 아니라면 CURRENT_UPSTREAM를 green으로 설정    
- CURRENT_UPSTREAM가 green이라면 CURRENT_PORT는 8081, STOPPED_PORT는 8080, TARGET_UPSTREAM은 blue로 설정이 돼서
- 그 다음에 실행될 서버가 blue라는 것을 의미함
- 현재 실행되고 있는 서버, 포트, 멈춰 있는 포트, 앞으로 실행될 서버가 정보에 담기게 됨

- CURRENT_UPSTREAM에 green이 담겨서 env에 전달됨          
- $GITHUB_ENV 전역변수에 담으면 아래에서 env. 으로 접근 가능함          


### 현재 상황          
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/44004582-4bf9-4e05-bd5d-301902c1a4fc)        
- Nginx 서버로 접속은 되지만 env 요청은 처리가 안된 상태        
- 아직 프록시 서버에서 스프링 부트 서버를 배포하지 않았기 때문에 404에러가 뜨는 것        

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d12b4057-d4b5-4d6f-8170-3b70979f60a8)    
- http://localhost:8080/env로는 요청이 잘 가는 상태        
  
  
### STEP 7 Docker compose 실행    

```yml
      - name: Docker compose
        uses: appleboy/ssh-action@master
        with:
          username: ubuntu
          host: ${{ secrets.HOST_PROD }}
          port: 22
          key: ${{ secrets.PRIVATE_KEY }}
          script_stop: true
          script: |
            sudo docker pull ${{ secrets.DOCKER_USERNAME }}/eroom-prod:latest
            sudo docker-compose -f docker-compose-${{env.TARGET_UPSTREAM}}.yml up -d
```
- env 에 변수값을 담은 후 docker compose를 실행해야 한다    
- github actions에서 EC2로 접속을 해야한다    
- 이 때, SSH로 접속해야하기 때문에 자동으로 secrets에 등록한 PRIVATE_KEY가 담기게 됨        
- shell 스크립트를 여러줄 실행시킬 때는 | 즉, or bar를 사용하면 된다        

- 스크립트를 실행시킬 때는 관리자 권한으로 sudo 사용해서 실행시켜야 한다        
- 아까 push 해둔 eroom-prod:latest 이미지를 EC2로부터 PULL 받는다        
- 이전 실행시키고 있는 파일이 BLUE였다면 GREEN이 TARGET_UPSTREAM에 담겼으므로 해당 GREEN 도커 컴포즈 YML 파일이 실행됨        
- Dockerfile에 profiles랑 env 가 green, green으로 바껴서 green서버가 실행됨    


### STEP 8 Check deploy server URL    

```yml
      - name: Check deploy server URL
        uses: jtalk/url-health-check-action@v3
        with:
          url: http://${{ secrets.HOST_PROD }}:${{env.STOPPED_PORT}}/env
          max-attempts: 5
          retry-delay: 10s

      - name: Wait for Load Balancer to Register Targets
        run: sleep 60
```

- 10초마다 1번씩 최대 5번 요청하고 응답이 없다면 배포가 실패하게 됨    
- 이 다음이 blue를 green으로 바꿔주는 작업인데 요청이 없다면 나중에 둘다 실행이 안되기 때문에 먼저 정상적으로 돌아간다는 체크 작업이 필요하다        

- 블루서버나 그린서버로 전환했다면 AWS 로드밸런서에 적용되기까지 적절한 Sleep(대기 시간)을 줘야한다    
- 이 sleep을 주지 않은 경우 배포 후에 502/200 에러가 번갈아 뜨는 문제가 발생한다

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f07150a4-ecb6-43d3-8085-a6579d127f3f)     
   

- 현재는 8081 포트로 그린 서버가 실행중이다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/250b3c0e-ff15-4dcd-92f0-58026ae9072c)      

- 새로운 버전으로 블루 서버를 실행하면서 sleep을 주는 과정을 확인한다    
  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b8ee9d42-cce6-42e1-bc8b-a2a58bcdb2a0)      

- 로드밸런서 대상그룹에 health check가 블루, 그린 서버 모두 완료된 것을 확인할 수 있다                
     이전 버전으로 실행 중이였던 그린 서버도 healthy 상태이기 때문에 중지, 삭제가 가능한 상태이다            

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b4de628d-d659-40d2-bb97-6a8dc9443943)    

- 정상적으로 8080 포트로 블루 서버만이 실행되고 있고 무중단배포가 완료된 것을 확인할 수 있다        

### STEP 9 Change nginx upstream    

```yml
      - name: Change nginx upstream
        uses: appleboy/ssh-action@master
        with:
          username: ubuntu
          host: ${{ secrets.HOST_PROD }}
          key: ${{ secrets.PRIVATE_KEY }}
          script_stop: true
          script: |
            sudo docker exec -i nginxserver bash -c 'echo "set \$service_url ${{ env.TARGET_UPSTREAM }};" > /etc/nginx/conf.d/service-env.inc && nginx -s reload'
```

- 다시 ssh 로 EC2 에 접속하고     
- sudo docker exec -i nginxserver bash는 nginxserver라는 서버에 접속한다    
- 여기에 -c 를 같이 쓰면 접속한 것처럼 command만 사용할 수 있다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3552ca8f-174e-4c5f-98e2-f0d5bb5a7fea)        
- echo "set \$service_url ${{ env.TARGET_UPSTREAM }};"는 service_url을 현재 ``env.TARGET_UPSTREAM``로 바꾼다는 것을 의미한다                
- (현재 위의 사진처럼 green으로 돼있는 것을 blue로 바꾸는 것, 아직 배포가 안됐기 때문에 status가 200이 아니라 green으로 돼있음)        


### STEP 10  Stop current server    

```yml
      - name: Stop current server
        uses: appleboy/ssh-action@master
        with:
          username: ubuntu
          host: ${{ secrets.HOST_PROD }}
          key: ${{ secrets.PRIVATE_KEY }}
          script_stop: true
          script: |
            sudo docker stop ${{env.CURRENT_UPSTREAM}}
            sudo docker rm ${{env.CURRENT_UPSTREAM}}

      - name: Prune unused Docker images
        run: sudo docker image prune -a
```

- 다시 EC2로 SSH 사용해서 접속 후 컨테이너 green을 중지 시키고 삭제한다            
- 처음 배포할 때 이 과정에서 기존 실행 서버가 없어서 오류난다(첫 시도에서는 이 과정 에러 무시 해도 됨)      

    
### STEP 11 Check Target Health      

```yml      
      - name: Check Target Health
        run: |
          aws elbv2 describe-target-health --target-group-arn arn:aws:elasticloadbalancing:us-east-1:471112860836:targetgroup/eroomTargetGroup/029a9432dd208dc7 | jq -r '.TargetHealthDescriptions[].TargetHealth.State'
```      

- AWS 자격 증명을 설정하고, 실제로 대상 그룹의 상태를 확인하는 단계이다

      

