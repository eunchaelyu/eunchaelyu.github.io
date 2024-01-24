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
  
## 🔥**Issue 1**     
뉴닉 이라는 웹사이트를 클론코딩하는 프로젝트를 FE-BE와 진행하면서 로그인/회원가입 구현 스켈레톤 코딩 후     
메인페이지 CRUD와 웹 크롤링으로 자료를 띄우는 기능을 맡았다.       
처음에 뉴닉 웹 사이트에서 웹 크롤링을 할 수 있을 줄 알고 코드를 거의 다 작성해가는데 문제가 발생했다.        
웹 크롤링이 가능한지 판단할 때 ``웹 사이트/robots.txt``를 url 검색에 쳐봐야 한다는 걸 뒤늦게 깨달았다.        
아래 사진처럼 ``https://newneek.co/robots.txt`` 이라고 검색했을 때 모든 User에 대해서 ``User-agent: *Disallow:`` 허락하지 않는다.     
즉, 웹 크롤링이 불가능하다는 것을 알 수 있다.    

![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/000d5fa7-a5a7-44b3-b94e-eec05bdce8e9)


## **해결 방법(Issue 1)**            
### 1. Jsoup 라이브러리 설치         
#### build.gradle        
``implementation 'org.jsoup:jsoup:1.14.3'``      
- Jsoup은 HTML 문서에서 데이터를 추출하고 조작하기 위한 편리한 API를 제공한다    
- CSS Selector 문법을 사용하여 특정 HTML 요소를 선택해서 필요한 정보를 가져올 수 있게 한다
- Http Request를 사용하는 라이브러리여서 정적 페이지만 파싱할 수 있다
  
### 2. url 변경해서 콘솔에 찍어본다   
크롤링이 가능한 네이버 사이트 검색창으로 url을 변경하고 **키워드** 검색 하듯이 조회하면서 뉴스 title, content, imageUrl, date, category를 불러온다 
```java
    private void scrapeNaverNews(String query) {
        String url = "https://search.naver.com/search.naver?query=" + query + "&where=news";
```
지금은 ``scrapeNaverNews("정치");`` 정치가 ``String query``로 들어가고 있으므로 정치와 연관된 뉴스 기사들이 크롤링 된다   
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
                String imageUrl = article.select("img").attr("src");
                String date = article.select("span.info").text();
                String category = article.select("a.info.press").text();

                System.out.println("Title: " + title);
                System.out.println("Content: " + content);
                System.out.println("ImageUrl: " + imageUrl);
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
콘솔에 찍었을 때 아래와 같이 나온다.  
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/699ccd03-50c2-4259-8492-6f8b372b43d3)
   


## 🔥**Issue 2**            
title을 제외하고 content, image, date, category는 원하는 값으로 올바른 형식에 따라 콘솔에 제대로 찍히지 않는다          
image는 추후 다룬다      
content, date, category(=query) 형식에 맞게 출력하려면 코드의 어떤 부분을 수정해야 할까?    


## **해결 방법(Issue 2)**        
### html 구조를 살핀다        
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/e763b983-ec57-4f37-b218-4a6af12de8d8)    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/d7077cc2-3494-496d-90bb-4a4ae0549fc7)    
- 위의 사진처럼 html 구조를 뜯어 봤을 때 news_area 중심으로 우리가 필요한 데이터 title, content, imageUrl, date가 있는 것을 볼 수 있다    
- 따라서, scrapeNaverNews 메서드 내의 url을 **div.news_area**를 기준으로 추출하는 것으로 한다    
- 그에 맞게 **selector**도 변경해서 아래와 같이 코드를 재작성 한다      
```java
        try {
            Document document = Jsoup.connect(url).get();
            Elements newsArticles = document.select("div.news_area");

            for (Element article : newsArticles) {
                String title = article.select("a.news_tit").text();
                String link = article.select("a.news_tit").attr("href");
                String content = article.select("div.news_dsc").text();
                String imageUrl = article.select("img").attr("src");
                String date = article.select("span.info").text();
                String category = query;

                System.out.println("Title: " + title);
                System.out.println("Content: " + content);
                System.out.println("Image: " + imageUrl);
                System.out.println("Date: " + date);
                System.out.println("Category: " + category);
                System.out.println("Link: " + link + "\n");
            }
```    
- category는 현재 query 와 동일하기 때문에 변경한다         
- class명 앞에는 ``.``을 붙여서 사용한다    

