---
title: [Java] Generic 제네릭 
author: eunchaelyu
date: 2023-12-14 12:30:00 +0900
categories: [IT, Java]
tags: [IT, Java]
pin: true
img_path: '/posts/20231214'
---



## Generic 제네릭   

### 제네릭의 효용      
- 중복되거나 필요없는 코드를 줄여주는 것      
- 타입 안정성 유지

### 제네릭 문법    

```java
// (1)
public class Generic<T> {
		// (2)
    private T t;
    // (3)
    public T get() {
        return this.t;
    }

    public void set(T t) {
        this.t = t;
    }

    public static void main(String[] args) {
				// (4)
        Generic<String> stringGeneric = new Generic<>();
        stringGeneric.set("Hello World");
				
        String tValueTurnOutWithString = stringGeneric.get();

        System.out.println(tValueTurnOutWithString);
    }
}
```

  1. 제네릭은 클래스 또는 메서드에 사용,
     클래스 이름 뒤에 <> 문법 안에 들어가야 할 타입 변수(= T) 지정
     static은 사용 못한다
     
  2. private 프로퍼티인 t의 타입
     
  3. 메서드의 리턴타입에 들어감    

  4. 제네릭을 통해 구현한 클래스를 사용(실제 변수의 값을 넣어줘야 함)
