---
title: "[Spring] Redis 적용하여 이전 채팅 내역 저장 및 전송"
author: eunchaelyu
date: 2024-02-26 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240226'
---

    
# Redis 적용하여 이전 채팅 내역 저장 및 전송  

현 프로젝트에 채팅 내역을 저장하려면 다음과 같은 방법으로 구현해야 한다

1. 클라이언트에서 채팅 메시지가 생성되면 해당 메시지를 Redis의 List에 저장하고 
각 채팅방은 별도의 List로 관리 한다.
2. 채팅방의 채팅 내역이 필요한 경우 해당 채팅방의 id를 key로 설정해 List에서 채팅 내역을 조회한다.

**첫번째 시도 방식**

클라이언트가 WebSocket을 통해 서버에 연결 할 때!
클라이언트가 요청하는 **CONNECT 메시지**와 
서버의 응답으로 받는 **CONNECTED 메시지**는 
**WebSocket 연결의 핸드셰이크(handshake)** 과정에서 교환된다.

이 점을 활용해 웹소켓 연결 시 클라이언트로부터 challengeId를 받아 이전 채팅내역을 불러오자

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ccad036b-03ec-49d2-a64e-61eeb0f79ad5)

1. 클라이언트가 WebSocket 연결을 시작하면 먼저 CONNECT 메시지를 서버에 보낸다
이때 클라이언트는 요청하는 **CONNECT 메시지의 헤더**에 challengeId를 포함시킬 수 있다. 
이 CONNECT 메시지를 서버가 받으면, 서버는 해당 헤더에서 challengeId를 추출할 수 있다.

2. 서버는 클라이언트의 요청을 받아들이고 연결을 수락하는 CONNECTED 메시지를 클라이언트에게 보낸다. CONNECTED 메시지를 받는다면 클라이언트가 서버에 성공적으로 연결되었음을 나타낸다. 

**CONNECT 메시지의 헤더 구조**

```bash
CONNECT
challengeId: {실제 challengeId}
accept-version: 1.2,1.1,1.0
heart-beat: 10000,10000
```

주의 할 점! 

 **STOMP 프로토콜을 사용하여 헤더에 Long 값(혹은 다른 자료형)을 직접적으로 보내는 것은 지원되지 않는다.** STOMP 프로토콜의 헤더는 일반적으로 문자열(String) 형식으로 구성되어 있으며, Long 값과 같은 데이터 형식을 헤더로 직접 전송하는 것은 일반적으로 지원되지 않는다.

Spring WebSocket에서 **CONNECT 메시지의** 헤더를 읽어오는 방법 2가지

1. **@MessageMapping 어노테이션과 함께 @Header 어노테이션을 사용**
- 웹 소켓 메시지의 특정 헤더 값을 메서드의 파라미터로 직접 받아올 수 있다.
- 메시지 핸들러 메서드에서 헤더를 사용하는 일반적인 방법

```java
@MessageMapping("/yourEndpoint")
public void yourHandler(@Payload YourPayloadType payload, @Header("yourHeaderName") String yourHeaderValue) {
    // your handler logic
}
```

1.  **StompHeaderAccessor 클래스를 사용**
- 웹 소켓 이벤트 핸들러에서 사용됨.

```java
@EventListener
public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String yourHeaderValue = (String) headerAccessor.getFirstNativeHeader("yourHeaderName");
    // your logic here
}
```

웹소켓 연결 시 채팅 내역을 전송하기로 했으므로 이 방법을 선택한다.

**클라이언트 측 추가 코드**

- 헤더에 challengeId를 담아서 서버에 보낸다

