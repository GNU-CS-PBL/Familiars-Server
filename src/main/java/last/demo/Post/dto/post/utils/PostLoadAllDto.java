package last.demo.Post.dto.post.utils;

import jakarta.persistence.Column;
import last.demo.Room.dto.RoomDto;
import last.demo.Room.dto.RoomMemberDto;
import last.demo.Room.entity.RoomEntity;
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
public class PostLoadAllDto { // 게시글 전체 조회 DTO (in 특정 방)


    // postEntity 에서 가져오는 요소
    private Long postId; // 게시물 고유번호
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private List<String> postImage = new ArrayList<>(); // 게시글 이미지 리스트
    private Long totalLikeCount; // 좋아요 총 갯수
    private Timestamp createDate; // 게시글 작성 시간
    private Timestamp modifyDate; // 게시글 수정 시간

    // userEntity 에서 가져오는 요소
    private String image; //사용자 프로필 사진
    private String name; // 사용자 이름

    // 댓글 Entity 에서 가져오는 요소
    private Long PostCommentCount; // 부모댓글 총 갯수

    // postTagEntity에서 가져오는 요소
    private List<Long> taggedUserId  = new ArrayList<>(); // 게시글에 태그된 사용자 UID 리스트


    // 사용자 태그를 추가하는 메소드
    public void addTaggedUserId(Long taggedUserId) {
        this.taggedUserId.add(taggedUserId);
    }



    // Entity -> Dto
//
//    public static PostLoadAllDto toPostLoadAllDto(PostLoadAllEntity postLoadAllEntity) {
//
//        if (postLoadAllEntity == null) {
//            return null;
//        }
//
//        PostLoadAllDto postLoadAllDto = new PostLoadAllDto();
//
//        postLoadAllDto.setPostId(postLoadAllEntity.getPostId());
//        postLoadAllDto.setUserId(postLoadAllEntity.getUserId() != null ? postLoadAllEntity.getUserId() : null);
//        postLoadAllDto.setTitle(postLoadAllEntity.getTitle() != null ? postLoadAllEntity.getTitle() : null);
//        postLoadAllDto.setContent(postLoadAllEntity.getContent() != null ? postLoadAllEntity.getContent() : null);
//        postLoadAllDto.setTotalCommentCount(postLoadAllEntity.getTotalCommentCount() != null ? postLoadAllEntity.getTotalCommentCount() : null);
//        postLoadAllDto.setTotalLikeCount(postLoadAllEntity.getTotalLikeCount() != null ? postLoadAllEntity.getTotalLikeCount() : null);
//
//
//        // taggedUserId 설정
//        postLoadAllDto.setTaggedUserId(postLoadAllEntity.getTaggedUserId());
//
//        // postImage 설정
//        postLoadAllDto.setPostImage(postLoadAllEntity.getPostImage());
//
//        return postLoadAllDto;
//    }
}
