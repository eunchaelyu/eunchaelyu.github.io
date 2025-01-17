---
title: "[WebDev] Form & 클라우드 서비스"
author: eunchaelyu
date: 2023-12-04 19:30:00 +09:00
categories: [IT, WebDev]
tags: [IT, WebDev]
pin: true
img_path: '/posts/20231204'
---


### Form
> 사용자가 입력한 데이터를 서버로 보낼 수 있다
> 처리할 곳을 ``url_for('데이터를 보낼 곳')``으로 적고 방식을 GET과 POST 중에 사용한다

```bash
<form action="{{ url_for('music_create') }}" method="GET">
  <div class="mb-3">
```
> GET(검색,조회), POST(로그인 등 정보 수정, 중요한 정보 저장)


### Form에서 보낸 데이터를 Flask에서 받기
> music_create()라고 정했기 때문에 def 다음에 music_create()라고 적어줘야 한다\
> ``form``태그에에서 name 속성으로 정해준 데이터 이름을 사용해서 ``request.args.get("데이터이름")``으로 가져올 수도 있다

```bash
@app.route('/music/create')
def music_create()
   # form으로 데이터 입력 받기
   username_receive = request.args.get("username")

    # 데이터를 DB에 저장하기
    song = Song(username=username_receive)
    db.session.add(song)
    db.session.commit()
```

### 필수 라이브러리(flask)

```python
0. Flask : 웹서버를 시작할 수 있는 기능. app이라는 이름으로 플라스크를 시작한다
1. render_template : html파일을 가져와서 보여준다
'''
from flask import Flask, render_template, request, redirect, url_for
app = Flask(__name__)
```

### DB 기본코드

```python
import os
from flask_sqlalchemy import SQLAlchemy

basedir = os.path.abspath(os.path.dirname(__file__))
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] =\
        'sqlite:///' + os.path.join(basedir, 'database.db')

db = SQLAlchemy(app)

class Song(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(100), nullable=False)

    def __repr__(self):
        return f'{self.username}'

with app.app_context():
    db.create_all()
```


### 클라우드 서비스

> Pythonanywhere\
> 컴퓨터를 빌리는 것
<https://www.pythonanywhere.com>
  계정을 만들고 무료로 배포할 수 있도록 도와준다

###og 태그 

## 5주차 feedback
데이터베이스에서 특정한 데이터를 수정하고 삭제하는 기능이 너무 어려웠는데 차분히 라우팅 링크 주소나 session 코드 오류를 고쳐가다보면 해결이 됐다. 
