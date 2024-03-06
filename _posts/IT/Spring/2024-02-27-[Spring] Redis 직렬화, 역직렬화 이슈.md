---
title: "[Spring] Redis 직렬화/역직렬화 이슈 "
author: eunchaelyu
date: 2024-02-27 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240227'
---

    
# Redis 직렬화/역직렬화 이슈     
- 채팅 메시지를 Redis에 저장하는 동안 "SerializationException"이 발생
- Redis에 Java 8의 LocalDateTime을 저장하려고 할 때 발생하는 문제인데 처리할 수 있는 모듈이 필요하다라는 뜻 !


```bash
2024-02-29T07:42:08.065Z ERROR 1 --- [boundChannel-19] .WebSocketAnnotationMethodMessageHandler : Unhandled exception from message handler method
org.springframework.data.redis.serializer.SerializationException: Could not write JSON: Java 8 date/time type java.time.LocalDateTime not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage["time"])
at org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer.serialize(GenericJackson2JsonRedisSerializer.java:255) ~[spring-data-redis-3.2.2.jar!/:3.2.2]
at org.springframework.data.redis.core.AbstractOperations.rawValue(AbstractOperations.java:128) ~[spring-data-redis-3.2.2.jar!/:3.2.2]
at org.springframework.data.redis.core.DefaultListOperations.leftPush(DefaultListOperations.java:110) ~[spring-data-redis-3.2.2.jar!/:3.2.2]
at com.sparta.eroomprojectbe.domain.chat.repository.ChatRoomRepository.saveChatMessage(ChatRoomRepository.java:32) ~[!/:0.0.1-SNAPSHOT]
at com.sparta.eroomprojectbe.domain.chat.service.ChatMessageService.lambda$saveMessage$0(ChatMessageService.java:80) ~[!/:0.0.1-SNAPSHOT]
at java.base/java.util.Optional.ifPresent(Optional.java:178) ~[na:na]
at com.sparta.eroomprojectbe.domain.chat.service.ChatMessageService.saveMessage(ChatMessageService.java:60) ~[!/:0.0.1-SNAPSHOT]
at com.sparta.eroomprojectbe.domain.chat.controller.ChatController.sendMessage(ChatController.java:27) ~[!/:0.0.1-SNAPSHOT]
at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:na]
at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:78) ~[na:na]
at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:na]
at java.base/java.lang.reflect.Method.invoke(Method.java:568) ~[na:na]
at org.springframework.messaging.handler.invocation.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:169) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.handler.invocation.InvocableHandlerMethod.invoke(InvocableHandlerMethod.java:119) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler.handleMatch(AbstractMethodMessageHandler.java:567) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler.handleMatch(SimpAnnotationMethodMessageHandler.java:511) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler.handleMatch(SimpAnnotationMethodMessageHandler.java:93) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler.handleMessageInternal(AbstractMethodMessageHandler.java:522) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler.handleMessage(AbstractMethodMessageHandler.java:457) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at org.springframework.messaging.support.ExecutorSubscribableChannel$SendTask.run(ExecutorSubscribableChannel.java:144) ~[spring-messaging-6.1.3.jar!/:6.1.3]
at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1135) ~[na:na]
at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635) ~[na:na]
at java.base/java.lang.Thread.run(Thread.java:831) ~[na:na]
Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type java.time.LocalDateTime not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage["time"])
at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:77) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.SerializerProvider.reportBadDefinition(SerializerProvider.java:1308) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer.serialize(UnsupportedTypeSerializer.java:35) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.BeanPropertyWriter.serializeAsField(BeanPropertyWriter.java:732) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:772) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeWithType(BeanSerializerBase.java:655) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer.serialize(TypeWrappedSerializer.java:32) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider._serialize(DefaultSerializerProvider.java:479) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:318) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ObjectMapper._writeValueAndClose(ObjectMapper.java:4719) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at com.fasterxml.jackson.databind.ObjectMapper.writeValueAsBytes(ObjectMapper.java:3987) ~[jackson-databind-2.15.3.jar!/:2.15.3]
at org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer.serialize(GenericJackson2JsonRedisSerializer.java:252) ~[spring-data-redis-3.2.2.jar!/:3.2.2]
... 22 common frames omitted
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/689aa77d-4758-49dc-b153-6931c83b6f74)

- 위의 사진 처럼 이전 채팅내역 전송 시 시간 데이터가 제대로 보내지지 않는 것을 확인할 수 있다    
  
- Redis에 객체를 저장하고 조회할 때는 객체를 직렬화하고 역직렬화해야 한다는 사실을 알게 되었다.    
- 즉,  Java 객체를 JSON으로 변환하고 다시 JSON을 Java 객체로 변환하는 모듈을 추가해야 함.    

## [1] **해결방법**    
### 1. build.gradle 파일에 jackson-datatype-jsr310 모듈 추가함    
JSON과 Java의 시간 관련 데이터 유형 간의 변환을 간단하게 처리할 수 있으므로     
직렬화하고 역직렬화 하는데에 코드 변경이 적다    

    
    ```bash
    dependencies {
        implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.3'
    }
    ```
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/da95562e-9464-47b8-b714-07ab8d2ce31c)

    
### 2. "LocalDate", "LocalTime", "LocalDateTime", "ZonedDateTime" 등의 데이터 유형을 JSON으로 직렬화하고 역직렬화 할 수 있도록 지원되므로 "RedisConfig" 클래스의 "redisTemplate" 빈에 "ObjectMapper"를 구성하여 모듈을 등록

**기존 코드**
    
```bash
        /**
         * 어플리케이션에서 사용할 redisTemplate 설정
         */
        @Bean
        public RedisTemplate<String, ChatMessage> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, ChatMessage> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);
    
            // Key와 Value의 Serializer 설정
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    
            return redisTemplate;
        }
    
