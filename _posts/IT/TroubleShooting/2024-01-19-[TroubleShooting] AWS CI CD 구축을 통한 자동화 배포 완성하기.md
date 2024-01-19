---
title: "[TroubleShooting] AWS CI/CD 구축을 통한 자동화 배포 완성하기"
author: eunchaelyu
date: 2024-1-19 9:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
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


    
    
  




### 레퍼런스 참고 순서    
[Getting started with CodePipeline](https://docs.aws.amazon.com/codepipeline/latest/userguide/getting-started-codepipeline.html)    
[Step 1: Create an AWS account and administrative user](https://docs.aws.amazon.com/codepipeline/latest/userguide/getting-started-codepipeline.html#create-iam-user)        
[Step 1-1: AWS 계정 루트 사용자에 대한 가상 MFA 디바이스 활성화(콘솔)](https://docs.aws.amazon.com/IAM/latest/UserGuide/enable-virt-mfa-for-root.html)        
[Step 1-2: IAM 자격 증명 센터를 활성화](https://docs.aws.amazon.com/singlesignon/latest/userguide/get-set-up-for-idc.html)            
[Step 2-1: AWS IAM Identity Center의 사용자 및 그룹 생성](https://docs.aws.amazon.com/singlesignon/latest/userguide/howtocreatepermissionset.html)        


