---
title: "[Spring] AWS CI/CD 구축을 통한 자동화 배포 완성하기"
author: eunchaelyu
date: 2024-1-19 9:49:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240119'
---


#  AWS CI/CD 구축을 통한 자동화 배포 완성하기    

## **Issue**     
  : AWS로 https 배포에 성공했지만 코드를 업그레이드 하고 매번 리눅스 환경해서 명령어 치는 시간이 꽤나 많이 소요가 됐다.     
    POSTMAN으로 테스트 후 배포한 웹 브라우저에서 한 번 테스트 할 때마다 서버를 재작동 시키는 과정이 번거롭다.     
    개발자로서 코드에 집중할 수는 없을까. 리액트에서 VERCEL 로 자동화 배포 하는 것처럼 스프링에서도 자동화 배포 하는 방법이 없을까     
    생각해보다가 CI/CD 파이프라인을 구축하면 된다는 솔루션을 얻었다.      
    AWS 레퍼런스를 참고하면서 차근차근 진행해본다.    
    
## CI/CD란        
  : 파이프라인 설정을 해서 자동 빌드로 EC2 인스턴스에 배포하는 등 프로세스의 단계를 자동화 한다.    

## Step 1. AWS 계정 및 관리 사용자를 생성한다    
  - 계정에는 이미 등록이 되어있으므로 관리 사용자 생성
    
  - AWS 계정 루트 사용자에 대한 가상 MFA 디바이스 활성화(콘솔)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6d4e0bc0-d807-45c0-8f40-0f0ab9525b40)        

  - MFA(Multi-Factor Authentication 섹션에서 MFA 장치 할당을 선택      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3d819e24-6fd9-46a0-b32b-1a6da30c0d33)    

  - Device name 입력하고 Authenticator app 선택 후 Next 선택    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0116b1ed-f635-4e2f-873c-16f1770f85ab)    

  - QR 코드 표시 누르고 앱으로 스캔(구글 OTP 사용함) 후 6자리 코드가 시간차를 두고 2번 뜨면 순차적으로 입력     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/2c0b1895-12d3-43a9-be78-109089bb50bc)    

  - IAM 자격 증명 센터를 활성화 아래에서 AWS Organizations로 활성화를 선택
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/2ad87df4-b23e-4b39-9849-9ab42be0dd8c)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/fac68690-a4a7-4f94-8dda-e99ea24fefb9)    

이제 IAM 자격 증명 센터를 구성할 준비가 되었고 IAM 자격 증명 센터를 활성화하면 자격 증명 센터 디렉터리가 기본 자격 증명 소스로 자동 구성됨.

## Step 2. CodePipeline에 대한 관리 액세스를 위한 AWSCodePipeline_FullAccess 관리형 정책 적용
  : **AWSCodePipeline_FullAccess**는 IAM 사용자가 액세스할 수 있는 모든 CodePipeline 작업 및 리소스에 대한 액세스를 제공하고     
  : CodeDeploy, Elastic Beanstalk 또는 Amazon S3를 포함하는 단계 생성과 같이 파이프라인에서 단계를 생성할 때 가능한 모든 작업에 대한 액세스를 제공한다    

  - AWS IAM Identity Center의 사용자 및 그룹 생성(IAM ID 센터 콘솔-> 다중 계정 권한 아래에서 권한 집합 선택)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3e7382ae-abfe-4951-8f9b-39e1743208d3)

  - 사전 정의된 정책 아래 목록에서 IAM 작업 기능 정책 또는 공통 권한 정책 중 하나를 선택한 후 다음을 선택
  - (나는 AWS에 모든 리소스 엑세스 가능한 걸 선택했다 )
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/57f634d8-0a23-43f8-8d54-cc2ee7fed2ec)
    
  - 선택사항 검토 후 생성 누르면 다음과 같은 화면이 나온다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ff4de2bc-f150-4f96-83e5-f8c2a7360c1f)


## Step 3. AWS CLI 설치        
  : CodePipeline 명령을 호출하려면 AWS CLI를 설치해야한다, AWS CLI는 정기적으로 업데이트 된다는 것 기억하자    
   
  - [Windows(64비트)용 AWS CLI MSI 설치 프로그램을 다운로드하고 실행](https://awscli.amazonaws.com/AWSCLIV2.msi)
  - 별도의 선택해야 하는 사항 없음 바로 install 하면 된다
    
  - 명령 프롬프트 창을 검색하여 ``cmd``열고 명령 프롬프트에서 해당 ``aws --version`` 명령을 사용
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c9907023-0b2c-4f6b-ade2-cd4e78a4fdcd)

  - CodePipeline과 함께 사용할 IAM 사용자의 AWS 액세스 키와 AWS 보안 액세스 키를 지정( defalut output은 json으로 설정 )
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6edf4069-98d2-4a69-af84-3593222d29a3)

## Step 4. CodePipeline 콘솔 열기    
[CodePipeline 콘솔 열기](http://console.aws.amazon.com/codesuite/codepipeline/home)


## Step 5.  배포 (AWS Elastic Beanstalk)
- 프로젝트를 저장한 git repository 생성 (Github)은 되어있으므로 생략 
- Elastic BeanStalk는 콘솔을 사용해서 애플리케이션을 손쉽게 배포하고 관리할 수 있는 컴퓨팅 서비스   

## Step 7: CodePipeline에서 파이프라인 생성    
https://us-east-1.console.aws.amazon.com/codesuite/codepipeline/pipelines?region=us-east-1&pipelines-meta=eyJmIjp7InRleHQiOiIifSwicyI6eyJwcm9wZXJ0eSI6InVwZGF0ZWQiLCJkaXJlY3Rpb24iOi0xfSwibiI6MjAsImkiOjB9


    
  




### 레퍼런스 참고 순서    
[Getting started with CodePipeline](https://docs.aws.amazon.com/codepipeline/latest/userguide/getting-started-codepipeline.html)    
[Step 1: Create an AWS account and administrative user](https://docs.aws.amazon.com/codepipeline/latest/userguide/getting-started-codepipeline.html#create-iam-user)        
[Step 1-1: AWS 계정 루트 사용자에 대한 가상 MFA 디바이스 활성화(콘솔)](https://docs.aws.amazon.com/IAM/latest/UserGuide/enable-virt-mfa-for-root.html)        
[Step 1-2: IAM 자격 증명 센터를 활성화](https://docs.aws.amazon.com/singlesignon/latest/userguide/get-set-up-for-idc.html)            
[Step 2-1: AWS IAM Identity Center의 사용자 및 그룹 생성](https://docs.aws.amazon.com/singlesignon/latest/userguide/howtocreatepermissionset.html)            
[Step 3: 최신 버전의 AWS CLI 설치 또는 업데이트](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)      
[Step 4: CodePipeline 콘솔 열기](http://console.aws.amazon.com/codesuite/codepipeline/home)    
[Step 5: CodePipeline에서 파이프라인 생성](https://docs.aws.amazon.com/ko_kr/codepipeline/latest/userguide/pipelines-create.html)



