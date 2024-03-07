---
title: "[Spring] Redis에 TTL(Time-To-Live) 설정"
author: eunchaelyu
date: 2024-03-03 14:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240303'
---

    
# Redis에 TTL(Time-To-Live) 설정    

- Redis에 이전 채팅 내역을 저장하다 보면 점점 쌓이는 데이터가 많고 그대로 두면 CPU가 넘친다    
- 프론트단이랑 30일을 채팅 메시지 보관 기간으로 설정했기 때문에     
- 기간이 지난 내역은 서버에서 삭제 되도록 구현해야 한다    

- 이전에는 수동으로 Redis 저장된 정보를 클러스터 단위로 비워줬다    
- 물론 Key별로 비우는 것도 가능! (현 프로젝트에서는 챌린지 방 id 값들이 key값들로 설정 돼있음        

- redis-eroomchallenge-001 데이터베이스를 비우기    

```java
FLUSHDB
```

- ChatRoomRepository에서 Redis에 직접 채팅 내역을 저장할 때 TTL도 같이 설정하자        


## ChatRoomRepository 에 코드 추가    

```java
@Slf4j
@Repository
@Component
public class ChatRoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ListOperations<String, Object> listOperations;

    private static final String CHAT_ROOM_PREFIX = "chat_room:";

    /**
     * ChatRoomRepository의 생성자
     *
     * @param redisTemplate Redis 연동을 위한 RedisTemplate 객체
     */
    public ChatRoomRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
    }

    /**
     * challengeId에 해당하는 채팅방의 채팅 내역을 불러오는 메서드
     *
     * @param challengeId 채팅 내역을 가져올 채팅방의 고유 식별자
     * @return 채팅 내역을 담은 리스트
     */
    public List<Object> getChatHistory(String challengeId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        return listOperations.range(key, 0, -1);
    }

    /**
     * challengeId에 해당하는 채팅방에 채팅 메시지를 저장하는 메서드
     *
     * @param challengeId 채팅 메시지를 저장할 채팅방의 고유 식별자
     * @param chatMessage 저장할 채팅 메시지 객체
     */
    public void saveChatMessage(String challengeId, ChatMessage chatMessage) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        listOperations.rightPush(key, chatMessage);

        // 키의 TTL을 30일로 설정
        redisTemplate.expire(key, Duration.ofDays(30));
    }
```

- TTL(Time-To-Live)은 데이터가 Redis에 저장된 이후에 유지되는 시간을 나타낸다    
- 만료되면 Redis는 해당 데이터를 삭제한다    

- `ChatRoomRepository`**에서는 Redis에 직접 채팅 내역을 저장하고 있으며    
- Redis에 저장된 데이터의 TTL을 30일로 설정하는 것이고 이를 통해 Redis는 해당 데이터를 30일 동안 유지한 후에 자동으로 삭제한다

