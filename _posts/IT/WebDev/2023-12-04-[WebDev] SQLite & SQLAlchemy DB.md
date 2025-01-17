---
title: "[WebDev] SQLite & SQLAlchemy DB"
author: eunchaelyu
date: 2023-12-04 17:30:00 +09:00
categories: [IT, WebDev]
tags: [IT, WebDev]
pin: true
img_path: '/posts/20231204'
---


### 라우팅 연습
> 주소에 따라 html 파일이 보여질 수 있도록 처리함.

```python
from flask import Flask, render_template
app = Flask(__name__)

@app.route("/")
def home():
    return render_template("home.html")

@app.route("/page/")
def music():
    return render_template("page.html")

if __name__ == "__main__":
    app.run(debug=True, port=8080)
```

### 데이터 넘겨주기
> 웹 사이트에서 데이터가 필요한 경우, Python으로 크롤링하거나 Database에서 필요한 데이터를 만들어서 넘긴다

```python
@app.route("/")
def home():
    name = "홍길동"
    goal = "할 수 있다"

    context = {
        "name": name,
        "goal": goal
    return render_template("home.html", data=context)
```

  ``render_template`` 에서 ``HTML에서 쓸 이름 = Python변수`` 형식으로 적어야 한다\
  여러 데이터를 넘겨줄 때는 name과 goal변수를 만들고 context 딕셔너리에 넣는다

```html
<div class="container">
    <div class="greeting">
      <h1>Hello, {{ data.name }}</h1> //"안녕, 홍길동"
      <h1 id="current-time"></h1> //현재 시간
    </div>

    <div class="todo">
      <h3>My life's goal</h3>
      <h2>{{ data.goal }}</h2>
    </div>
  </div>
```
  python에서 넘겨준 data에 따라 html에서 화면에 출력하는 코드\
  home.html에서 data 딕셔너리에 담긴 name을 사용하므로 ``{{ data.name }}``형식으로 써야한다

### 주소에 따라 메인화면 바꾸기
> ``route()`` 부분에 ``<name>`` 와 같이 ``<>`` 를 사용하면, URL 주소에 따라서 ``name``을 변수처럼 쓸 수 있다.

```python
@app.route("/profile/<name>/")
def profile(name):

    motto = f"{name}의 프로필입니다"
    context = {
        "name": name,
        "goal": goal
    }

    return render_template("home.html", data=context)
```

### 페이지 이동기능
> ``navbar`` 태그 안에 ``a`` 태그의 ``href`` 속성을 ``{{ url_for('이동할 곳') }}`` 형식으로 바꾸면 해당 메뉴를 클릭했을 때 html로 이동하게 된다


### RDBMS(SQL)
> 행/열의 생김새가 정해진, 정형화 된 데이터 저장 방식
> 데이터의 일관성이 있고 분석에 용이함
  ex) SQLite, MS-SQL, My-SQL 등

### No-SQL
딕셔너리 형태로 데이터를 저장해두는 Database(데이터마다의 값을 가지지 않게 됨)
  ex) MongoDB


### SQLite
> Python에는 기본적으로 SQLite가 내장되어 있으므로 별도 설치 없이 VScode에서 파일이름.db 형식으로 손쉽게 사용할 수 있다

1. SQLAlchemy로 Database 연결하기
2. 폴더 생성, 가상환경 설치
3. ``pip install Flask-SQLAlchemy``
4. ``app.py`` 에 Flask와 Database를 연결하는 코드를 작성
5. app.py에 데이터베이스 모델을 정의

```python
class Song(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String, nullable=False)
    artist = db.Column(db.String, nullable=False)
    title = db.Column(db.String, nullable=False)
    image_url = db.Column(db.String, nullable=False)

    def __repr__(self):
        return f'{self.artist} {self.title} 추천 by {self.username}'

with app.app_context():
    db.create_all()
```

6. ``$ flask shell (엔터)``
7. 테이블 만들기

```bash
>>> from app import db, Song (엔터)
>>> db.create_all()
```

### SQLAlchemy DB 조작 요약

```shell
# 데이터를 DB에 저장하기
song = Song(username="추천자", title="노래제목", 
            artist="가수", image_url="이미지 주소")
db.session.add(song)
db.session.commit()

# 모든 데이터 조회하기
song_list = Song.query.all()

# 데이터 1개 가져오기
Song.query.filter_by(id=3).first()

# 데이터 변경하기 
song_data = Song.query.filter_by(id=4).first()
song_data.title = '변경된제목'
db.session.add(song_data)
db.session.commit()

# 데이터 삭제하기
delete_data = Song.query.filter_by(id=4).first()
db.session.delete(delete_data)
db.session.commit()
```

## 4주차 feedback
: URL에서 데이터를 들고와서 DB에 데이터를 저장하고 조작하는 방식을 배울 수 있었다\
아직은 행과 열이 정해져 있는 DB저장 방식만 배웠지만 자유롭게 데이터를 조작하는 것도 공부해서 비교해보고 싶다


