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

- ``@EnableWebSocketMessageBrokerWebSocket`` 서버를 활성화하는 데 사용된다    
- ``WebSocketMessageBrokerConfigurer`` 인터페이스를 구현하여 웹소켓 연결을 구성에 필요한 일부 메소드에 대한 구현을 제공한다        

```
    @Override
    // Stomp 엔드포인트 등록: 특정 도메인에서만 웹소켓 연결을 허용
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") 
                .setAllowedOriginPatterns("*") 
                .withSockJS() 
                .setHeartbeatTime(1000); 
    }
```    

- 클라이언트 측에서 연결할 때 사용할 엔드포인트를 ``ws-stomp``로 설정했다
- 해당 도메인에서만 웹소켓 연결 허용하되 개발 중에는 모두 허용하고("*") 필요에 따라 조정한다    
- ``withSockJS()``끝점 구성과 함께 사용한다. SockJS는 웹소켓을 지원하지 않는 브라우저에 대한 대체 옵션을 활성화 함    
- 클라이언트와 서버의 연결 상태 확인 주기 : 1초로 설정한다      

```
    @Override
    // 
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/sub"); 
        registry.setApplicationDestinationPrefixes("/pub"); 
    }
```

- 위의 코드는 메시지 브로커를 설정하는 코드이다
- 수신 메시지를 받는 데 사용되는 URL:  ``/queue``는 개별 메시지, ``/sub``는 해당 주제를 구독한 이들의 전체 메시지를 나타낸다
- 송신 메시지를 전송하는 데 사용되는 URL: 클라이언트 측에서 메세지 전송 시 ``/pub``로 시작하는 메시지는 처리 메서드로 라우팅된다

```
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(64 * 1024); 
        registration.setSendTimeLimit(10 * 10000); 
        registration.setSendBufferSizeLimit(512 * 1024); 
    }
```

- 웹소켓 전송 설정 조정하는 코드이다
- 메시지 최대 크기 default : 64 * 1024
- 메시지 전송 시간 default : 10 * 10000
- 버퍼 사이즈 default : 512 * 1024


### 2. ChatMessage에서 모델 생성    
```
@Getter
@Setter
public class ChatMessage {
    private MessageType type;
    private String message;
    private String sender;

    private LocalDateTime time;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return message;
    }

    public void setContent(String content) {
        this.message = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
```

- ChatMessage모델은 클라이언트와 서버 간에 교환될 메시지 페이로드이다


### 3. 메세지를 주고받기 위한 ChatController    

```
@Controller
public class ChatController {
    @MessageMapping("/chat.sendMessage")
    @SendTo("/sub/chat/challenge/{challengeId}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/sub/chat/challenge/{challengeId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now());
        return chatMessage;
    }
```

- ``@MessageMapping``: websocket 구성에 따라 대상이 ``/pub``로 시작하는 클라이언트에서 보낸 모든 메시지는 주석이 달린 ``/pub`` 메시지 처리 방법으로 라우팅된다
  예시    
- ``sendMessage()``:  메서드 ``/pub/chat.sendMessage``로 라우팅되고        
- ``addUser()``: 메서드 ``/app/chat.addUser``로 라우팅됩니다     


### 4. WebSocket 이벤트 리스너    

```  
@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChallengerRepository challengerRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        // 이벤트에서 Stomp 헤더 접근
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // 챌린지 ID 가져오기
        Long challengeId = (Long) headerAccessor.getSessionAttributes().get("challengeId");
        // 회원 ID 가져오기
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");

        if (challengeId != null && memberId != null) {
            // 챌린지와 회원 엔터티를 찾음
            Optional<Challenge> challengeOptional = challengeRepository.findById(challengeId);
            Optional<Member> memberOptional = memberRepository.findById(memberId);

            // 챌린지와 회원이 존재하는 경우에만 처리
            if (challengeOptional.isPresent() && memberOptional.isPresent()) {
                Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challengeOptional.get(), memberOptional.get());
                // 챌린저가 존재하는 경우 닉네임을 세션에 저장
                challengerOptional.ifPresent(challenger -> headerAccessor.getSessionAttributes().put("nickname", challenger.getMember().getNickname()));
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        if(nickname != null) {
            logger.info("User Disconnected : " + nickname);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(nickname);
            chatMessage.setTime(LocalDateTime.now());

            messagingTemplate.convertAndSend( "/sub/chat/challenge/{challengeId}", chatMessage);
        }
    }
}
```

-  소켓 연결 및 연결 해제 이벤트를 수신하는 코드이다
-  채팅에 참여중인지, 나간상태인지 기록도 가능하다    