```bash

  useEffect(() => {
    const socket = new SockJS(`https://api.eroom-challenge.com/ws-stomp`);
    const client = new Client({
      brokerURL: `ws://api.eroom-challenge.com/ws-stomp`,
      webSocketFactory: () => socket,
      connectHeaders:{
        challengeId: String(challengeId),
        },
```

**서버 측 코드**

- 서버에서 웹소켓이 연결되면 헤더로부터 challengeId를 받는다

```bash

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");

        // WebSocket 연결이 시작될 때 채팅 내역을 불러와 사용자에게 전송
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String challengeId = (String) headerAccessor.getSessionAttributes().get("challengeId");

        if (challengeId != null) {
            // 채팅 내역 불러오기
            Iterable<ChatMessage> chatHistory = chatRoomRepository.getChatHistory(challengeId);

            // 채팅 내역을 사용자에게 전송
            for (ChatMessage chatMessage : chatHistory) {
                messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
            }
        }
    }
```

하지만 웹소켓이 연결되면서 challengeId를 받고 있지만 서버에서 처리하지 못하여
이전 채팅내역을 프론트단에 전송하지 못하고 있다

```java
 이벤트 Web Socket Opened...
Chat.jsx:60 이벤트 >>> CONNECT
challengeId:65
accept-version:1.2,1.1,1.0
heart-beat:10000,10000

Chat.jsx:60 이벤트 Received data
Chat.jsx:60 이벤트 <<< CONNECTED
user-name:lanchaelog@kakao.com
heart-beat:0,0
version:1.2
content-length:0

Chat.jsx:60 이벤트 connected to server undefined
Chat.jsx:60 이벤트 >>> SEND
destination:/pub/chat.sendMessage/65
content-length:49

Chat.jsx:60 이벤트 >>> SUBSCRIBE
id:sub-0
destination:/sub/chat/challenge/65

Chat.jsx:60 이벤트 Received data
Chat.jsx:60 이벤트 <<< MESSAGE
content-length:153
message-id:g1hrcmf1-44
subscription:sub-0
content-type:application/json
destination:/sub/chat/challenge/65
content-length:153

Chat.jsx:60 이벤트 Received data
Chat.jsx:60 이벤트 <<< MESSAGE
content-length:261
message-id:g1hrcmf1-46
subscription:sub-0
content-type:application/json
destination:/sub/chat/challenge/65
content-length:261

```

**두번째 시도 방식**

**해결방안**

1. 웹소켓 연결이 되고 프론트단에서 ChatMessage에 JOIN type을 설정해서 서버로 보내는 시점에 이전 채팅 메시지를 프론트단으로 전송하는 로직으로 변경한다.
2. 클라이언트가 새로고침하거나 페이지 이동 후 다시 채팅방에 들어올 때에도 프론트단에서JOIN type을 설정하여 서버에 보내기 때문에 채팅방에 들어왔을 때 해당 채팅방의 이전 메시지를 가져오도록 유도한다.
3. 서버는 클라이언트의 요청을 받으면 해당 채팅방의 이전 메시지를 Redis에서 가져와 클라이언트에게 응답한다.
4. 클라이언트는 서버로부터 받은 이전 메시지를 적절히 처리하여 화면에 보여준다.

```java
// Redis에 채팅 메시지 저장
chatRoomRepository.saveChatMessage(challengeId, chatMessage);

switch (chatMessage.getType()) {
  case JOIN -> {
     System.out.println("MessagesType : JOIN");
     // 사용자가 챌린지 방에 입장할 때 ChatRoomService를 통해 currentMemberList에 추가
    chatRoomService.userJoinedRoom(challengeId, memberIdString, senderNickname, profileImageUrl);
	}
}
```

- `ChatMessageService`의 saveMessage메서드에서 ChatMessage 형태로 들어온 채팅 메시지는 모두 redis에 저장하고 사용자가 챌린지 방에 입장할 때 이전 채팅내역을 불러오는 로직을 추가 작성한다

```java
    /**
     * 사용자가 채팅방에 입장했을 때 실행되는 메서드
     * @param challengeId 채팅방의 고유 식별자
     * @param memberId 사용자의 고유 식별자
     * @param senderNickname 사용자의 닉네임
     * @param profileImageUrl 사용자의 프로필 이미지 URL
     */
    public void userJoinedRoom(String challengeId, String memberId, String senderNickname, String profileImageUrl) {

        List<MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);

        // 현재 멤버 리스트가 없는 경우 새로운 리스트 생성
        if (currentMemberList == null) {
            currentMemberList = new ArrayList<>();
            challengeRoomMemberLists.put(challengeId, currentMemberList);
        }
        // 새로운 멤버 정보 추가
        MemberInfo memberInfo = new MemberInfo(memberId, senderNickname, profileImageUrl);
        currentMemberList.add(memberInfo);

        // 해당 채팅방의 이전 대화 내용 불러오기
        List<ChatMessage> chatHistory = chatRoomRepository.getChatHistory(challengeId);

        // 채팅방의 구독자들에게 이전 대화 내용 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatHistory);
        // 채팅방의 구독자들에게 현재 멤버 리스트 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
    }
```

- JOIN type일 때 현 참여 리스트와 이전 채팅 내역을 프론트단에 전송하는 것까지 성공했다!!

- 하지만 n번 채팅방을 신청한 챌린지원들에게 같은 구독 주소로 응답하다보니 
구독자 중 1명만 새로고침하거나 다시 웹소켓에 재접속 해도 모든 챌린지원들에게 
매번 이전 채팅 내역이 불러와지는 것이다

해결!

- 이전 채팅 내역을 전송할 때는 구독 주소를 다르게 하고 memeberId를 추가하여 
채팅방에 웹소켓 연결 시도한 그 member에게만 이전 채팅 내역이 전송되도록 변경하면서
해결했다.

```java
        // 채팅방의 구독자들에게 이전 대화 내용 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s/history/%s", challengeId, memberId), chatHistory);
        // 채팅방의 구독자들에게 현재 멤버 리스트 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
```

- 물론 프론트단의 코드에서도 변경되어야 한다 (구독 주소)

```
      client.subscribe(`/sub/chat/challenge/${challengeId}/history/${memberId}`, (message) => {
        const history = JSON.parse(message.body);
        console.log('이건 히스토리', history);
        console.log('이건 히스토리 메시지', message);

```

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/f22e68c3-a5be-45b4-b580-7c7afbd72b50/844be1db-7d53-44ab-9bbf-d6b6cd06c590/Untitled.png)

memberId 가 3번인 member가 challengeId가 65번인 방에 접속(웹소켓 연결) 하게 되면 

/sub/chat/challenge/${challengeId}/history/${memberId} 라는 주소를 SUBSCRIBE 하고 

이전 채팅 내역을 Received 하는 것을 확인 할 수 있다!

- redis 이해하기 위한 레퍼런스
[STOMP Protocol Specification, Version 1.2](https://stomp.github.io/stomp-specification-1.2.html)    
