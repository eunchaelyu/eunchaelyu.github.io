---
title: "[GitHub] 깃허브 기초 사용법(with Intellij)"
author: eunchaelyu 
date: 2023-12-28 1:49:00 +09:00
categories: [IT, GitHub]
tags: [IT, GitHub]
pin: true
img_path: '/posts/20231228'
---


## 깃허브 사용법 익히기    
> 토이 프로젝트, 미니 프로젝트 진행하면서 완전히 git hub 사용을 겉핥기식으로 했었다면    
> 이번에 팀원들이랑 pull/push 반복학습 하면서 이제야 내 입으로 직접 설명할 수 있을 정도로    
> 익히게 되었다. git 용어 (clone, fork, branch, merge, pull request)    


[수정 작업 후]
```text
*터미널에서
  git status (작업 상태 modified 빨간색인지 확인)
  git add .
  git status(modified 초록색인지 확인
  git commit -m "커밋메세지"
  git checkout "브랜치명"
  git push origin "브랜치명"
*깃허브에서
  pull and create request 누르기 (본인 브랜치에서 작업한 파일이 팀장 레퍼지토리 main으로 merge)
  write 문구 작성하기
  able to merge 알림 확인 후 merge 하기
```


[최신 작업 불러올 때]
```text
*깃허브에서
  create pull request 누르기 (팀장 레퍼지토리 main 에서 본인 브랜치로 merge 확인 후)
  write 문구 작성하기
  able to merge 알림 확인 후 merge 하기
*터미널에서
  본인 폴더로 들어가 있는지 꼭 확인 
  git checkout "브랜치명"
  git pull origin "브랜치명"
```
  - 특히 폴더 경로로 제대로 설정이 안돼있는 경우 'did not match any file(s) known to git'라는 오류가 뜬다)    