```
    
**수정 코드**
    
```bash
        /**
         * 어플리케이션에서 사용할 redisTemplate 설정
         */
    		@Bean
        public RedisTemplate<String, ChatMessage> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, ChatMessage> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);
    
            **ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());**
    
            // Key와 Value의 Serializer 설정
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(**objectMapper**));
    
            return redisTemplate;
        }
    }
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4a390a4b-42c0-42da-867d-aa4c2f1f8175)    

    
### 3. time을 사용하는 ChatMessage 엔티티 클래스에서 직렬화 역직렬화 주석을 달아준다    
    
    ```java
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime time;
    ```
    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4942d48b-160b-4e46-b727-54274eb90c93)
  
#### 1) "@JsonSerialize(using = LocalDateTimeSerializer.class)"    
해당 변수를 직렬화할 때 사용할 **LocalDateTimeSerializer** 클래스를 지정한다. **LocalDateTime** 객체를 JSON 문자열로 변환할 때 사용된다.    

#### 2) "@JsonDeserialize(using = LocalDateTimeDeserializer.class)"   
해당 변수를 역직렬화할 때 사용할 "LocalDateTimeDeserializer" 클래스를 지정한다.   
JSON 문자열을 "LocalDateTime" 객체로 변환할 때 사용된다.    

#### 3) "@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")"    
해당 변수의 JSON 표현을 "yyyy-MM-dd'T'HH:mm:ss"로 형식화 한다.     
원하는 형식으로 클라이언트에 전송하기 위함이다.    

## [2] Redis 에 저장이 잘 되었는지 확인       

- Redis 클라이언트를 사용하여 Elasticache 클러스터에 연결    

```java
redis-cli -h [클러스터 ID] -p 6379
```

- chat_room:1 키에 저장된 리스트의 모든 요소를 가져오기    

```java
LRANGE chat_room:1 0 -1
```

- 1번 채팅방의 모든 채팅내역 리스트 조회!      

