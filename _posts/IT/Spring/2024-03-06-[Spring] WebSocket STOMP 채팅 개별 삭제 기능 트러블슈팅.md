---
title: "[Spring] WebSocket STOMP 채팅 개별 삭제 트러블슈팅"
author: eunchaelyu
date: 2024-03-06 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240306'
---

# WebSocket STOMP 채팅 개별 삭제 트러블슈팅 
     1. java.lang.ClassCastException
     2. 삭제한 사용자만 업데이트 되는 현상    
     3. 채팅메세지 개별 삭제기능 전체 코드    

    
## [1] java.lang.ClassCastException       
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e015156d-b575-4c2d-8718-4584300c7d79)    

- java.lang.ClassCastException은 캐스팅 예외가 발생했다는 에러이다
- "java.util.LinkedHashMap cannot be cast to class com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage"라는 메시지가 나타나는데 쉽게 말해서 LinkedHashMap는 ChatMessage 객체로 캐스팅 되지 못한다는 것이다
  

- Redis에서 채팅 메시지를 가져올 때 ChatMessage 객체가 아닌 LinkedHashMap 객체를 가져오고 있어서 발생한다
- ChatRoomRepository 클래스의 deleteMessageById 메서드에서 ChatMessage 대신 LinkedHashMap을 사용하여 메시지를 처리하도록 수정해야 한다
  

### 수정 전 코드    

```java
    public boolean deleteMessageById(String challengeId, String messageId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        List<Object> messages = listOperations.range(key, 0, -1);
        for (Object message : messages) {
            ChatMessage chatMessage = (ChatMessage) message;
            if (chatMessage.getMessageId().equals(messageId)) {
                listOperations.remove(key, 1, message); // 전체 메시지 객체를 제공하여 삭제
                return true; // 삭제 성공
            }
        }
        return false; // 삭제 실패
    }
```

### 수정 후 코드    

```java
public boolean deleteMessageById(String challengeId, String messageId) {
    String key = CHAT_ROOM_PREFIX + challengeId;
    List<Object> messages = listOperations.range(key, 0, -1);
    for (Object message : messages) {
        Map<String, Object> messageMap = (Map<String, Object>) message;
        if (messageMap.get("messageId").equals(messageId)) {
            listOperations.remove(key, 1, message);
            return true;
        }
    }
    return false;
}
```

- 여기서는 ChatMessage 대신에 Map<String, Object>을 사용하여 메시지를 가져와서 messageId를 비교한다
- 이렇게 하면 LinkedHashMap 객체를 ChatMessage로 캐스팅하는 오류를 방지할 수 있다




## [2] 삭제한 사용자만 업데이트 되는 현상        

```java
    public boolean deleteChatMessage(String challengeId, String messageId) {
        boolean deleteSuccess = chatRoomRepository.deleteMessageById(challengeId, messageId);

        // 삭제 성공 시 삭제된 메시지 정보를 해당 채팅방의 모든 구독자에게 전송
        if (deleteSuccess) {
            ChatMessage deletedMessage = new ChatMessage();
            deletedMessage.setMessageId(messageId);
            deletedMessage.setType(ChatMessage.MessageType.DELETE);

            // 해당 채팅방의 모든 구독자에게 삭제된 메시지 정보 전송
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), deletedMessage);
        }
        return deleteSuccess;
    }
```

- 한 사용자가 특정 채팅방에서 채팅메세지를 선택하여 삭제할 경우 본인의 페이지에서는 삭제된 것이 업데이트 되어 보이도록 프론트단에서 처리할 수 있지만
- 같은 채팅방을 구독하고 있는 사용자들에게는 실시간으로 적용이 되지 않는 문제가 발생했다

- 오류를 해결하기 위해서 로직 흐름을 다시 아래와 같이 정했다    
  1) 클라이언트에서 삭제 요청을 보낸다    
  2) 서버에서 삭제한다      
  3) 서버에서 클라이언트 구독자들한테 DELETE타입 메세지를 보낸다    
  4) 프론트단에서 해당하는 MessageId를 안보이게 한다    

