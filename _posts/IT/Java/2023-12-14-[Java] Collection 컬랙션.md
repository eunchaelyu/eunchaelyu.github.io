---
title: [Java] Collection 컬랙션
author: eunchaelyu
date: 2023-12-14 14:30:00 +0900
categories: [IT, Java]
tags: [IT, Java]
pin: true
img_path: '/posts/20231214'
---

## Collection 컬랙션
 - 자료구조를 구체화해서 java에 놓여놓은 것

### List 배열
> 추상적 자료구조, 순서가 있고 중복을 허용함 (Set 과 구별됨)    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/416787dc-3d99-4d6f-9ed6-747bc445964a)

### List<E> 코드        

```java
// 실제 java.util의 List 코드
public interface List<E> extends Collection<E> { // 제네릭 인터페이스 
		int size();
    boolean isEmpty();
		...
		boolean addAll(Collection<? extends E> c);
		boolean add(E e);
		...
}      
```
- 타입변수 E    
- addAll()메서드의 인자에 조건이 두개 들어있다.    
    1. Collection 타입에 속하는 것    
    2. List의 타입변수 E의 자손 클래스를 원소로 가지고 있을 것    