**결과**    
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/13b37db4-65c9-4b16-9ac4-38848ebf5d2b)    

**Application.java**에 있던 파일을 Controller, Service, Application으로 분리한 작업
```java
@SpringBootApplication
public class CloneCodingUnicornApplication implements CommandLineRunner{
    private PostService postService;
    public CloneCodingUnicornApplication(PostService postService) {
        this.postService = postService;
    }
    public static void main(String[] args) {
        SpringApplication.run(CloneCodingUnicornApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        postService.scrapeNaverNews("정치");
        postService.scrapeNaverNews("경제");
        postService.scrapeNaverNews("세계");
        postService.scrapeNaverNews("테크");
        postService.scrapeNaverNews("노동");
        postService.scrapeNaverNews("환경");
        postService.scrapeNaverNews("인권");
        postService.scrapeNaverNews("사회");
        postService.scrapeNaverNews("문화");
        postService.scrapeNaverNews("라이프");

    }
}
```
```java
    @GetMapping("/scrapeNaverNews")
    public String scrapeNaverNews(@RequestParam String query) throws IOException {
        postService.scrapeNaverNews(query);
        return "Scraping in progress. Check console for details.";
    }
```
```java
@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void scrapeNaverNews(String query) throws IOException {
        String url = "https://search.naver.com/search.naver?query=" + query + "&where=news";
        List<Post> scrapedPosts = new ArrayList<>();

        try {
            Document document = Jsoup.connect(url).get();
            Elements newsArticles = document.select("div.news_area");

            for (Element article : newsArticles) {
                String title = article.select("a.news_tit").text();
                String contents = article.select("div.news_dsc").text();
                String imageUrl = article.select("div.news_contents > a.dsc_thumb > img.thumb").attr("src");
                String date = article.select("span.info").text();
                String categoryText = query;
                Category category = mapCategory(categoryText);

                Post savedPost = postRepository.save(new Post(title, contents, imageUrl, date, category));
                scrapedPosts.add(savedPost);
                List<PostResponseDto> savedPostsResponseDto = new ArrayList<>();

                for (Post scrapedPost : scrapedPosts) {
                    Post savePost = postRepository.save(scrapedPost);
                    savedPostsResponseDto.add(new PostResponseDto(savePost));

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Category mapCategory(String categoryText) {
        for (Category category : Category.values()) {
            if (categoryText.equals(category.name())) {
                return category;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 카테고리 입니다." + categoryText);
    }
}
```


## 🔥**Issue 3**                   
1. 네이버 검색창에서 카테고리를 입력했을 때 나오는 뉴스 리스트 중 사진을 url로 받아올 때 
  imageUrl 데이터 형식이 GIF 이미지의 실제 Base64 인코딩 바이너리 데이터로 나온다          
2. 아래 사진처럼 인코딩된 이미지 데이터가 다 동일한 데이터로 들어옴
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/f85a0e9e-8e41-46d1-a71b-375ec457619a)

## **해결 방법(Issue 3)**     
- 네이버 뉴스 검색창에서 검색한 category로 뉴스 리스트로 받게 되면 각 뉴스마다 태그값이 다르기 때문에
- 태그값이 동일한 한 메인페이지 내에서 웹크롤링 사용 % /robots.txt 주소창에 검색했을 떄 허용되는 사이트 사용해서 해결한다

[서울경제](https://www.sedaily.com/)
![image](https://github.com/eunchaelyu/eunchaelyu.github.io/assets/119996957/ff7a21c6-3aa9-43d6-b4a8-57ba9ebf4527)
- 서울 경제 페이지에서 모든 User에 대해 Allow 돼있는 것을 알 수 있다
- 따라서, 웹 크롤링 url과 태그를 바꿔서 재작성한다
