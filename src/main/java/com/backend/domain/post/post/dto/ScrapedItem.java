package com.backend.domain.post.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedItem {
    private String title;   // 뉴스 타이틀 (sa_text_strong)
    private String url;     // 링크 (sa_text_title의 href)
    private String summary; // 요약된 뉴스 기사 (sa_text_lede)
    private String press;   // 신문사 (sa_text_press)
    private String datetime; // 시간 (sa_text_datetime)
}
