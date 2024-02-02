---
title: "[Spring] 오버라이딩(Overriding)과 오버로딩(Overloading)"
author: eunchaelyu
date: 2024-02-02 11:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240202'
---

# 오버로딩과 오버라이딩  
 : 오버로딩(Overloading) - 기존에 없던 새로운 메서드를 정의 하는 것(new)
 : 오버라이딩(Overloading) - 상속받은 메서드의 내용을 변경하는 것(change, modifiy)
  
## 1. 오버로딩(Overloading)
  - 사전적 의미는 과적하다, 즉 많이 싣는 것을 뜻한다
  - 보통 하나의 메서드 이름에 하나의 기능을 구현하는데 **하나의 메서드 이름으로 여러 기능을 구현**하기 때문이다!    

### 1-1. 오버로딩의 조건    
  1) 메서드 이름이 같아야 한다  
  2) 매개변수의 개수 또는 타입이 달라야 한다      
  - return 타입은 동일하거나 다를 수 있지만, return 타입만 다를 수는 없다         
  - 오버로딩은 매개변수의 타입이나 개수가 다른 메서드를 만드는 것을 의미하기 때문이다         

### 1-2. 오버로딩의 예   
  - 가장 대표적인 **println**
```java  
void println()  
void println(boolean x)  
void println(char x)  
void println(double x)  
void println(String x)  
.  
.  
.  
```  
  - 또 다른 예시
  - 부모 클래스로부터 상속받아 자식 클래스에서 ``ParentMethod``를 추가적인 매개변수 int 값, String 값을 입력받아 사용하고 있다      
```java
class Parent {
	void ParentMethod() {
	}
class Child extends Parent{
void parentMethod(int x) {} //오버 로딩
void parentMethod(String x) {} //오버 로딩
}
```
### 1-3. 오버로딩의 장점        
  - 근본적으로 같은 기능을 하는 메서드들이 하나의 이름으로 묶어진다면    
    훨씬 기억도 하기 쉽고 짧게 줄일 수 있어서 오류도 줄일 수 있다          
  - 메서드의 이름을 하나하나 다르게 짓지 않아도 되니 절약할 수 있다!        
  
## [2] 오버라이딩(Overriding)         
  - 상위 클래스 혹은 인터페이스에 존재하는 메서드를 하위 클래스에서 필요에 맞게 재정의하는 것을 의미한다         
  - 자바의 경우는 오버라이딩 시 동적바인딩된다    

### 2-1. 오버라이딩의 조건        
  1) 이름이 같아야 한다    
  2) 매개변수가 같아야 한다      
  3) 반환타입이 같아야 한다               
  4) 자식 클래스의 메서드에서 부모 클래스의 메서드의 접근 제어자보다 더 넓은 범위의 접근 제어자를 설정할 수 있다    
  5) 부모 클래스의 메서드가 static이나 final로 선언되면 오버라이딩할 수 없다    
     
### 2-2. 오버라이딩의 예 
  - 부모 클래스로부터 상속받아 자식 클래스에서 ``ParentMethod``를 사용하고 있다     
```java
class Parent {
	void ParentMethod() {
	}
class Child extends Parent{
void parentMethod() {} //오버라이딩
}
```

### 2-3. 오버라이딩의 장점    
  - 이미 정의된 부모 클래스의 메서드를 사용하면 공통적으로 필요한 객체들을 
    또 반복문 돌리게 되는 불필요한 코드를 피할 수 있다  
  - 공통적으로 꼭 들어가야하는 상속받아서 오버라이딩 후 추가적으로 필요한 변수나 배열만 인스턴스 생성을 하면 되기 때문에
  - 코드가 훨씬 간결해지고 가독성이 좋아진다     
