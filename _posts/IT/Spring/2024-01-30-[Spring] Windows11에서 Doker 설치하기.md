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

  - 패키지를 정기적으로 업데이트하고 업그레이드하는 것이 좋다 
``sudo apt update && sudo apt upgrade``
    
|case|powershell|
|:---:|:---:|    
|기본 WSL 버전 설정| wsl --set-default-version 2|    
|배포판 WSL 버전 변경| wsl --set-version <distribution name> <versionNumber>|    
|예시| wsl --set-version Ubuntu-18.04 2|    


## [5] GNU/Linux 앱 설치    
  - Microsoft Store > Ubuntu LTS 다운로드    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/81def0c0-a542-40f9-8f1a-e55623649fa2)


  - 새로 설치된 Linux 배포를 처음 시작 > 콘솔 창이 열림 > 파일이 압축 해제    
  - > PC에 저장될 때까지 1~2분 정도 소요 > 새 Linux 배포를 위한 사용자 계정 및 암호 생성        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9c869579-18c7-4613-9ecf-14860a06c124)    

  - Enter new UNIX username: ubuntu(EC2 환경과 동일하게 설정한다)    
  - New password:    
  - Retype new password:    
  - 화면에 보이지 않으므로 인지하면서 입력한다!    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7c101ed4-fe40-4028-b4d1-2c742865e6c3)    

  ``ubuntu@DESKTOP-UFPCV7J:~$``로 접속되는 것 확인    



## [6] Doker Desktop 설치      
[Doker 설치 홈페이지](https://www.docker.com/)     
  - 다운받은 'Docker Desktop Installer' 실행 
![스크린샷 2024-01-30 171645](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4074f3f7-a50f-4f1c-9c2f-29703d756c1c)    
  - Configuration 항목은 모두 체크 
![스크린샷 2024-01-30 171703](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/173a3fe3-e80f-443a-a73f-aafaead41a45)

  - Docker를 실행한 뒤, 약관을 확인 > 동의      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3f65cf29-e6db-49da-bfa5-c775bddff89c)    

  - ``Update to the latest version of WSL 2 from Microsoft (Recommended - requires administrator password)
    We run `wsl --update` for you.``

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f0f449dd-6060-40d2-a286-c921d5aad9f2)

## [7] 도커 WSL 설정 확인하기    
  - Settings > General 선택 후 > 설정 확인    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4dae5541-bc44-4835-8b8f-2dad2bd622dc)

  - Settings > Resources > WSL INTEGRATION 선택 후 > 설정 확인 > Apply & Restart
  - [x] Enable Integration with my default WSL distro
  - [x] Enable Integration with additional distros: "설치한 리눅스 배포판"
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/92000993-fdc4-4dc9-98c1-bbec9d52e4e5)


## [8] 설치 확인    
  - 'Windows Terminal' 관리자 권한으로 실행(``wsl -l -v`` 명령어 실행)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/cd6bfa9b-1935-4b04-b981-057dd1f1b251)

  - 'Ubuntu'에서 다음 명령어 실행하여 설치 확인    
  ``docker run -d -p 80:80 docker/getting-started``    
  ``docker ps``    
  ``docker images``    



## 참고 레퍼런스   
  - MS 공식 페이지의 1~6단계        
[WSL의 수동 설치 단계](https://learn.microsoft.com/ko-kr/windows/wsl/install-manual#step-4---download-the-linux-kernel-update-package)    

  - WSL 2에서 Docker 원격 컨테이너 시작    
[WSL 2에서 Docker 원격 컨테이너 시작](https://learn.microsoft.com/ko-kr/windows/wsl/tutorials/wsl-containers)    
