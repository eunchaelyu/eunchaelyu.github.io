---
title: "[WebDev] JavaScript & jQuery"
author: eunchaelyu
date: 2023-12-01 18:24:00 +09:00
categories: [IT, WebDev]
tags: [IT, WebDev]
pin: true
img_path: '/posts/20231201'
---

# JavaScript & jQuery

##  JavaScript & jQuery(미리 완성된 JavaScript 코드)
> 코드가 복잡하고 브라우저 간 호환성 문제도 고려해야 해서 jQuery라는 라이브러리가 생김\
쓰기 전에 import 해야 함.


jQuery
: jQuery CDN 부분을 참고해서 임포트하기( ``<head>``안에 넣음)

```html
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
</head>
```

: ``<div>``에 id를 넣어줘서 html 을 움직이게 만든다
```html
<body>
  <div id="아이디값">
  테스트
  </div>

  <script>
    function 클래스명() {
	    let word = '변수'
	    $('#아이디값').text(a)
    }
  </script>
</body>
```
:  temp_html에 forEach문으로 돌린 반복문의 데이터가 들어온다\
아이디값에 위의 내용이 더해짐
```html
<body>
  <div id="아이디값">
  테스트
  </div>

  <script>
        function 클래스명() {
            let fruits = ['변수1','변수2']
            
            $('#아이디값').empty()
            // 기존에 내용들이 지워짐
            fruits.forEach(element => {
                let temp_html = `<p>${element}</p>`
                $('#아이디값').append(temp_html)
                // '아이디값'에 temp_html 더해진다
            });
  </script>
```

시간 기능 구현하기
: 

서버-클라이언트 통신
: 



