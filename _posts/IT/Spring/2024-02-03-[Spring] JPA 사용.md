---
title: "[Spring] JPA 사용"
author: eunchaelyu
date: 2024-02-03 07:21:00 +09:00
categories: [IT, Spring]
tags: [IT, Spring]
pin: true
img_path: '/posts/20240203'
---


## [1] JPA란?    
> Java Persistence API의 약자로 자바가 제공하는 API이다
> 스프링에서 많이 사용하고 있지만 자바 APP에서 관계형 DB를 사용하는 방식을 정의한 **인터페이스**
> 직접 SQL 문을 작성하지 않고도 JPA API를 활용해서 간단하게 DB를 저장하고 관리할 수 있다
> ORM 기술 표준으로 사용함    

  
  ORM(Object Relational Mapping) 기술 특징        
  - ORM은 객체 관계형 DB를 매핑한다      
  - 직관적인 메서드로 데이터를 조작할 수 있다    
  - Object(객체)가 데이터베이스 테이블이 되도록 만들어 준다    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/40f8929d-864c-4995-91c8-ee74f3bd72c6)    
  - JPA는 애플리케이션과 JDBC 사이에서 동작하며 API를 통해 SQL을 호출하는 방식으로 작동한다      

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/306a11a6-1015-46bd-becc-50c8b69725ef)    
  - 스프링 부트에서는 위의 설정과 같이 ``build.gradle``에서 ``spring-boot-starter-data-jpa`` 패키지를 가져와서 사용한다    

## [2] JPA 특징   
  - 1. 객체 중심 개발이 가능하다
    2. 만들어진 객체에 JPA를 활용하여 DB를 다루기 때문에 생산성이 증가한다(객체지향적인 코드 작성이 가능)
    3. 유지보수가 쉽다 
    4. JPA는 캐시 기능을 지원해주기 때문에 직접 SQL문을 작성하는 것보다 성능이 좋다
   
## [3] JPA의 사용      
  - 반복적인 CRUD SQL을 작성 & 객체를 매핑할 때 생산성을 높이기 위해서 사용한다
  - SQL 중심적인 개발 -> 객체 중심적인 개발이 가능하다     
  - JPA는 복잡한 쿼리보다 실시간 쿼리에 최적화 되어있기 때문에 다소 복잡한 작업에서는 다른 Mapper를 사용하는 것이 더 낫다