[서버에서 수정해야 할 부분]
- 먼저 MessageType에 DELETE를 추가한다

![스크린샷 2024-03-07 122000](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/1906e735-39da-423c-abd6-2f62a66fc665)            

- ChatMessageService의 deleteChatMessage 메서드에서 삭제 성공 시
- DELETE로 설정한 MessageType과 messageId를 ChatMessage에 저장한다       

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/c74a2ddf-4329-4d3e-8fe8-ffab46442941)    

- 삭제된 메시지 정보를 해당 채팅방의 모든 구독자에게 전송하는 추가 코드를 작성한다        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/cc3773cd-ef0b-4a00-9f18-5421eadb2397)    

- 특정 채팅 메세지를 삭제할 경우 "/sub/chat/challenge/106"를 구독한 사용자들에게 삭제된 메세지 정보를 보내는 것을 볼 수 있다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d5381338-64fc-4582-a1cc-a77e949d1f34)    

<결과>    
- [실시간 채팅 메세지 작성]            
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f1ebfaa4-9e0d-49f2-beea-a084dbcb6f3c)            

- [삭제 요청]        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f9b1aa11-acf5-46f4-b225-37eb70a94400)    

- [삭제 완료 & 구독자들에게 동일하게 실시간 반영]    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ddd291c4-09b7-4e69-bca8-a66bb16aeb6f)    




## [3] 채팅메세지 개별 삭제기능 전체 코드     

### 1. ChatController의 deleteChatMessage 메서드        

```java
    /**
     * 특정 challenge와 연관된 메시지 ID를 사용하여 채팅 메시지를 삭제
     * @param challengeId 삭제할 챌린지의 ID
     * @param messageId   삭제할 메시지의 ID
     * @return 삭제 작업 결과를 포함하는 BaseResponseDto를 담은 ResponseEntity
     */
    @DeleteMapping("/api/chat/{challengeId}/{messageId}")
    public ResponseEntity<BaseResponseDto<String>> deleteChatMessage(@PathVariable String challengeId,
                                                                     @PathVariable String messageId) {
        boolean deleteSuccess = chatMessageService.deleteChatMessage(challengeId, messageId);
        if (deleteSuccess) {
            return ResponseEntity.ok().body(new BaseResponseDto<>(null, "채팅 메시지가 성공적으로 삭제되었습니다.", HttpStatus.OK));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDto<>(null, "채팅 메시지 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
```



### 2. ChatMessageService의 deleteChatMessage 메서드

```java
    /**
     * 채팅 메시지를 삭제하는 메서드
     * @param challengeId 챌린지 식별자
     * @param messageId 삭제할 메시지 번호
     * @return 삭제 성공 여부
     */
    public boolean deleteChatMessage(String challengeId, String messageId) {
        boolean deleteSuccess = chatRoomRepository.deleteMessageById(challengeId, messageId);

        // 삭제 성공 시 삭제된 메시지 정보를 해당 채팅방의 모든 구독자에게 전송
        if (deleteSuccess) {
            ChatMessage deletedMessage = new ChatMessage();
            deletedMessage.setMessageId(messageId);
            deletedMessage.setType(ChatMessage.MessageType.DELETE);

            // 해당 채팅방의 모든 구독자에게 삭제된 메시지 정보 전송
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), deletedMessage);
        }
        return deleteSuccess;
    }
```


### 3. ChatRoomRepository의 deleteMessageById 메서드 

```java
    /**
     * 특정 챌린지방에서 messageId를 사용하여 메시지를 삭제하는 메서드
     *
     * @param messageId   삭제할 메시지의 UUID 식별자
     * @param challengeId 챌린지 식별자
     * @return 삭제가 성공하면 true, 실패하면 false 반환
     */
    public boolean deleteMessageById(String challengeId, String messageId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        List<Object> messages = listOperations.range(key, 0, -1);
        for (Object message : messages) {
            Map<String, Object> messageMap = (Map<String, Object>) message;
            if (messageMap.get("messageId").equals(messageId)) {
                listOperations.remove(key, 1, message);
                return true;
            }
        }
        return false; // 삭제 실패
    }
}
```



