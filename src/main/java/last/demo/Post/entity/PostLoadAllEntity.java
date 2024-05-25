//package last.demo.Post.entity;
//
//import jakarta.persistence.*;
//import last.demo.Post.dto.post.PostDto;
//import lombok.*;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PostLoadAllEntity {
//
//    @Id
//    @Column
//    private Long postId; // 게시물 고유번호
//
//    @Column
//    private Long roomId; //방 고유 번호
//
//    @Column
//    private Long roomMemberId; //방 구성원 고유번호
//
//    @Column
//    private Long userId; //작성자(사용자) 고유번호
//
//    @Column(columnDefinition = "varchar(50)")
//    private String title; // 게시글 제목
//
//    @Lob
//    @Column(columnDefinition = "TEXT")
//    private String content; // 게시글 내용
//
//    @ElementCollection(fetch = FetchType.EAGER)
//    @Column(columnDefinition = "LONGTEXT")
//    private List<String> postImage = new ArrayList<>(); // 게시글 이미지들을 담는 ArrayList
//
//    @Column
//    private Long totalLikeCount; // 좋아요 총 갯수
//
//    @ElementCollection(fetch = FetchType.EAGER)
//    @Column
//    private List<Long> taggedUserId  = new ArrayList<>(); // 게시글에 태그될 사용자 UID ArrayList
//
//    @Column
//    private Long totalCommentCount; // 댓글 총 갯수
//
//}
