package last.demo.Post.service;

import last.demo.Post.dto.comment.ChildCommentDto;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.comment.ChildCommentEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.repository.comment.ChildCommentRepository;
import last.demo.Post.repository.comment.CommentRepository;
import last.demo.Post.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ChildCommentRepository childCommentRepository;

    // 부모댓글 생성
    public void savePostCommentInfo(PostCommentDto postCommentDto) {
        postCommentDto.setCreateDate(new Timestamp(System.currentTimeMillis()));
        postCommentDto.setModifyDate(new Timestamp(System.currentTimeMillis()));
        PostCommentEntity postCommentEntity = PostCommentEntity.toPostCommentEntity(postCommentDto); //DTO -> Entity 변환
        commentRepository.save(postCommentEntity);

        // 게시글 postEntity에 부모댓글수(postCommentEntity)를 1 증가
        // postId를 사용해서 해당 postEntity를 조회한다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postCommentDto.getPostId());

        if(OptionalPostEntity.isPresent()) {
            PostEntity postEntity = OptionalPostEntity.get(); //optional 객체에서 Entity를 꺼낸다.
            postEntity.incrementPostCommentCount(); // postCommentCount 증가
            postRepository.save(postEntity); // 변경된 Entity를 저장한다.

        }
    }

    // 부모댓글 삭제 + 대댓글도 같이 삭제
    public void deletePostCommentInfo(Long parentCommentId) {
        Optional<PostCommentEntity> OptionalPostCommentEntity = commentRepository.findById(parentCommentId);
        if(OptionalPostCommentEntity.isPresent()) {
            PostCommentEntity postCommentEntity = OptionalPostCommentEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // #1 게시글 postEntity 의 부모댓글수 1 감소
            Optional<PostEntity> OptionalPostEntity = postRepository.findById(postCommentEntity.getPostEntity().getPostId());

            if(OptionalPostEntity.isPresent()) {
                PostEntity postEntity = OptionalPostEntity.get(); //optional 객체에서 Entity를 꺼낸다.
                postEntity.decrementPostCommentCount(); // postCommentCount 1감소
                postRepository.save(postEntity); // 변경된 Entity를 저장한다.
            }

            // #2 부모 댓글에 연결된 모든 대댓글을 가져와서 순회하면서 각 대댓글을 삭제
            List<ChildCommentEntity> childComments = postCommentEntity.getChildComments();
            for (ChildCommentEntity childComment : childComments) {
                childCommentRepository.delete(childComment);
            }

            // #3 부모댓글 Entity를 삭제한다.
            commentRepository.delete(postCommentEntity);

        }

    }
    
    
    //대댓글 생성
    public void saveChildCommentInfo(ChildCommentDto childCommentDto, Long parentCommentId) {

        // 대댓글 시간 설정
        childCommentDto.setCreateDate(new Timestamp(System.currentTimeMillis()));
        childCommentDto.setModifyDate(new Timestamp(System.currentTimeMillis()));

        // 부모 댓글 엔티티 조회
        Optional<PostCommentEntity> optionalParentCommentEntity = commentRepository.findById(parentCommentId);
        if (optionalParentCommentEntity.isPresent()) {
            PostCommentEntity parentCommentEntity = optionalParentCommentEntity.get(); // optional 객체에서 Entity를 꺼낸다.
            ChildCommentEntity childCommentEntity = ChildCommentEntity.toChildCommentEntity(childCommentDto); // DTO -> Entity 변환

            // *** 부모 댓글 설정해주는 코드 ***
            // 자식댓글의 parent_comment_id 부분에 부모 코드가 들어감
            childCommentEntity.setParentComment(parentCommentEntity);
            childCommentRepository.save(childCommentEntity); // 자식 댓글 저장

            // 부모댓글 postCommentEntity에 자식 댓글수(childCommentEntity)를 1 증가
            //Long parentCommentId = childCommentEntity.getParentComment().getParentCommentId(); // paremtCommentId를 사용해서 해당 postCommentEntity를 조회한다.
            Optional<PostCommentEntity> optionalPostCommentEntity = commentRepository.findById(parentCommentId);

            if(optionalPostCommentEntity.isPresent()) {
                PostCommentEntity postCommentEntity = optionalPostCommentEntity.get(); //optional 객체에서 Entity를 꺼낸다.
                postCommentEntity.incrementChildCommentCount(); // childCommentCount 증가
                commentRepository.save(postCommentEntity); // 변경된 Entity를 저장한다.
            }
        } else {
            // 부모 댓글이 존재하지 않을 경우의 처리 로직
            // 예외를 던지거나, 로그를 남기거나, 사용자에게 알림
            throw new RuntimeException("Parent comment not found");
        }


    }

    // 대댓글 삭제
    public void deleteChildCommentInfo(Long childCommentId) {
        Optional<ChildCommentEntity> OptionalChildCommentEntity = childCommentRepository.findById(childCommentId);
        if(OptionalChildCommentEntity.isPresent()) {
            ChildCommentEntity childCommentEntity = OptionalChildCommentEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // #1 부모댓글 postCommentEntity 의 자식댓글수 1 감소
            Optional<PostCommentEntity> OptionalPostCommentEntity = commentRepository.findById(childCommentEntity.getParentComment().getParentCommentId());

            if(OptionalPostCommentEntity.isPresent()) {
                PostCommentEntity postCommentEntity = OptionalPostCommentEntity.get(); //optional 객체에서 Entity를 꺼낸다.
                postCommentEntity.decrementChildCommentCount(); // postCommentCount 1감소
                commentRepository.save(postCommentEntity); // 변경된 Entity를 저장한다.
            }

            // #2 대댓글 Entity를 삭제한다.
            childCommentRepository.delete(childCommentEntity);
        }
    }
}
