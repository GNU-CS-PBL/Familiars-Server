package last.demo.Post.service;

import last.demo.OAuth.entity.UserEntity;
import last.demo.OAuth.repository.UserRepository;
import last.demo.Post.dto.comment.ChildCommentDto;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.dto.comment.utils.ChildCommentLoadAllDto;
import last.demo.Post.dto.comment.utils.MyPagePostCommentLoadAllDto;
import last.demo.Post.dto.comment.utils.PostCommentLoadAllDto;
import last.demo.Post.dto.post.utils.PostLoadAllDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.PostTagEntity;
import last.demo.Post.entity.comment.ChildCommentEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.repository.comment.ChildCommentRepository;
import last.demo.Post.repository.comment.CommentRepository;
import last.demo.Post.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ChildCommentRepository childCommentRepository;
    private final UserRepository userRepository;

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

    // 특정 게시글의 부모댓글 조회
    public List<PostCommentLoadAllDto> findAllPostCommentByPostId(Long postId) {

        // #1. 특정 postId 에 해당하는 모든 postCommentEntity들을 List형태로 불러온다.
        List<PostCommentEntity> postCommentEntities = commentRepository.findAllByPostEntityPostId(postId);

        // #2. 결과를 저장할 리스트
        List<PostCommentLoadAllDto> postCommentLoadAllDtoList = new ArrayList<>();

        // #3. for문을 활용 : PostCommentEntities 내용들을 -> PostCommentLoadAllDtoList 로 옮기기
        for (PostCommentEntity postCommentEntity : postCommentEntities) {
            // #3-1 : 옮길 postCommentLoadAllDto 객체 생성
            PostCommentLoadAllDto postCommentLoadAllDto = new PostCommentLoadAllDto();

            // #3-2 : postCommentEntity안의 userId를 이용해서, userEntity에서 name과 Image를 가져와서 postCommentLoadAllDto 요소에 대입
            Optional<UserEntity> optionalUserEntity= userRepository.findById(postCommentEntity.getUserId());
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                postCommentLoadAllDto.setName(userEntity.getName());
                postCommentLoadAllDto.setImage(userEntity.getImage());
            }

            // #3-3 : postCommentEntity안의 나머지 요소들도, postCommentLoadAllDto에 대입
            postCommentLoadAllDto.setContent(postCommentEntity.getContent());
            postCommentLoadAllDto.setTotalLikeCount(postCommentEntity.getTotalLikeCount());
            postCommentLoadAllDto.setCreateDate(postCommentEntity.getCreateDate());
            postCommentLoadAllDto.setParentCommentId(postCommentEntity.getParentCommentId());

            // #3-4 : 결과 리스트에 추가
            postCommentLoadAllDtoList.add(postCommentLoadAllDto);
        }

         //결과 리스트를 createDate 시간값으로 내림차순으로 정렬하여 반환
        postCommentLoadAllDtoList.sort(Comparator.comparing(PostCommentLoadAllDto::getCreateDate).reversed());


        // 결과 리스트를 반환
        return postCommentLoadAllDtoList;
    }


    // 특정 부모댓글의 자식댓글들 조회
    public List<ChildCommentLoadAllDto> findAllChildCommentByPostId(Long parentCommentId) {

        // #1. 특정 parentCommentId 에 해당하는 모든 childCommentEntity들을 List형태로 불러온다.
        List<ChildCommentEntity> childCommentEntities = childCommentRepository.findAllByParentCommentParentCommentId(parentCommentId);

        // #2. 결과를 저장할 리스트
        List<ChildCommentLoadAllDto> childCommentLoadAllDtoList = new ArrayList<>();

        // #3. for문을 활용 : ChildCommentEntities 내용들을 -> ChildCommentLoadAllDtoList 로 옮기기
        for (ChildCommentEntity childCommentEntity : childCommentEntities) {
            // #3-1 : 옮길 ChildCommentLoadAllDto 객체 생성
            ChildCommentLoadAllDto childCommentLoadAllDto = new ChildCommentLoadAllDto();

            // #3-2 : ChildCommentEntity안의 userId를 이용해서, userEntity에서 name과 Image를 가져와서 childCommentLoadAllDto 요소에 대입
            Optional<UserEntity> optionalUserEntity= userRepository.findById(childCommentEntity.getUserId());
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                childCommentLoadAllDto.setName(userEntity.getName());
                childCommentLoadAllDto.setImage(userEntity.getImage());
            }

            // #3-3 : childCommentEntity안의 나머지 요소들도, childCommentLoadAllDto에 대입
            childCommentLoadAllDto.setContent(childCommentEntity.getContent());
            childCommentLoadAllDto.setCreateDate(childCommentEntity.getCreateDate());
            childCommentLoadAllDto.setChildCommentId(childCommentEntity.getChildCommentId());

            // #3-4 : 결과 리스트에 추가
            childCommentLoadAllDtoList.add(childCommentLoadAllDto);
        }

        //결과 리스트를 createDate 시간값으로 내림차순으로 정렬하여 반환
        childCommentLoadAllDtoList.sort(Comparator.comparing(ChildCommentLoadAllDto::getCreateDate).reversed());


        // 결과 리스트를 반환
        return childCommentLoadAllDtoList;
    }




    // 내가 쓴 (부모)댓글 전체 조회(in 마이페이지)
    public List<MyPagePostCommentLoadAllDto> findAllPostCommentByUserId(Long userId) {

        // #1. 특정 parentCommentId 에 해당하는 모든 postCommentEntity들을 List형태로 불러온다.
        List<PostCommentEntity> postCommentEntities = commentRepository.findAllByUserId(userId);

        // #2. 결과를 저장할 리스트
        List<MyPagePostCommentLoadAllDto> myPagePostCommentLoadAllDtoList = new ArrayList<>();

        // #3. for문을 활용 : PostCommentEntities 내용들을 -> myPagePostCommentLoadAllDtoList 로 옮기기
        for (PostCommentEntity postCommentEntity : postCommentEntities) {
            // #3-1 : 옮길 myPagePostCommentLoadAllDto 객체 생성
            MyPagePostCommentLoadAllDto myPagePostCommentLoadAllDto = new MyPagePostCommentLoadAllDto();

            // #3-2 : postCommentEntity안의 userId를 이용해서, userEntity에서 name과 Image를 가져와서 postCommentLoadAllDto 요소에 대입
            Optional<UserEntity> optionalUserEntity= userRepository.findById(postCommentEntity.getUserId());
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                myPagePostCommentLoadAllDto.setName(userEntity.getName());
                myPagePostCommentLoadAllDto.setImage(userEntity.getImage());
            }

            // #3-3 : postCommentEntity안의 나머지 요소들도, myPagePostCommentLoadAllDto에 대입
            myPagePostCommentLoadAllDto.setContent(postCommentEntity.getContent());
            myPagePostCommentLoadAllDto.setParentCommentId(postCommentEntity.getParentCommentId());

            // #3-4 : 결과 리스트에 추가
            myPagePostCommentLoadAllDtoList.add(myPagePostCommentLoadAllDto);
        }

        // 결과 리스트를 반환
        return myPagePostCommentLoadAllDtoList;
    }
}
