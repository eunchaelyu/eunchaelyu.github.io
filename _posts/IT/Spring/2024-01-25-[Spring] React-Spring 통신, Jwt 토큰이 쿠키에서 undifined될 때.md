---
title: "[Spring] React-Spring 통신, Jwt 토큰이 쿠키에서 undifined될 때"
author: eunchaelyu
date: 2024-1-25 9:49:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240125'
---

## React-Spring 통신, Jwt 토큰이 쿠키에서 undifined될 때

### Issue
- Spring에서는 Jwt 토큰 생성과, 쿠키에 저장, 전달까지 완료된 상태
- React에서 로그인 후 Jwt 토큰이 쿠키에 전달되지 않는 상황 (undefined)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/158be7f2-d967-4a64-812e-fdc1e1be58b4)

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3c0a8707-c2dc-4289-909f-8903a6d9873c)

- 서버단에서 JwtUtill.java 파일에서 중복되었던 인코딩 과정을 제거했다.
