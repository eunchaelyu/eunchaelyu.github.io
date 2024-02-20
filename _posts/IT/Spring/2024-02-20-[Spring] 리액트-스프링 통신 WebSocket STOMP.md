---
title: "[Spring] 리액트-스프링 통신 WebSocket STOMP"
author: eunchaelyu
date: 2024-02-20 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240220'
---

# 리액트-스프링 통신 WebSocket STOMP   
     1. 웹소켓이란?
     2. WebSocket VS HTTP  
     3. 기술적 스택 결정  
     4. WebSocket API를 사용하는 방법

    
## [1] 웹소켓이란?        
- 서버와 클라이언트 사이에 양방향 통신 채널을 구축할 수 있게 해주는 통신 프로토콜이다    
- 서버와 일반 HTTP 연결을 설정한 다음 헤더를 보내 양방향 웹소켓 연결로 업그레이드 하는 방식이다    
- 전이중 통신을 제공하여 실시간성을 보장하기 때문에 채팅이나 게임 등의 서비스에서 사용할 수 있다


## [2] WebSocket VS HTTP 
1) 웹소켓      

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/0c11268e-2a03-44cf-88c7-a289ab890291)    

- 연결을 지향한다     
- 한 번 연결을 맺은 후 유지한다    
- 양방향 통신이다     


2) HTTP 통신    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6e049405-3cd4-436f-88d5-53303ff8b6a5)   

- 비연결성: 연결을 맺고 요청을 하고 응답을 받으면 연결을 끊는다        
- 무상태성: 서버가 클라이언트의 상태를 가지고 있지 않다            
- 단방향 통신이다    
   


## [3] 기술적 스택 결정    
- HTTP 프로토콜은 client의 요청이 있을 경우 서버가 응답하여 단방향으로 통식이 가능하지만 양방향인 듯 비슷하게 구현할 수 있다           
- 그러나 주고받는 데이터의 양이 많아지게 될수록 연결을 기다리는 시간이 성능 저하에 영향을 끼치게 된다            
- 필요한 경우에만 데이터를 주고 받을 때 HTTP 프로토콜을 사용하는 것이 더 효율적이다      

- 현 프로젝트에서는 같은 챌린지를 하는 사람들끼리 대화하는 공간, 채팅을 구현하기로 했다             
- 이러한 HTTP 통신의 경우 실시간 통신에 적합하지 않다고 생각한다          
- HTTP 통신처럼 연결을 맺고 바로 끊어버리는 게 아닌 실시간으로 데이터를 주고받는 통신이 필요하므로 웹소켓을 선택한다          


## [4]  WebSocket API를 사용하는 방법    
### 1. WebSocketConfig    
```
package com.sparta.eroomprojectbe.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    // Stomp 엔드포인트 등록: 특정 도메인에서만 웹소켓 연결을 허용
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // 연결될 Endpoint
                .setAllowedOriginPatterns("*") // 해당 도메인에서만 웹소켓 연결 허용. 개발 중에는 모두 허용하되 필요에 따라 조정
                .withSockJS() // websocket 관련 자바스크립트 라이브러리 SockJS 설정
                .setHeartbeatTime(1000); // 클라이언트 - 서버 연결 상태 확인 주기 : 1초
    }

    @Override
    // 메시지 브로커 설정
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/sub"); // 수신 메시지를 받는 데 사용되는 ULR /queue는 개별 메시지, /sub는 해당 주제를 구독한 이들의 전체 메시지
        registry.setApplicationDestinationPrefixes("/pub"); // 송신 메시지를 전송하는 데 사용되는 URL

    }

    @Override
    // 웹소켓 전송 설정 조정
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(64 * 1024); // 메시지 최대 크기 default : 64 * 1024
        registration.setSendTimeLimit(10 * 10000); // 메시지 전송 시간 default : 10 * 10000
        registration.setSendBufferSizeLimit(512 * 1024); // 버퍼 사이즈 default : 512 * 1024
    }
}
```











