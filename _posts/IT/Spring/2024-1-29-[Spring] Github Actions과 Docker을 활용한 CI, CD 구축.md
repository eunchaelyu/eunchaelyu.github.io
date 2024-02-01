---
title: "[Spring] Github Actions과 Docker을 활용한 CI/CD 구축"
author: eunchaelyu
date: 2024-1-29 07:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240129'
---

# Github Actions과 Docker을 활용한 CI/CD 구축        

## CI / CD 자동화 배포 전체 흐름        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e78102d7-74f7-41a9-a788-634b0fb16536)


## CI (Continuous Integration) 배포 
  새로운 코드 변경 사항이 주기적으로 1)빌드하고 2)기존 파일과 병합시 오류가 없는지 테스트를 완료한 후          
  3) **문제가 발생한 경우** 다시 코드 수정 후 빌드 / **문제가 없는 경우** 배포를 진행한다        

## CD (Continuous Delivery  / Continuous Deployment) 배포      
  개발자가 수정한 코드를 저장소뿐 아니라 사용자가 사용할 수 있는 프로덕션 환경까지 항상 신뢰 가능한 수준에서 배포될 수 있도록 관리한다    

## Github Actions 선정 이유
  Travis CI, Jenkins 등 잘 알려진 CI/CD 구축 방법들이 있지만 Github Actions을 사용하려는 이유는       
  먼저 무료플랜에서 진행이 가능하다는 점 :) 게다가 GitHub 저장소 내에서 직접 설정하고 사용할 수 있고       
  ``.github/workflows``에 다소 간단하게 구현할 수 있다고 생각이 들어 Github Actions 배포 방식을 택하게 되었다    

## [1] Github Actions ``yml`` 파일 설명
### 1. 코드 변경시 배포 스크립트 실행
```
# github repository actions 페이지에 나타날 이름
name: CI/CD using github actions & docker

# event trigger
# master이나 dev 브랜치에 push가 되었을 때 실행
on:
  push:
    branches: [ "master", "dev" ]

permissions:
  contents: read
```
  - master이나 dev 브랜치에 push가 되었을 때 workflow를 실행한다    
  - 1) master 브랜치 - 배포용 서버 코드
  - 2) dev 브랜치 - 개발용 서버 코드(merge용)
  - 3) feature 브랜치 - 기능별 코드


### 2. JDK 설정    
```
jobs:
  CI-CD:
    runs-on: ubuntu-latest
    steps:

      # JDK setting - github actions에서 사용할 JDK 설정 (프로젝트나 AWS의 java 버전과 달라도 무방)
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
```
  - Github Actions용 Java 프로젝트를 빌드하거나 실행하기 위한 JDK 버전 설정
  - ``uses: actions/checkout@v3``: 새로운 코드 변경사항을 가져오기 위한 액션
  
### 3. gradle caching
```
      # gradle caching - 빌드 시간 향상
      - name: Gradle Caching
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-
```
  - Gradle 빌드 시간을 향상시키기 위해 캐싱을 설정하는 것    
  - ``actions/cache@v3``: 캐싱 처리 액션      
  - ``path:``: 캐싱할 디렉토리 경로 | ``key``: 캐시를 식별하기 위한 키 | ``restore-keys``: 이전에 생성된 캐시를 복원하기 위한 키

    
### 4. application.yml 파일 생성    
```
# 환경별 yml 파일 생성(1) - application.yml
      - name: make application.yml
        if: |
          contains(github.ref, 'master') ||
          contains(github.ref, 'dev')
        run: |
          mkdir ./src/main/resources # resources 폴더 생성
          cd ./src/main/resources # resources 폴더로 이동
          touch ./application.yml # application.yml 생성
          echo "${{ secrets.YML }}" > ./application.yml # github actions에서 설정한 값을 application.yml 파일에 쓰기
        shell: bash

      # 환경별 yml 파일 생성(2) - dev
      - name: make application-dev.yml
        if: contains(github.ref, 'dev')
        run: |
          cd ./src/main/resources
          touch ./application-dev.yml
          echo "${{ secrets.YML_DEV }}" > ./application-dev.yml
        shell: bash

      # 환경별 yml 파일 생성(3) - prod
      - name: make application-prod.yml
        if: contains(github.ref, 'master')
        run: |
          cd ./src/main/resources
          touch ./application-prod.yml
          echo "${{ secrets.YML_PROD }}" > ./application-prod.yml
        shell: bash
```
  - 배포 환경용 파일을 분리하여 사용한다
  - Github의 Secret Key같은 중요한 정보를 저장해주고 ``gitignore``에 저장해서 Github에는 올라가지 않도록 주의!!    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/10a56976-7864-4a4c-809e-481077b79894)    



