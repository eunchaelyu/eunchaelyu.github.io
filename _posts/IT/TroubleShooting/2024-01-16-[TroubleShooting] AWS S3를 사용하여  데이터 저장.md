---
title: "[TroubleShooting] AWS S3를 사용하여 데이터 저장"
author: eunchaelyu
date: 2024-1-16 9:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240116'
---


#  AWS S3을 사용하여 데이터 저장

## **Issue** 
  React-Spring 연동해서 Web 사이트를 만드는데 사진 데이터를 전송하고 받아야한다.     
  하지만 많은 사진 데이터를 다 서버에 저장할 수는 없다.     
  대량의 데이터를 **안전하고** **직관적으로** 관리 할 수 있는 시스템 즉, 보관할 공간이 필요했다.     

## 해결 방안
 ``AWS S3``를 사용해서 해결한다    
###  [AWS WS3란]    
 : AWS S3(Amazon Simple Storage Service) 대규모의 데이터를 저장하고 검색하는데 쓰이는 용도로 인터넷 기반 스토리지 서비스라고 할 수 있다.        
 : S3는 버킷이라는 폴더에 객체인 파일을 저장할 수 있다. 즉, 버킷은 리소스에 객체로 저장된다.
 : 아래 사진은 실제 프로젝트 버킷 생성 완성한 모습, Project-housebucket 이라는 버킷에 3개의 객체(파일)가 저장된 것을 확인 할 수 있다         
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/528de016-3066-402c-b0f2-65b3615ae7d2)    
 : prefix 라는 하위 폴더들을 지정해줄 수 있는데 단순히 경로 역할만 한다.    
 
### [객체 업데이트 / 삭제 방식]    
 : S3는 업데이트나 삭제를 할 경우 기존 파일에 덮어써지는 것이 아니라 기존 파일이 남아 있고 routing만 작동을 바꾼다.  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d23a8a85-3f1e-47c5-827a-3085e1969770)    
 : 따라서 이전 파일은 삭제하고 새로 생성된 객체로 routing을 바꿔주는 기능을 구현해야한다.        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c6a3f889-53b8-4da7-b0b5-0f3eab56fa99)    


###  [S3 선택 이유]
 : 데이터 용량이 커질수록 사용자의 필요에 따라 확장이 되고 설계된 내구성이 단단해 데이터 손실을 걱정할 필요가 없다.    
 : 물론 현 프로젝트에서는 트래픽이 발생할 만큼의 대규모의 데이터를 다루지 않지만 추후 프로젝트 확장성을 생각했을 때     
 : 미리 안전한 DB 저장 환경을 구축해보는 차원에서 AWS S3를 선택했다. (대규모 데이터 분석, 백업 용도로도 사용이 가능하다) 

 
## *버킷 생성 순서* 
###  1. AWS 서비스에서 S3을 검색
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9c6126cb-cde1-471a-8c5e-fa92cb5e0980)
  - AWS 리전은 반드시 EC2와 동일한 리전을 사용해야 한다
  - 버킷 이름 설정해주고 다음으로 이동    
 
### 2. 객체소유권 & 차단 설정    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d106c27c-4431-4dcc-a1a4-0fa3b260b6cb)
  - 한 계정에서 관리하므로 ACL 비활성화
  - 퍼블릭으로 설정(추후 필요시 변경)

### 3. 버킷 버전관리 
  - 비활성화로 체크 (활성화는 비용이 더 든다, 무료 버전이라면 활용해봐도 좋을 것 같다)
    
### 4. 기본 암호화
  ![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ba6823b6-2cf8-482d-a74a-e2adce06b094)
  - 버킷 생성하기 누르면 s3-bucket 생성 완료         

### 5. 버킷 정책 설정    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d41559c6-9f5c-47f2-ae11-14e719826911)    
  - 해당 버킷을 누른 후 권한 -> 정책 편집을 누른다    
  - 버킷 ARN을 복사한 후 정책 생성기를 클릭    
  - Select Type of Policy 에서 S3 Bucket Policy를 선택    
  - Principal에 * 입력    
  - Actions에 Get Object, Put Object 체크    
  - Amazon Resource Name (ARN) 에 위에서 복사한 ARN을 입력한 후 /* 입력 ex)arn:aws:s3:::project-housebucket/*    
  - Add Statement 클릭    
  - Generate Policy를 클릭하여 뜨는 json 구문을 복사    
  - 버킷 정책 편집 페이지에 돌아가 정책에 복사한 json구문을 붙여넣기 후, 번경 사항 저장을 클릭    
  - 다시 버킷을 클릭하면 퍼블릭 엑세스 가능 표시 뜬다( 아래 사진 참고 )
 ![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/57413a50-66db-4582-b3d5-14a8693c74f7)


    

