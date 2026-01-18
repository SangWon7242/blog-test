package com.backend.domain.post.post.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;
import com.backend.domain.post.post.service.PostService;
import com.backend.domain.post.post.dto.Todo;
import com.backend.domain.post.post.dto.ScrapedItem;

import lombok.RequiredArgsConstructor;
import com.backend.domain.post.post.entity.Post;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;
  private final RestClient restClient;

  @GetMapping("/list")
  public String getPosts(Model model) {
    List<Post> posts = postService.findAll();
    model.addAttribute("posts", posts);

    return "posts/list";
  }

  @GetMapping("/{id}")
  public String getPost(@PathVariable long id, Model model) {
    Post post = postService.findById(id);
    model.addAttribute("post", post);

    return "posts/detail";
  }

  @GetMapping("/list/todos")
  public String getTodos(Model model) {
    String apiUrl = "https://jsonplaceholder.typicode.com/todos";
    Todo[] todosArray = restClient.get()
        .uri(apiUrl)
        .retrieve()
        .body(Todo[].class);
    List<Todo> todos = Arrays.asList(todosArray);

    model.addAttribute("todos", todos);

    return "posts/todos";
  }

  @GetMapping("/scrape")
  public String getScrapedData(Model model) {
    // 네이버 IT/과학 뉴스 섹션 페이지 URL (예시)
    String url = "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=105";
    List<ScrapedItem> scrapedItems = new ArrayList<>();

    try {
      Document doc = Jsoup.connect(url)
          .timeout(5000) // 5초 타임아웃 설정
          .get();

      // 각 뉴스 기사 항목을 포함하는 .sa_text 클래스를 선택
      Elements newsArticles = doc.select(".sa_text");

      for (Element article : newsArticles) {
        String link = "";
        String title = "";
        String summary = "";
        String press = "";
        String datetime = "";

        // 1. .sa_text_title 에 있는 href 속성에 링크
        Element titleLinkElement = article.selectFirst(".sa_text_title");
        if (titleLinkElement != null) {
          link = titleLinkElement.attr("href");
        }

        // 2. sa_text_strong 에 있는 뉴스 타이틀
        Element titleStrongElement = article.selectFirst(".sa_text_strong");
        if (titleStrongElement != null) {
          title = titleStrongElement.text();
        }

        // 3. sa_text_lede 에 있는 요약된 뉴스 기사
        Element summaryElement = article.selectFirst(".sa_text_lede");
        if (summaryElement != null) {
          summary = summaryElement.text();
        }

        // 4. sa_text_press 에 있는 신문사
        Element pressElement = article.selectFirst(".sa_text_press");
        if (pressElement != null) {
          press = pressElement.text();
        }

        // 5. .sa_text_datetime에 있는 시간
        Element datetimeElement = article.selectFirst(".sa_text_datetime");
        if (datetimeElement != null) {
          datetime = datetimeElement.text();
        }

        // 모든 정보가 유효할 경우에만 추가
        if (!link.isEmpty() && !title.isEmpty()) { // 최소한 링크와 타이틀은 있어야 유효하다고 판단
          scrapedItems.add(new ScrapedItem(title, link, summary, press, datetime));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      model.addAttribute("errorMessage", "웹 페이지를 스크래핑하는 데 실패했습니다: " + e.getMessage());
    }

//    System.out.println(scrapedItems);

    model.addAttribute("scrapedItems", scrapedItems);
    return "posts/scrape";
  }
}
