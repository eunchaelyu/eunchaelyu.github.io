---
title: "[Spring] Windows11에서 Doker 설치하기"
author: eunchaelyu
date: 2024-1-30 06:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240130'
---

# Windows11에서 Doker 설치하기    

## [1] Windows Terminal 설치    
  - Microsoft Store > Windows Terminal 다운로드
[Microsoft 공식 홈페이지](https://www.microsoft.com/ko-kr/p/windows-terminal/9n0dx20hk701?rtc=1&activetab=pivot:overviewtab)

## [2] WSL2 설치    
  - 'Windows Terminal' 앱을 관리자 권한으로 실행 (다음 명령어 실행) 
``dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart``
![스크린샷 2024-01-30 163847](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/a51f45ef-fdc6-49c8-9a70-03a69fcd862c)

  - WSL2 요구사항 확인 (다음 명령어 실행)   
``winver``    
![스크린샷 2024-01-30 164023](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8b4f585e-6bc4-45cc-bebd-225b0a0e7f28)


## [3] Virtual Machine 기능 사용      
  - 'Windows Terminal' 앱을 관리자 권한으로 실행 (다음 명령어 실행)
``dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart``

## [4] WSL2 업그레이드
  - Linux커널 업데이트 패키지 [최신 다운로드](https://wslstorestorage.blob.core.windows.net/wslblob/wsl_update_x64.msi)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/fd73fe8a-4781-4b3c-9397-3c49157884e9)

|case|powershell|
|:---:|:---:|    
|기본 WSL 버전 설정| wsl --set-default-version 2|    
|배포판 WSL 버전 변경| wsl --set-version <distribution name> <versionNumber>|    
|예시| wsl --set-version Ubuntu-18.04 2|    


## [5] GNU/Linux 앱 설치    
  - Microsoft Store > Ubuntu:22.04 LTS 다운로드    
![스크린샷 2024-01-30 165427](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/728219cb-91a2-43ba-ba81-2bb927d6ae9d)

## [6] Doker Desktop 설치      
  - 다운받은 'Docker Desktop Installer' 실행    
![스크린샷 2024-01-30 171437](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0a8a04bb-1cb9-4d1a-836e-dcfd2f03387f)
  - Configuration 항목은 모두 체크    
![스크린샷 2024-01-30 171645](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4074f3f7-a50f-4f1c-9c2f-29703d756c1c)
![스크린샷 2024-01-30 171703](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/173a3fe3-e80f-443a-a73f-aafaead41a45)


## 참고 레퍼런스   
- MS 공식 페이지의 1~6단계    
[WSL의 수동 설치 단계](https://learn.microsoft.com/ko-kr/windows/wsl/install-manual#step-4---download-the-linux-kernel-update-package)