```bash
 1) "{\"messageId\":\"d7a5d044-d1a3-4f2f-b9db-189c01eae554\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-04T22:52:30\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
 2) "{\"messageId\":\"b96b021a-ac88-4f1d-9ae3-7dc1be7e1abe\",\"type\":\"LEAVE\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":null,\"memberId\":null,\"challengeId\":\"103\",\"profileImageUrl\":null}"
 3) "{\"messageId\":\"441b94bf-d525-4785-9c89-57706f87ed27\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-04T22:52:35\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
 4) "{\"messageId\":\"e696fab5-25d9-4ad9-bbdc-94c1de6597f3\",\"type\":\"LEAVE\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":null,\"memberId\":null,\"challengeId\":\"103\",\"profileImageUrl\":null}"
 5) "{\"messageId\":\"5e6e89d8-fd4e-4b25-81af-398569caacef\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-04T22:52:38\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
 6) "{\"messageId\":\"d438b71e-11d0-4587-9f48-12b29c3ecd28\",\"type\":\"LEAVE\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":null,\"memberId\":null,\"challengeId\":\"103\",\"profileImageUrl\":null}"
 7) "{\"messageId\":\"ad607619-e8bd-4404-bcdf-3e027e1c8e7d\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-04T22:52:47\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
 8) "{\"messageId\":\"0639c063-6c5b-4fb4-8ca1-36afb095f425\",\"type\":\"LEAVE\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":null,\"memberId\":null,\"challengeId\":\"103\",\"profileImageUrl\":null}"
 9) "{\"messageId\":\"d25b6ea6-4a48-49c3-b32b-8f075afd7053\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-04T22:53:17\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
10) "{\"messageId\":\"20db0d71-0b48-4d90-a220-90e7e42bb8de\",\"type\":\"LEAVE\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":null,\"memberId\":null,\"challengeId\":\"103\",\"profileImageUrl\":null}"
11) "{\"messageId\":\"8687eed7-09dc-40b4-93ad-9e4604d16bfd\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-04T23:02:00\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
12) "{\"messageId\":\"1d3c8227-e6e1-4f64-9f98-b827494197d4\",\"type\":\"LEAVE\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":null,\"memberId\":null,\"challengeId\":\"103\",\"profileImageUrl\":null}"
13) "{\"messageId\":\"7c279cec-64d7-4b94-89b1-563aba04b127\",\"type\":\"JOIN\",\"message\":null,\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:42\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
14) "{\"messageId\":\"c4d59d9d-5016-49a9-a66e-e265c47ef4d1\",\"type\":\"CHAT\",\"message\":\"\xec\x95\x88\xeb\x85\x95\xed\x95\x98\xec\x84\xb8\xec\x9a\x94\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:46\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
15) "{\"messageId\":\"be30f5c7-1f02-45ca-ae9e-0edf74f159da\",\"type\":\"CHAT\",\"message\":\"\xe3\x85\x87\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:47\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
16) "{\"messageId\":\"5aa9efda-e935-46c7-a87d-a28103745bb1\",\"type\":\"CHAT\",\"message\":\"\xec\x95\x84\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:48\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
17) "{\"messageId\":\"fc2d0fd1-c6d1-49bb-990f-2b3665b6b3c4\",\"type\":\"CHAT\",\"message\":\"\xec\x9d\xb8\xec\xa6\x9d\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:49\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
18) "{\"messageId\":\"b88ad8da-fa10-4f60-ab26-981a245f53af\",\"type\":\"CHAT\",\"message\":\"\xed\x95\x98\xec\x85\xa8\xec\x96\xb4\xec\x9a\x94?\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:51\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
19) "{\"messageId\":\"2e8a90e7-2cc4-4e62-8652-b5e57c010d99\",\"type\":\"CHAT\",\"message\":\"\xeb\x8c\x80\xeb\xb0\x95\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:52\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
20) "{\"messageId\":\"51b59d97-94c9-4f08-926b-8113342b554a\",\"type\":\"CHAT\",\"message\":\"\xeb\xb9\xa8\xeb\xa6\xac\xed\x95\x98\xec\x85\xa8\xeb\x84\xa4\xec\x9a\x94\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:55\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
21) "{\"messageId\":\"85413bfa-c736-485e-ae6b-ade46278b702\",\"type\":\"CHAT\",\"message\":\"\xeb\x84\xa4\xec\x97\x90~\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:47:57\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
22) "{\"messageId\":\"2cd25762-5a15-4494-8896-ec4f7a28de8f\",\"type\":\"CHAT\",\"message\":\"\xec\x88\x98\xea\xb3\xa0\xeb\xa7\x8e\xec\x9c\xbc\xec\x85\xa8\xec\x8a\xb5\xeb\x8b\x88\xeb\x8b\xa4\",\"sender\":\"\xeb\x9e\x80\xec\xb1\x84\xeb\x8b\xb9\",\"time\":\"2024-03-05T10:48:00\",\"memberId\":\"3\",\"challengeId\":\"103\",\"profileImageUrl\":\"https://eroomchallengebucket.s3.amazonaws.com/I7rMCvRnbN_\xec\x8a\xa4\xed\x81\xac\xeb\xa6\xb0\xec\x83\xb7 2024-02-24 192339.png\"}"
```

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/92032bba-204e-477b-8a47-20e748e83cdf)

- redis에는 데이터가 리스트 형식으로 이전 채팅 내역이 잘 저장돼있는 것을 확인 할 수 있다. 

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/469702b9-a024-465f-aeb0-579f73c4e259)

- 웹사이트에서도 아래 사진처럼 이전 채팅 내역 조회 시 시간 데이터가 올바른 형식으로 서버에서 클라이언트로 잘 전송이 되고 있는 것을 볼 수 있다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/6dc11663-25af-4fd0-8b3e-e5c4772ce18b)
