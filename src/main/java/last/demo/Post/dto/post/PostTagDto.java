package last.demo.Post.dto.post;

import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTagDto {

    private Long postTagId; // 게시물 태그 고유번호
    private Long postId; // 게시물 고유번호
    private Long roomId; // 방 고유번호
    private Long userId; // 태그를 하는 사용자 UID
    private Long taggedUserId; // 태그될 사용자 UID
    private Timestamp createDate; // 게시물 태그 생성시간
    private Timestamp modifyDate; // 게시물 태그 수정시간
}
