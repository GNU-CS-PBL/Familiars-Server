package last.demo.Post.dto.post.utils;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLoadAllDto {

    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private List<String> postImage = new ArrayList<>(); // 게시글 이미지들을 담는 ArrayList
}
