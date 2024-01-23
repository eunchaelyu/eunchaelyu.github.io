---
title: "[TroubleShooting] Jsoup 라이브러리를 이용한 웹 크롤링"
author: eunchaelyu
date: 2024-1-16 9:49:00 +09:00
categories: [IT, TroubleShooting]
tags: [IT, TroubleShooting]
pin: true
img_path: '/posts/20240122'
---


#   Jsoup 라이브러리를 이용한 웹 크롤링    
  
## **Issue 1**     
뉴닉 이라는 웹사이트를 클론코딩하는 프로젝트를 FE-BE와 진행하면서 로그인/회원가입 구현 스켈레톤 코딩 후     
메인페이지 CRUD와 웹 크롤링으로 자료를 띄우는 기능을 맡았다.       
처음에 뉴닉 웹 사이트에서 웹 크롤링을 할 수 있을 줄 알고 코드를 거의 다 작성해가는데 문제가 발생했다.        
웹 크롤링이 가능한지 판단할 때 ``웹 사이트/robots.txt``를 url 검색에 쳐봐야 한다는 걸 뒤늦게 깨달았다.        
아래 사진처럼 ``https://newneek.co/robots.txt`` 이라고 검색했을 때 모든 User에 대해서 ``User-agent: *Disallow:`` 허락하지 않는다.     
즉, 웹 크롤링이 불가능하다는 것을 알 수 있다.    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/000d5fa7-a5a7-44b3-b94e-eec05bdce8e9)


## **해결 방법(Issue 1)**            
## 1. Jsoup 라이브러리 설치         
### build.gradle        
``implementation 'org.jsoup:jsoup:1.14.3'``      
- Jsoup은 HTML 문서에서 데이터를 추출하고 조작하기 위한 편리한 API를 제공한다    
- CSS Selector 문법을 사용하여 특정 HTML 요소를 선택해서 필요한 정보를 가져올 수 있게 한다
- Http Request를 사용하는 라이브러리여서 정적 페이지만 파싱할 수 있다
  
## 2. url 변경해서 콘솔에 찍어본다   
크롤링이 가능한 네이버 사이트 검색창으로 url을 변경하고 **키워드** 검색 하듯이 조회하면서 뉴스 title, content, imageUrl, date, category를 불러온다 
```java
    private void scrapeNaverNews(String query) {
        String url = "https://search.naver.com/search.naver?query=" + query + "&where=news";
```
지금은 
``scrapeNaverNews("정치");`` 정치가 ``String query``로 들어가고 있으므로 정치와 연관된 뉴스 기사들이 크롤링 된다   
```java
@SpringBootApplication
public class CloneCodingUnicornApplication implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication.run(CloneCodingUnicornApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        scrapeNaverNews("정치");
    }
    private void scrapeNaverNews(String query) {
        String url = "https://search.naver.com/search.naver?query=" + query + "&where=news";

        try {
            Document document = Jsoup.connect(url).get();
            Elements newsArticles = document.select("div.news_area");
            
            for (Element article : newsArticles) {
                String title = article.select("a.news_tit").text();
                String link = article.select("a.news_tit").attr("href");
                String content = article.select("div.articleBody").text();
                String image = article.select("img").attr("src");
                String date = article.select("span.info").text();
                String category = article.select("a.info.press").text();

                System.out.println("Title: " + title);
                System.out.println("Content: " + content);
                System.out.println("Image: " + image);
                System.out.println("Date: " + date);
                System.out.println("Category: " + category);
                System.out.println("Link: " + link + "\n");
            }
        } catch (IOException | java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
```
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/eec2338c-794f-40b7-b9ee-7e49eec85b1f)
콘솔에 찍었을 때 위의 사진과 같이 나온다. 


## **Issue 2**        
title을 제외하고 content, image, date, category는 원하는 값으로 올바른 형식에 따라 콘솔에 제대로 찍히지 않는다    
특이한 점은 image 가 GIF 이미지의 실제 Base64 인코딩 바이너리 데이터로 나온다

## **해결 방법(Issue 2)**    


        



