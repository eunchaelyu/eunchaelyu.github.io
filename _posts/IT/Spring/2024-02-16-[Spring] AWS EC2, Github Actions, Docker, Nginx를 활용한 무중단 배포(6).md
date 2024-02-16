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
     15. AWS 설정
     16. 무중단 배포 확인  
     17. 트러블 슈팅    

    
## [15] AWS 설정   
### 1. 인바운드 규칙
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/066b3762-191c-453e-9039-7cb99d717215)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d0f145cf-0a24-4c9e-be72-f7b71cfcba5c)    

- 이 요청을 날리는 대상이 GitHubActions ubuntu ! 즉, 외부 서버이다.     
- 외부 서버에서는 해당 8080, 8081 접속이 안된다    
- 왜냐하면 EC2에서 포트 설정을 안해줬기 때문이다

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/916ba4ca-d530-4f48-b9de-cfb8aeed27c8)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9e9a2cf1-683e-4e2b-92f7-57f88d01aea5)

- 현재 보안그룹 > 해당 인스턴스가 launcha-wizard-2로 잡혀있다( HTTP 80은 열려있다, HTTP 8081, HTTP 8080 열어줘야한다 )

  
### 2. Route 53      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/08209b85-dfb4-40b7-b894-68c0c76ffc76)

- [x] 리액트 (프론트단) 배포 시
1. vercel로 "eroom-challenge.com"랑 "www.eroom-challenge.com" 도메인명으로 두 개 배포    
2. "www.eroom-challenge.com"로 요청 시 "eroom-challenge.com"로 리다이렉트    
3. env에 백엔드 url 설정 "https://api.eroom-challenge.com"
4. vercel 배포시 key, value 값에 "REACT_APP_SERVER_URL", "https://api.eroom-challenge.com" 각각 secrets로 저장하기
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0e0bafb7-b8a5-4ca4-baf3-b1d019987c1d)

- [x] 스프링 (백엔드) 배포 시
1. api.eroom-challenge.com로 배포하기
2. 인증서 "*.eroom-challenge.com" 추가 발급 및 연결 필요
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/be606a79-4267-407b-9246-5e781cfd1819)    


### 3. RDS 생성             
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/77e1b690-56bc-4cf8-8457-d4473abe7833)        
USER, PASSWORD, 엔드포인트 잘 기억해두고 Intellij에서 연결 및 yml 설정 파일에 사용    


### 4. 로드밸런서 설정    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/77f59e5c-5946-4906-853d-286736f56788)            
- 8080,8081,443 포트로 연결된 리스너를 각각 생성한다    
- eroomTargetGroup은 8080포트로 연 리스너와 연결         
- eroomTargetGroup8081은 8081 포트로 연 리스너와 연결    
- 각 대상그룹은 8080,8081 포트를 열어준다    



## [16] 무중단 배포 확인    
### 1. build    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/83d4d12a-7126-457b-8350-aa1c0e073046)

### 2. deploy      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/383d18c9-40d0-479f-b658-5651d89370f9)


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


## [17] 트러블 슈팅    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/618d2c09-f8a3-493b-8b41-2bc9d2e54126)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4b3464b5-f7d3-4ec4-be76-d067b6a3af4c)
- 무중단 배포 후 1분 정도 502에러와 200이 번갈아가며 뜨다가 안정화 되는 현상    
- 몇일 동안 삽질했는데 nginx, blue, green 인스턴스 3개를 사용하는 방법도 있었고 
- 인스턴스 하나로 사용하지만 502, 200 번갈아 뜨는 현상에 대한 트러블 슈팅은 없는 블로그도 있었다    
- 지금은 8080,8081 대상그룹이 나뉘어져 있지만 하나의 인스턴스로 연결되어 있다

- 인스턴스 3개는 비용이 부담스럽고 생각한 해결 방법은 8081에 대한 인스턴스를 하나 더 생성해서 대상그룹에 연결해주고         
- nginx에 설정했던 blue green ip 주소를 추가 및 변경해보는 것이다    

- 인스턴스 시작!  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d20661e9-dc70-480b-aa64-93ea606f72f4)

- 이름 설정 > Ubuntu 체크     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/066ddccd-c4df-4aea-ba62-ab0f4ae5aa7a)

- 키페어 생성
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/376c8c08-4176-4f5a-ad93-c635d1afe591)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/49bc8ac0-7dee-4445-9b6b-52efd4bfbbce)

- 보안그룹 (새로 생성할 필요 없다고 생각해서 기존 보안 그룹 선택)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c9448799-9000-4e03-8252-36e47697cb2c)    

- 스토리지 (프리티어는 30GB까지 가능하므로 29 선택)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c364b87b-5e89-49fe-9c80-55e2bcf24dce)

- 인스턴스 실행중인지 확인
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c8a8dd74-7663-42fe-95c6-fc4ac7b45a53)

- RDS 랑 방금 생성한 인스턴스 연결
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d17345c6-62de-4a83-bbc0-58b7b925c544)

- 탄력적 IP 할당
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/bd174ba1-ab18-4354-9288-56321781fdef)

- 인스턴스랑 연결
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9cb6c508-e939-4544-b384-cc420270252c)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/a9b2703d-1a8a-4a63-ad82-386c46a4bf98)

- 인스턴스 RDS, 탄력적 IP 연결 완료     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b908bedb-833d-4eb7-b67f-af66b6868f5c)

- 로드밸런서 8081 포트로 연 대상그룹에 들어오기     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/b18357b9-8b3b-4e1a-937e-291b9aeab0d1)

- 방금 생성한 인스턴스의 8080,8081 포트를 대상 등록    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/2c914910-abb5-4fe8-8bd7-a2d41faa7874)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f9b27d84-3b91-4bdd-9d83-d25980fc5004)    


- default.conf 파일 수정    
```
upstream blue {
		server 44.219.159.74:8080;
		server 184.73.14.52:8080;
}
upstream green {
		server 44.219.159.74:8081;
		server 184.73.14.52:8081;
}
server {

        listen 80;
        listen [::]:80;
	server_name api.eroom-challenge.com;

        access_log  /var/log/nginx/access.log;
        error_log /var/log/nginx/error.log;

        include /etc/nginx/conf.d/service-env.inc;

    	location / {
             proxy_pass http://$service_url;  # reverse proxy의 기능

             proxy_set_header X-Real-IP $remote_addr;
	     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_set_header Host $http_host;
 
             root /usr/share/nginx/html;
             index index.html index.htm;
    }

    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
	root     /usr/share/nginx/html;
    }
}
```

- 무중단 배포 완료
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8ab4bcb3-ec5a-4592-806d-d137e94aa2b5)






