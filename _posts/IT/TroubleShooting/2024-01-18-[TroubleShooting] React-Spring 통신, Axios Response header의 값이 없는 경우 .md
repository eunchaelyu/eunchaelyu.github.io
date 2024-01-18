---
title: "[TroubleShooting] React-Spring 통신, Axios Response header의 값이 없는 경우"
author: eunchaelyu
date: 2024-1-17 9:23:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240117'
---


#  [React-Spring 통신] Axios Response header의 값이 없는 경우   

## **Issue**           
  :  React-Spring FE-BE 프로젝트에서 로그인/회원가입 API 기능 구현은 완료, 백엔단 배포도 완료된 상태             
:  스프링 백엔단에서 https로 배포한 뒤에 jwt 토큰을 쿠키에 저장해서 보내는 방식을 jwt 토큰을 헤더에 담아서 전달하는 방식으로 바꿨다.            
:  분명히 POSTMAN으로 테스트를 했을 때는 우리가 넣어준 header "Authorization"필드에 "Bearer " + 토큰값 상태로 잘 보였다.            
:  ![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/61ca050a-0812-478d-933d-ecec01349f9e)    
:  jwt 디코딩을 했을 때 토큰 값에도 정확한 user 정보가 잘 들어가 있었다(아래 사진 참고)     
:  [jwt.io 사이트](https://jwt.io/)    
:  ![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c0ab16bb-a58e-47b2-982b-763f6c90e5a1) 

:  하지만 서버에 로그인 요청을 하면 클라이언트 측 프론트단에서 헤더가 보이지 않는 것이다.     
:  "Authorization"으로 응답 받지 못하는 문제가 발생했다.    


## 해결 
- Gpt, 블로그 모두 CORS문제라고 짚었다.  CORS (Cross-Origin Resource Sharing) 때문에 헤더에 접근하는 것에 제한이 걸려 Response header로 받지 못한 상황이라는 것을 알게 됐다.
- 최대한 우리 상황과 비슷한 블로글들을 찾아보았다.
- 서버측 코드를 수정해야한다.
[react - header의 Authorization받기](https://kingchan223.tistory.com/230)    
[Axios Response header의 값이 없는 경우](https://bogmong.tistory.com/5)    


### 1. 먼저 서버측 CORS 코드를 살펴본다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/1b489636-c17a-431a-9e48-07a351ecb74c)        
- 클라이언트의 로컬 서버가 허용할 오리진에 추가되어있고 모든 METHOD 요청을 허용하는    
- ```config.addAllowedMethod("*");```도 추가 돼있었다.        
- ```source.registerCorsConfiguration("/api/**", config);``` 경로에 따라 다르지만        
- 우리 프로젝트에서는 ```api/member```, ```api/interior```를 사용하기 때문에 api로 시작하는 경로에 cors를 허용해주는 코드도 넣었다.    
    
### 2. 클라이언트가 응답에 접근할 수 있는 header 추가    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/60d02076-4423-4b90-b990-4354128da71b)    
- MDN 문서를 참고해보면 헤더 값은 정확하게 명시를 해줘야한다.    
[Expose-Headers](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Expose-Headers)    
- 응답 Access-Control-Expose-Headers헤더를 사용하면 서버는 요청에 대한 응답으로 브라우저에서 실행 중인 스크립트에 어떤 응답 헤더를 사용할 수 있는지 나타낼 수 있다.        
- CORS 허용 목록에 포함된 응답 헤더만 노출이 되기 때문에 클라이언트가 다른 헤더를 엑세스 하기 위해서는            
- 포트를 허용해주며 열어줬듯이 우리가 사용하는 "Authorization" 헤더를 열어줘야 했던 것이다        


### 3. **setAllowCredentials(true)** 설정 추가
- CORS 정책은 보안 상의 이유로 기본적으로 브라우저가 다른 출처에서의 요청에 대해 인증 정보를 포함하지 않도록 막는다.            
- setAllowCredentials(true)를 설정하면 브라우저는 인증 정보를 요청에 포함하도록 허용하게 됨.            
- 헤더에 토큰을 담아서 보내는 경우, 이 토큰은 CORS 정책에 따라 브라우저에 의해 요청 헤더에 포함된다.             
- 서버는 이 토큰을 검증하고 필요에 따라 인증하게 되는 것.  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7938294d-df07-4636-b942-b99ab3642983)


### 4. 회고    
이번에 계속 React-Spring 통신을 하면서 CORS 오류를 많이 접하게 되면서 느꼈던 점은        
서버측 입장에서 API 작성하고 ERD 구성하고 토큰이나 전달 소스도 잘 보인다고 던져주면 끝인 게 아니라    
클라이언트 측에서 잘 받아지는지까지 확인하고 서버측에서 어떻게 설정을 해서 보내야 받을 수 있는지     
코드를 짤 때 클라이언트 입장에서 한 번 더 생각해보게 되는 계기가 되었다.        
생각보다 설정해줘야 할 것이 많고 트러블도 많았지만 포트 열어주고 사용하는 헤더를 직접 열어주고 하는 것이         
보안상 당연한 절차임을 깨달았다.    
  
  
