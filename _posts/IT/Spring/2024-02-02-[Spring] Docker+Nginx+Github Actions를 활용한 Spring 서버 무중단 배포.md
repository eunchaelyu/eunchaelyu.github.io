---
title: "[Spring] Docker+Nginx+Github Actions를 활용한 Spring 서버 무중단 배포"
author: eunchaelyu
date: 2024-02-02 18:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240202'
---

# Docker + Nginx + Github Actions를 활용한 Spring 서버 무중단 배포
   
## [1] 무중단 배포 방식       
> Blue/Green Deployment 채택 이유
    
   - 먼저 현 프로젝트는 소규모 서비스로 운영하고 있어서 Blue/Green 무중단 배포가 적절할 것이라고 판단이 되었다    
   - 또한, 블루 그린 방식은 두 개의 환경만 유지하면 되기 때문에 유지보수가 용이하다는 장점이 있고       
   - 트래픽 전환으로 롤백이 쉽고 안정성과 유연성을 동시에 충족한다는 점에서 블루 그린 무중단 배포 방식을 채택하게 되었다   

- [x] 현 프로젝트에서 CI/CD 구축에 Github Actions만 사용하고 있어, Github Actions + Nginx 설정을 활용해서 무중단 배포를 진행한다.


## [2]  
