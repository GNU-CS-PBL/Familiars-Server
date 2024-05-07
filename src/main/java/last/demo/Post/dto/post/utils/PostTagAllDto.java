package last.demo.Post.dto.post.utils;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTagAllDto {

    private Long userId; //사용자 고유번호
    private String alias; // 사용자 별칭
    private String image; // 사용자 프로필 사진


    // Entity -> DTO 변환

}
