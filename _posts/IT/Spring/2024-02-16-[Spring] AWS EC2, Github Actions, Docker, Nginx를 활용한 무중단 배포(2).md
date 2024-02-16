---
title: "[Spring] AWS EC2, Github Actions, Docker, Nginx를 활용한 무중단 배포(2)"
author: eunchaelyu
date: 2024-02-16 08:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240216'
---

# 무중단 배포 구현하기 - (2)     
    5. Nginx 설치    
    6. Nginx 세팅    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/cec52020-2136-498a-81a5-f3d1a0dc8e88)
- Nginx의 역할: 8080 blue 서버가 정상적으로 작동하고 있다면 green 서버를 켠다    
                green 서버가 정상적으로 작동하면 Nginx가 green 서버를 가리키게 되고 blue와의 연결을 끊고 서버를 중지한다    
                8081 green 서버가 정상적으로 작동하고 있다면 blue 서버를 켠다    
                blue 서버가 정상적으로 작동하면 Nginx가 blue 서버를 가리키게 되고 green과의 연결을 끊고 서버를 중지한다
- 즉, 서버의 중단 없이 무중단 배포가 가능하게 된다
                  

## [5] Nginx 설치 
- 도커 허브에서 nginx를 검색하면 명령어 볼 수 있다      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/67f5ae6d-ea8e-4b5c-8278-bde180525c26)    

- 별도로 nginx 설치 필요없이 이미 도커 허브에 있는 도커 이미지 통해서 자동으로 세팅된다    
- ``` docker pull nginx``` 명령어 확인하고 터미널에 입력한다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ea710d7b-79e8-4947-aadc-98769c01921b)    

### Trouble Shooting 1
- 도커 이미지를 통해 pull 할 때 다음과 같은 에러가 생긴다면?    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6fd1b93f-3151-45fa-990e-468b249ff4ee)

- 도커가 실행중인지 확인하기    
```systemctl status docker```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/9d3f6cf6-332f-42db-b6a6-71e946d388ec)

- 실행되고 있지 않으므로 아래의 명령어를 실행한다
- 도커 시작       
```sudo systemctl start docker```
- 도커 설치 확인
```docker --version```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/29e8cb48-4329-47be-af52-50cffdfbc6c0)

- root라는 사용자를 도커그룹에 추가 & 권한 설정
```sudo usermod -aG docker root```    
- 도커 재실행    
```sudo systemctl restart docker```
- 도커 실행중인지 확인    
```systemctl status docker```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/8907e136-97f5-460f-855a-3cbc31e79420)

**트러블 슈팅 해결!!**     

- 다시 Nginx 명령어 실행        
```docker pull nginx```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/24330229-35d7-4f99-aa9a-db1f90ee9eab)

- 도커 컨테이너를 실행한다    
```docker container run --name nginxserver -d -p 80:80 nginx```

### Trouble Shooting 2    
- 기존 컨테이너가 있을 경우 아래와 같은 에러 메세지가 발생    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6b2eb4c1-4c10-46fb-9087-2fc4905575a6)

- 먼저 아래 명령어로 현재 실행중인 도커 컨테이너 확인한다        
```docker ps -a```    

- 기존 컨테이너 제거    
```docker container rm 4c630c0cbcad```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0d692cc4-8a6c-4310-864d-07d6eb8c7ac4)

**트러블 슈팅 해결!!**     

- 다시 도커를 해당 컨테이너에 올려서 Nginx를 실행시킨다        
```docker container run --name nginxserver -d -p 80:80 nginx```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/de76e597-2449-4d63-aa0f-9a85878d28ac)    

- 도커 컨테이너 id 이미지 상태 포트 이름 확인    
```docker ps```     
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/147e67f2-21dc-43b7-ab8a-7a2a080736a4)        

- 도커, nginx 설치 완료!    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/02607160-2f62-45ef-9bb5-fa4fada23057)   



## [6] Nginx 세팅      
### Nginx 에 접속
- 외부에서 접속시 EC2 안에 가상의 공간으로 들어가야 Nginx에 접속할 수 있다        
```docker exec -it nginxserver bash```

- (Ngnx에서 나올 때 명령어)
```exit```
  
  
- root 계정의 해당 컨테이너 안으로 들어온 것을 의미한다        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dc662719-69b0-48e9-bb83-0d1c00e85e2f)

- (도커 컨테이너 Id는 Nginx 접속 전 아래의 명령어로 확인할 수 있다)    
```docker ps```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/aa579b37-ab06-4cc6-bd8c-b194f88e01d3)      
 
### 주요 편집기 설치(vim, nano)
- vim은 내용 수정 전 전체 삭제시 용이
- nano는 변경사항을 줄바꿈, 들여쓰기 그대로~ 붙여넣기가 가능해서 구분해서 사용한다        

- vim 에디터 설치        
```apt-get update```        
```apt-get upgrade```        
```apt-get install vim```        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/a28d9a6a-22b2-4151-8eef-8831285854db)    

- nano 에디터 설치            
```apt-get update```          
```apt-get upgrade```        
```apt-get install nano```        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d4c9e3aa-f54c-4d6d-b311-11ba43c8b017)    


### Nginx default.conf 파일 생성 및 설정       
- ls 명령어로 폴더와 파일명을 확인 > etc확인 후 경로 이동        
```cd etc/```    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/221dfe0a-8624-4163-80d0-c7300803c932)    
    
- cd etc/nginx/ 로 경로 이동    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ac248f66-1263-4d25-bd3f-f7061c878a2e)        
    
- cd etc/nginx/conf.d/ 경로로 이동        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/647954e1-ee33-4763-a7e0-3487be19bb74)        

**Tip: + Tab 키 누르면 하위 폴더가 보여짐**  

- 해당 파일에 vim 에디터로 접속
```vim default.conf```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/7dbccde1-14ef-4730-af1d-0e889191691c)

- **편집할 때:** i 를 입력하면 왼쪽하단에 --INSERT-- 라고 뜨면서 편집이 가능!
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/743d7cae-efb6-437f-be2d-a9e469c5ae2c)

- **나갈 때:** Esc키 누르고 :q(저장하지 않고 나감)  또는 :wq(저장하고 나감) 입력 하면 화면에서 나간다 (왼쪽 하단에 집중)      
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dae0774d-9058-4236-afb9-ba34d73575f7)    

- **전체 내용 삭제:** ggDG 한글자씩 입력하면 한번에 삭제가 가능하다    


- default.conf 전체 파일        
```yml
upstream blue {    
		server 44.219.159.74:8080;  
}    
upstream green {    
		server 44.219.159.74:8081;    
}    
server {    

        listen 80;    
        listen [::]:80;    
	server_name api.eroom-challenge.com;  

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

### service-env.inc 파일 생성 및 설정    
- service_url을 green으로 set       
       
```nano service-env.inc```      

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/01878822-0361-460e-92be-1eb260999593)              

- service-env.inc 파일            
- 초기 서버세팅을 green 서버로 진행하겠다는 뜻        
    
```    
set $service_url green;    
```    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/679c67f0-5503-4141-a86d-186afb89e898)               
    

**주의 할점**    
- Nginx의 default.conf 파일과 service-env.inc 파일을 생성 및 수정 할 때        
- 반드시 ```docker exec -it nginxserver bash``` 이 명령어로 Nginx 서버에 접속 후 진행해야 된다는 점을 잊지 말자!!    






















