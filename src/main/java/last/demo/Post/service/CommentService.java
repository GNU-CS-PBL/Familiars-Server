package last.demo.Post.service;

import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    
    // 부모댓글 생성
    public void savePostCommentInfo(PostCommentDto postCommentDto) {
        postCommentDto.setCreateDate(new Timestamp(System.currentTimeMillis()));
        postCommentDto.setModifyDate(new Timestamp(System.currentTimeMillis()));
        PostCommentEntity postCommentEntity = PostCommentEntity.toPostCommentEntity(postCommentDto); //DTO -> Entity 변환
        commentRepository.save(postCommentEntity);
    }
}