### 5. gradle build
```
# gradle build
      - name: Build with Gradle
        run: ./gradlew build -x test
```
  - Gradle을 사용하여 프로젝트를 빌드하는 GitHub Actions 단계 지정
  - ``name``: 이 단계 이름
  - ``run:``: 이 단계에서 실행할 명령어를 지정


### 6. docker build & push
```
# docker build & push to production
      - name: Docker build & push to prod
        if: contains(github.ref, 'master')
        run: |
          docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
          docker build -t ${{ secrets.DOCKER_USERNAME }}/eroom-prod 
          docker push ${{ secrets.DOCKER_USERNAME }}/eroom-prod

      # docker build & push to develop
      - name: Docker build & push to dev
        if: contains(github.ref, 'dev')
        run: |
          docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
          docker build -t ${{ secrets.DOCKER_USERNAME }}/eroom-dev 
          docker push ${{ secrets.DOCKER_USERNAME }}/eroom-dev
```
  - 배포용 서버와 개발용 서버는 설정 파일의 내용이 다르기 때문에 **빌드 파일의 내용과 생성된 도커 이미지도** 다르다.    
  - docker hub repository > 1) **eroom-prod** 2) **eroom-dev** 로 나뉜다
    

### 7. deploy to EC2 (무중단 배포 시 주석처리!!)
```
      ## deploy to production
      - name: Deploy to prod
        uses: appleboy/ssh-action@master
        id: deploy-prod
        if: contains(github.ref, 'master')
        with:
          host: ${{ secrets.HOST_PROD }} # EC2 퍼블릭 IPv4 DNS
          username: ubuntu
          key: ${{ secrets.PRIVATE_KEY }}
          envs: GITHUB_SHA
          script: |
             sudo docker ps
             docker stop $(docker ps -a -q)
             docker rm $(docker ps -a -q)
             sudo docker pull ${{ secrets.DOCKER_USERNAME }}/eroom_prod
             sudo docker run -d -p 8080:8080 ${{ secrets.DOCKER_USERNAME }}/eroom_prod
             sudo docker image prune -f

      ## deploy to develop
      - name: Deploy to dev
        uses: appleboy/ssh-action@master
        id: deploy-dev
        if: contains(github.ref, 'dev')
        with:
          host: ${{ secrets.HOST_DEV }} # EC2 퍼블릭 IPv4 DNS
          username: ${{ secrets.USERNAME }} # ubuntu
          password: ${{ secrets.PASSWORD }}
          port: 22
          key: ${{ secrets.PRIVATE_KEY }}
          script: |
             sudo docker ps
             docker stop $(docker ps -a -q)
             docker rm $(docker ps -a -q)
             sudo docker pull ${{ secrets.DOCKER_USERNAME }}/eroom_dev
             sudo docker run -d -p 8080:8080 ${{ secrets.DOCKER_USERNAME }}/eroom_dev
             sudo docker image prune -f
```
  - EC2에서 docker pull

### 8. 무중단 배포 시 추가 코드
```
// master 브랜치를 무중단 배포 적용할 경우
docker-pull-and-run:
   runs-on: [self-hosted, prod]
   if: ${{ needs.CI-CD.result == 'success' }}
   needs: [ CI-CD ]
   steps:
     - name : 배포 스크립트 실행
       run: |
         sh /deploy.sh

// dev 브랜치를 무중단 배포 적용할 경우
docker-pull-and-run:
   runs-on: [self-hosted, dev]
   if: ${{ needs.CI-CD.result == 'success' }}
   needs: [ CI-CD ]
   steps:
     - name : 배포 스크립트 실행
       run: |
         sh /deploy.sh
```

