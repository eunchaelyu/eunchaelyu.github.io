---
title: "[WebDev] Python & Flask 서버"  
author: eunchaelyu
date: 2023-12-02 16:30 +09:00
categories: [IT, WebDev]
tags: [IT, WebDev]
pin: true
img_path: '/posts/20231202'
---


##  Python
> 프로그래밍 언어

## IF
> 조건문(조건이 참(True), 거짓(False) 중 하나를 만족할 때 실행되는 파이썬 문법)

```python
if 조건1:
    # 조건1이 참일 때 실행할 코드
elif 조건2:
    # 조건1이 거짓이고 조건2가 참일 때 실행할 코드
elif 조건3:
    # 조건1과 조건2가 모두 거짓이고 조건3이 참일 때 실행할 코드
else:
    # 위의 모든 조건이 거짓일 때 실행할 코드
```

## FOR
> 반복문(요소의 리스트를 반복하는 파이썬 문법)

```python
a = [1, 2, 3, 4]
# a라는 숫자 값이 담긴 리스트

for fruit in a:
	print(a)
# a라는 리스트의 요소를 돌아가면서 순서대로 반복
```

### FUNCTION
> 함수(같은 행위를 반복하게 하는 것)

```python
# 파이썬으로 평균 구하는 함수
def average(numbers):
    total = 0
    for number in numbers:
        total += number
    return total / len(numbers)

numbers = [1, 2, 3, 4, 5]
print(average(numbers)) 
```

## virtual environment
> 가상환경(파이썬 배포 패키지들을 설치하거나 업그레이드하는 것을 가능하게 함)

1. 명령팔레트 열기(env 검색)
2. Python : Create Environment(환경 만들기)
3. Venv 클릭하면 설치됨. 

## packages
> 패키지는 모듈(일종의 기능들 묶음)을 모아 놓은 단위입니다

  패키지를 설치할 떄는 PIP 약자를 사용함 ``pip install (패키지명)``

## wep scraping 
> 웹 스크랩핑(인터넷에서 정보를 가져오는 작업)\
> API가 없더라도 내가 원하는 페이지의 데이터를 가져올 수 있다

```python
// 선택자를 사용하는 방법 (copy selector)
soup.select('태그명')
soup.select('.클래스명')
soup.select('#아이디명')

soup.select('상위태그명 > 하위태그명 > 하위태그명')
soup.select('상위태그명.클래스명 > 하위태그명.클래스명')

// 태그와 속성값으로 찾는 방법
soup.select('태그명[속성="값"]')
// 한 개만 가져오고 싶은 경우
soup.select_one('위와 동일')
```

   ``.contents``로 내부의 데이터를 전부 리스트에 값으로 담을 수 있다

```python
soup.select('.클래스명').contents

soup.select('.클래스명').contents[1]
# 리스트로 나열된 것 중에 1번째 데이터 들고 오기
```

   ``.text``로 각각 지정된 ``<tr>``태그 안에서 데이터를 찾아낼 수 있다.

```python
tr = soup.select_one('.클래스명')
찾는 데이터 클래스명 = tr.select_one('.찾는 데이터 클래스명').text
```

## Python의 Flask 서버
> 웹이 동작하는 서버

- [x] 로컬 개발환경: 컴퓨터 한 대로 서버와 요청도 만든다\
클라이언트 = 서버


  flask 폴더 구조

```text
flask
|— venv
|— app.py (서버)
|— templates
         |— index.html (클라이언트 파일)
```

Flask 패키지 설치
> 터미널에 ``pip install flask`` 누르고 Enter



  Flask 기초 코드(주소,요청에 따라 응답을 다르게 해주는 서버를 만듦)

```python
from flask import Flask
app = Flask(__name__)

@app.route('/')
def home():
   return 'This is Home!'

@app.route('/mypage')
def mypage():  
   return 'This is My Page!'

if __name__ == '__main__':  
   app.run(debug=True)
```

  ``index.html``에서 ``context``라는 딕셔너리에 원하는 데이터를 넣는다\
key 에는 HTML에서 사용할 이름을, value에는 Python 변수를 넣는다

```bash
context = {
	"HTML에서 사용할 이름1": 변수명1,
	"HTML에서 사용할 이름2": 변수명2,
}
return render_template('index.html',  data=context)
```

  반복문 사용(값을 리스트 그대로가 아닌 하나씩 출력)

```
<body>
    {% for number in data.변수1 %}
            {{ number }}
    {% endfor %}
</body>
```

  ``<ol>``태그 사용하면 숫자나 알파벳 같은 마커가 붙는다(ordered list)

```
<body>
  <ol>
    {% for number in data.변수1 %}
    <li>{{ number }}</li>
    {% endfor %}
  </ol>
</body>
```


## 3주차 feedback
: 여러가지 함수들을 사용해서 활용하는 법을 연습해야 한다


