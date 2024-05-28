package last.demo.Post.entity.like.post;

import jakarta.persistence.*;
import last.demo.Post.entity.PostEntity;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEntity { // 게시글 1개당 여러개의 LikeEntity가 존재

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId; // 좋아요 고유번호

    @Column
    private Long roomId; //방 고유 번호

    @Column
    private Long userId; // 사용자 고유번호

    @Column
    private Timestamp createDate; // 좋아요 생성 시간

    // PostEntity와의 양방향 관계 설정 ->  // 좋아요 누른 게시글 고유 번호 목록
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id") // PostEntity의 postId와 매핑
    private PostEntity postEntity; // PostEntity 를 참조하는 필드

}
