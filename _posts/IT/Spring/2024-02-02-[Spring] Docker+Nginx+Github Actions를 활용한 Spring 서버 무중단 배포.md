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
    
   - 소규모 서비스로 운영하는 점에서 Blue/Green 무중단 배포가 적절할 것이라고 판단이 되었다    
   - 블루 그린 방식은 두 개의 환경만 유지하면 되기 때문에 유지보수가 용이하다는 장점이 있고       
   - 트래픽 전환으로 롤백이 쉽고 안정성과 유연성을 동시에 충족한다는 점과     
   - 비용적인 측면을 고려했을 때 한 서버 안에서 구동할 수 있는 방향으로 설정하면 좋을 것 같다는 생각이 들었다         

> 현 프로젝트에서 CI/CD 구축에 Github Actions만 사용하고 있어, Github Actions + Nginx 설정을 활용해서 무중단 배포를 진행한다.


## [2]    
