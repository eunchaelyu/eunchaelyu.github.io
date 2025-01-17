---
title: "[WebDev] HTML,CSS(부트스트랩 사용)"
author: eunchaelyu
date: 2023-12-01 17:49:00 +09:00
categories: [IT, WebDev]
tags: [IT, WebDev]
pin: true
img_path: '/posts/20231201'
---


## HTML은 뼈대, CSS는 꾸미기!

 HTML
: ``<head>`` 태그와 ``<body>``태그로 구성된다 ( ! + Enter 키를 누르면 자동 완성됨)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    
</body>
</html>
```

CSS
: ``<head>`` 태그 속 ``<style>`` 태그로 공간을 만들어 작성한다
> 배경관련(background-color, background-image, background-size)
> 사이즈(width, height)
> 폰트(font-size, font-weight, font-family, color)
> 간격(margin, padding)
> 배치(display)

```css
  <style>
	  .mytitle {
      color: red;
        font-size: 40px;
     }
  </style>
```
> 'mytitle'이라는 클래스의 속성을 설정하고 싶다면\
> .mytitle {} 의 구조로 공간을 만든다

Flex
: block(1줄 모두 차지, 위에서 아래로 쌓임) 태그와 inline(가로로 배치) 태그가 있다.
```html
    <style>
        .container {
            display: flex; 
            justify-content: center; 
            /* justify-content는 주축 방향으로 정렬가능(가로 가운데) */
            align-items: center; 
            /* align-items는 주축에서 90도 방향으로 정렬가능(세로 가운데) */
        }
    </style>
```
- [x] 개발자 도구 에서 display 옆에 아이콘 클릭하면 여러 배치들 볼 수 있음

PageStructure
: 네비게이션 바 (Navbar)\
  -홈페이지 상단에 위치, 메뉴 항목을 통해 다른 페이지로 연결
: 본문 (Main)\
  -주요 컨텐츠 포함, 다양한 형태의 정보
: 푸터 (Footer)\
  -홈페이지 하단에 위치, 부가적인 링크와 추가 정보

Font
: 구글 폰트 중 Poppins Regular 400을 사용하고 싶다면 ``<style>``안에 해당 폰트 import를 복사해와서 붙여준다\
CSS rules to specify families 탭의 코드를 복사해서 전체 적용 ``*{}`` style에 넣으면 완성!

```css
<style>
  @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@500&display=swap');

 * {
    font-family: "Poppins", "sans-serif";
  }
</style>
```
:

Bootstrap
: 미리 완성된 CSS 모음집\
[부트스트랩 주소]  https://getbootstrap.com/docs/5.3/getting-started/introduction/

1. Components(구성 요소)
: Cards
: Navbar
: Button
2. Utilities(유틸리티)
: Flex
: Spacing
: Text
: Position
3. Forms(양식)
: Input group 
: 필요한 내용을 search 해서 원하는 위치에 붙여 사용