### ``gradle.yml`` 전체 파일    
```
# github repository actions 페이지에 나타날 이름
name: CI/CD using github actions & docker

# event trigger
# master이나 dev 브랜치에 push가 되었을 때 실행
on:
  push:
    branches: [ "master", "dev" ]

permissions:
  contents: read

jobs:
  CI-CD:
    runs-on: ubuntu-latest
    steps:

      # JDK setting - github actions에서 사용할 JDK 설정 (프로젝트나 AWS의 java 버전과 달라도 무방)
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      # gradle caching - 빌드 시간 향상
      - name: Gradle Caching
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      # 환경별 yml 파일 생성(1) - application.yml
      - name: make application.yml
        if: |
          contains(github.ref, 'master') ||
          contains(github.ref, 'dev')
        run: |
          mkdir ./src/main/resources # resources 폴더 생성
          cd ./src/main/resources # resources 폴더로 이동
          touch ./application.yml # application.yml 생성
          echo "${{ secrets.YML }}" > ./application.yml # github actions에서 설정한 값을 application.yml 파일에 쓰기
        shell: bash

      # 환경별 yml 파일 생성(2) - dev
      - name: make application-dev.yml
        if: contains(github.ref, 'dev')
        run: |
          cd ./src/main/resources
          touch ./application-dev.yml
          echo "${{ secrets.YML_DEV }}" > ./application-dev.yml
        shell: bash

      # 환경별 yml 파일 생성(3) - prod
      - name: make application-prod.yml
        if: contains(github.ref, 'master')
        run: |
          cd ./src/main/resources
          touch ./application-prod.yml
          echo "${{ secrets.YML_PROD }}" > ./application-prod.yml
        shell: bash

      # gradle build
      - name: Build with Gradle
        run: ./gradlew build -x test

      # docker build & push to production
      - name: Docker build & push to prod
        if: contains(github.ref, 'master')
        run: |
          docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
          docker build -t ${{ secrets.DOCKER_USERNAME }}/eroom-prod 
          docker push ${{ secrets.DOCKER_USERNAME }}/eroom-prod

      # docker build & push to dev
      - name: Docker build & push to dev
        if: contains(github.ref, 'dev')
        run: |
          docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
          docker build -t ${{ secrets.DOCKER_USERNAME }}/eroom_dev 
          docker push ${{ secrets.DOCKER_USERNAME }}/eroom_dev

      ## deploy to production
      - name: Deploy to prod
        uses: appleboy/ssh-action@master
        id: deploy-prod
        if: contains(github.ref, 'master')
        with:
          host: ${{ secrets.HOST_PROD }} # EC2 퍼블릭 IPv4 DNS
          username: ubuntu
          key: ${{ secrets.PRIVATE_KEY }}
          envs: GITHUB_SHA
          script: |
             sudo docker ps
             docker stop $(docker ps -a -q)
             docker rm $(docker ps -a -q)
             sudo docker pull ${{ secrets.DOCKER_USERNAME }}/eroom_prod
             sudo docker run -d -p 8080:8080 ${{ secrets.DOCKER_USERNAME }}/eroom_prod
             sudo docker image prune -f

      ## deploy to develop
      - name: Deploy to dev
        uses: appleboy/ssh-action@master
        id: deploy-dev
        if: contains(github.ref, 'dev')
        with:
          host: ${{ secrets.HOST_DEV }} # EC2 퍼블릭 IPv4 DNS
          username: ${{ secrets.USERNAME }} # ubuntu
          password: ${{ secrets.PASSWORD }}
          port: 22
          key: ${{ secrets.PRIVATE_KEY }}
          script: |
             sudo docker ps
             docker stop $(docker ps -a -q)
             docker rm $(docker ps -a -q)
             sudo docker pull ${{ secrets.DOCKER_USERNAME }}/eroom_dev
             sudo docker run -d -p 8080:8080 ${{ secrets.DOCKER_USERNAME }}/eroom_dev
             sudo docker image prune -f
```

## [2] Github Actions 적용 순서    
### 1) Github에 public 레포지토리 생성 > Setting > Secrets and variables > Actions 탭      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7a73e48f-b50a-4814-b1e4-a253f6a022ba)    
      
### 2) New repository secret에 각각 추가 (Settings > Secrets and variables > Actions)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/be851c40-538e-4095-9e54-ab4fb4c6f708)       

> DOCKERHUB_USERNAME : 본인의 Docker Hub Username
> DOCKERHUB_PASSWORD : 본인의 Docker Hub Password
> HOST_PROD: prod 환경의 EC2 인스턴스 ip (EC2 퍼블릭 IPv4 DNS)
> PRIVATE_KEY: 개인키
> USERNAME: EC2 인스턴스 계정 ID(ec2-user)

  - 3) Github의 Actions 탭 > ``set up a workflow yourself`` 
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f7a0c8c9-8ba5-46ff-beb0-2165e28dc2e7)

  - 4) ``gradle.yml`` 설정하기
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b26fa89c-7037-44ed-9e3c-df5127581e49)




  - **빌드 시 plain jar 생성하지 않도록 설정**    
```
jar{
    enabled = false
}
```




