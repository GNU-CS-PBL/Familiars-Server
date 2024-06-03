package last.demo.Post.service;

import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.entity.like.post.LikeEntity;
import last.demo.Post.entity.like.postComment.PostCommentLikeEntity;
import last.demo.Post.repository.comment.CommentRepository;
import last.demo.Post.repository.like.post.LikeRepository;
import last.demo.Post.repository.like.postComment.PostCommentLikeRepository;
import last.demo.Post.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;


    // [게시글] 좋아요
    public void addLikeOrCancelLikeToPost(Long roomId, Long userId, Long postId) {
        // 해당 게시글 Entity를 가져온다.
        Optional<PostEntity> optionalPostEntity = postRepository.findById(postId);

        if (optionalPostEntity.isPresent()) { // 게시글 엔티티가 존재하는 경우.
            PostEntity postEntity = optionalPostEntity.get();

            // #1. 현재 해당 게시글의 '총 좋아요 갯수'를 가져온다.
            Long currentLikeCount = Optional.ofNullable(postEntity.getTotalLikeCount()).orElse(0L);

            // #2. 해당 게시글 Id와 사용자 Id로 LikeEntity 조회
            Optional<LikeEntity> optionalLikeEntity = likeRepository.findByPostEntityPostIdAndUserId(postId, userId);

            if (optionalLikeEntity.isPresent()) {
                // 좋아요를 이미 누른 게시글이다 -> 좋아요 취소
                LikeEntity likeEntity = optionalLikeEntity.get();
                likeRepository.delete(likeEntity);

                // postEntity의 currentLikeCount를 1 감소해서 저장
                currentLikeCount -= 1;

            } else {
                // 좋아요를 누르지 않은 게시글이다 -> 좋아요 추가
                LikeEntity newLikeEntity = new LikeEntity();
                newLikeEntity.setCreateDate(new Timestamp(System.currentTimeMillis()));
                newLikeEntity.setPostEntity(postEntity); // PostEntity 설정
                newLikeEntity.setUserId(userId); // 사용자 고유번호 설정
                newLikeEntity.setRoomId(roomId); // 방 고유번호 설정
                likeRepository.save(newLikeEntity);

                // postEntity의 currentLikeCount를 1 추가해서 저장
                currentLikeCount += 1;
            }

            // 데이터 저장 및 '총 좋아요 갯수' 반환
            postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
            postRepository.save(postEntity); // 저장

        } else {
            // 게시글이 존재하지 않는 경우에 대한 처리
            throw new RuntimeException("해당 게시글이 존재하지 않습니다.");
        }
    }

    // [부모댓글] 좋아요
    public void addLikeOrCancelLikeToPostComment(Long roomId, Long userId, Long postId, Long parentCommentId) {
        // 해당 부모 댓글 Entity를 가져온다.
        Optional<PostCommentEntity> optionalPostCommentEntity = commentRepository.findById(parentCommentId);

        if (optionalPostCommentEntity.isPresent()) { // 부모 댓글 엔티티가 존재하는 경우
            PostCommentEntity postCommentEntity = optionalPostCommentEntity.get();

            // #1. 현재 해당 부모 댓글의 '총 좋아요 갯수'를 가져온다.
            Long currentLikeCount = Optional.ofNullable(postCommentEntity.getTotalLikeCount()).orElse(0L);

            // #2. 부모 댓글 Id와 사용자 Id로 PostCommentLikeEntity 조회
            Optional<PostCommentLikeEntity> optionalPostCommentLikeEntity = postCommentLikeRepository.findByPostCommentEntityParentCommentIdAndUserId(parentCommentId, userId);

            if (optionalPostCommentLikeEntity.isPresent()) {
                // 좋아요를 이미 누른 부모 댓글이다 -> 좋아요 취소
                PostCommentLikeEntity postCommentLikeEntity = optionalPostCommentLikeEntity.get();
                postCommentLikeRepository.delete(postCommentLikeEntity);

                // postCommentEntity의 currentLikeCount를 1 감소해서 저장
                currentLikeCount -= 1;

            } else {
                // 좋아요를 누르지 않은 부모 댓글이다 -> 좋아요 추가
                PostCommentLikeEntity newPostCommentLikeEntity = new PostCommentLikeEntity();
                newPostCommentLikeEntity.setCreateDate(new Timestamp(System.currentTimeMillis()));
                newPostCommentLikeEntity.setPostCommentEntity(postCommentEntity); // PostCommentEntity 설정
                newPostCommentLikeEntity.setUserId(userId); // 사용자 고유번호 설정
                newPostCommentLikeEntity.setRoomId(roomId); // 방 고유번호 설정
                newPostCommentLikeEntity.setPostId(postId); // 방 고유번호 설정
                postCommentLikeRepository.save(newPostCommentLikeEntity);

                // postCommentEntity의 currentLikeCount를 1 추가해서 저장
                currentLikeCount += 1;
            }

            // 데이터 저장 및 '총 좋아요 갯수' 반환
            postCommentEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
            commentRepository.save(postCommentEntity); // 저장

        } else {
            // 부모 댓글이 존재하지 않는 경우에 대한 처리
            throw new RuntimeException("해당 부모 댓글이 존재하지 않습니다.");
        }
    }

}


