---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(6)"
author: eunchaelyu
date: 2024-02-16 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (6)     
> 15. AWS 설정
> 16. 무중단 배포 확인  
> 17. 트러블 슈팅    

    
## [15] AWS 설정   
### 1. 인바운드 규칙
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/066b3762-191c-453e-9039-7cb99d717215)    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d0f145cf-0a24-4c9e-be72-f7b71cfcba5c)    


- 이 요청을 날리는 대상이 GitHubActions ubuntu ! 즉, 외부 서버이다.         
- 외부 서버에서는 해당 8080, 8081 접속이 안된다        
- 왜냐하면 EC2에서 포트 설정을 안해줬기 때문이다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/916ba4ca-d530-4f48-b9de-cfb8aeed27c8)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ec90949e-15d0-4fd0-8335-53fd2ba7e5ac)    
  

- 현재 보안그룹 > 해당 인스턴스가 launcha-wizard-2로 잡혀있다( HTTP 80은 열려있다, HTTP 8081, HTTP 8080 열어줘야한다 )    

  
### 2. Route 53          
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/08209b85-dfb4-40b7-b894-68c0c76ffc76)    

- [x] 리액트 (프론트단) 배포 시    
-  vercel로 "eroom-challenge.com"랑 "www.eroom-challenge.com" 도메인명으로 두 개 배포        
-  "www.eroom-challenge.com"로 요청 시 "eroom-challenge.com"로 리다이렉트        
-  env에 백엔드 url 설정 "https://api.eroom-challenge.com"    
-  vercel 배포시 key, value 값에 "REACT_APP_SERVER_URL", "https://api.eroom-challenge.com" 각각 secrets로 저장하기
  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0e0bafb7-b8a5-4ca4-baf3-b1d019987c1d)    

- [x] 스프링 (백엔드) 배포 시    
1. api.eroom-challenge.com로 배포하기    
2. 인증서 "*.eroom-challenge.com" 추가 발급 및 연결 필요    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/be606a79-4267-407b-9246-5e781cfd1819)        


### 3. RDS 생성                 
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/77e1b690-56bc-4cf8-8457-d4473abe7833)            
USER, PASSWORD, 엔드포인트 잘 기억해두고 Intellij에서 연결 및 yml 설정 파일에 사용      


### 4. 로드밸런서 설정        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/24ac9a06-30f2-4b69-9202-cded286c5cb4)    
               
- 8080,8081,443 포트로 연결된 리스너를 각각 생성한다

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8c73dcac-cd9b-441a-bf3e-637654d9f806)    

- eroomTargetGroup은 8080포트로 연 리스너와 연결            
- 대상그룹은 8080,8081 포트를 열어준다        



## [16] 무중단 배포 확인    
### 1. build        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/59ccb3d5-c1c9-41ea-b5fd-4ad82a84f066)    

### 2. deploy          
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/3aa5df43-1f22-4d90-abf2-3122240dc9bd)    
 


### 3. blue 서버가 실행되고 있는 경우        
- health 체크    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/70fc9416-490f-44f9-b86c-643661bab6aa)    

-env 체크    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b733339f-40cf-4030-820a-744ddaac474f)        


### 4. green 서버가 실행되고 있는 경우    
- health 체크    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9dc2e727-db1d-402b-b9a7-32b508c7e4fe)    

-env 체크      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e6847180-56e2-483d-96d3-2357b294e532)        









