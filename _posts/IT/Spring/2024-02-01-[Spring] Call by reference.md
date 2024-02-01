---
title: "[Spring] Call by reference란?"
author: eunchaelyu
date: 2024-2-1 09:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240201'
---


## [1] Call by reference란 무엇인가?    
  : 참조에 의한 호출    
  - call by reference 방식은 함수 호출 시 **변수의 메모리 주소**를 전달한다            
    따라서 함수 내에서 변수 값이 변경되면, 아규먼트로 전달된 객체의 값도 변경된다    

## [2] Call by reference 쓰는 방법     
  - 아래와 같이 MyNumber라는 클래스 객체를 정의한다      
  - getter: 객체의 상태(값)를 읽는데 사용 > 현재 객체의 값을 반환          
  - setter: 객체의 상태(값)를 변경하기 위한 메서드 > 수정되거나 새로운 값으로 갱신된 값을 반환
    
```java
class MyNumber {
    private int value;
    public MyNumber(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
```

  - Inner 클래스로 어규먼트로 전달된 값을 "20"으로 변경해주는 **modifyNumber**메서드를 만든다    
  - 메모리 주소를 복사해서 전달한다     
```java
    public static void modifyNumber(MyNumber num) {
        num.setValue(20);
    }
```

  - 처음에 정의해둔 MyNumber 객체를 생성하고 초기값을 "10"으로 설정한다    
  - modifyNumber 함수가 호출되기 전과 후를 출력한다          
  - myNumber의 메모리 주소값이 변경되어 "10" -> "20" 으로 갱신된 값을 반환하는 것을 볼 수 있다    
```java
public class CallByReference {
    public static void main(String[] args) {
        MyNumber myNumber = new MyNumber(10);

        System.out.println("함수 호출 전: " + myNumber.getValue() );
        modifyNumber(myNumber);
        System.out.println("함수 호출 후: " + myNumber.getValue() );
    }
```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/bf0ed9ec-4271-4a59-b02d-b39d0eb99da1)

  - getter를 쓰지 않았을 경우 변수의 **메모리 주소**가 반환되는 것을 볼 수 있다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/dac1c53b-17fc-4a7f-8896-6294b5aa6021)    


### Call by value 방식
  - 값에 의한 호출(Call by value) 방식은 함수 호출 후 변수값이 변경돼도 외부 변수 값은 변경되지 않는다    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/4f09071e-1465-4ab3-a75b-79dbf2841271)    



