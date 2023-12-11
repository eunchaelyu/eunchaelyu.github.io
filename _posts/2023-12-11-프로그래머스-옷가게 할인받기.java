---
title: 프로그래머스 Lv.0 옷가게 할인 받기
author: eunchaelyu
date: 2023-12-11 22:00:00 +09:00
categories: [성장 일기, 1일 1커밋, 프로그래머스]
tags: [프로그래머스, 1일 1커밋, 성장일기]
pin: true
img_path: '/posts/20231211'
---
  
  ```java
  class Solution {
    public int solution(int price) {
        if(price>=500000){
            price*=0.80;
        }else if(price>=300000){
            price*=0.90;
        }else if(price>=100000){
            price*=0.95;
        }
        return price;
    }
}
```
