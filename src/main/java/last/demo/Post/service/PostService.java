package last.demo.Post.service;

import last.demo.OAuth.entity.UserEntity;
import last.demo.OAuth.repository.UserRepository;
import last.demo.Post.dto.post.PostDto;
import last.demo.Post.dto.post.PostTagDto;
import last.demo.Post.dto.post.utils.PostImageDto;
import last.demo.Post.dto.post.utils.PostLoadAllDto;
import last.demo.Post.dto.post.utils.PostTagAllDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.PostTagEntity;
import last.demo.Post.entity.comment.ChildCommentEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.entity.like.post.LikeEntity;
import last.demo.Post.entity.like.postComment.PostCommentLikeEntity;
import last.demo.Post.repository.comment.ChildCommentRepository;
import last.demo.Post.repository.comment.CommentRepository;
import last.demo.Post.repository.like.post.LikeRepository;
import last.demo.Post.repository.like.postComment.PostCommentLikeRepository;
import last.demo.Post.repository.post.PostRepository;
import last.demo.Post.repository.post.PostTagRepository;
import last.demo.Room.entity.RoomMemberEntity;
import last.demo.MyPage.repository.RoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final PostTagRepository postTagRepository;
    private final LikeRepository likeRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final CommentRepository commentRepository;
    private final ChildCommentRepository childCommentRepository;


    // 게시글을 저장 및 반환하는 메소드
    public PostEntity savePostInfo(PostDto postDto) {
        postDto.setCreateDate(new Timestamp(System.currentTimeMillis()));
        postDto.setModifyDate(new Timestamp(System.currentTimeMillis()));
        PostEntity postEntity = PostEntity.toPostEntity(postDto); //DTO -> Entity 변환
        return postRepository.save(postEntity);
    }

    // 게시글 안의 태그를 생성하는 메소드
    public void createPostTag(Long userId, Long roomId, Long postId, PostDto postDto) {
        // 태그될 사용자 UID (userId) 들을 추출하여 PostTagDto에 추가
        List<PostTagDto> postTagDtos = new ArrayList<>();
        for (Long taggedUserId : postDto.getTaggedUserId()) {
            PostTagDto postTagDto = new PostTagDto();
            postTagDto.setUserId(userId);
            postTagDto.setPostId(postId);
            postTagDto.setRoomId(roomId);
            postTagDto.setTaggedUserId(taggedUserId);
            postTagDto.setCreateDate(postDto.getCreateDate());
            postTagDto.setModifyDate(postDto.getModifyDate());
            postTagDtos.add(postTagDto);
        }

        // PostTagDto를 PostTagEntity로 변환하여 저장
        List<PostTagEntity> postTagEntities = new ArrayList<>();
        for (PostTagDto postTagDto : postTagDtos) {
            PostTagEntity postTagEntity = PostTagEntity.toPostTagEntity(postTagDto);
            postTagEntities.add(postTagEntity);
        }
        // postTagRepository에 저장
        postTagRepository.saveAll(postTagEntities);

    }


    // 게시글을 수정하는 메소드
    public void updateRoomInfo(Long postId, PostDto postDto) {
        // postId를 사용해서 해당 postEntity를 조회한다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

        // postId를 사용해서 해당 postTagEntity를 조회한다.
        List<PostTagEntity> postTagEntities = postTagRepository.findByPostEntityPostId(postId);

        // postId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalPostEntity.isPresent()) {
            PostEntity postEntity = OptionalPostEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            postEntity.setTitle(postDto.getTitle()); // 추출한 roomEntity를 업데이트 한다.
            postEntity.setContent(postDto.getContent()); // 추출한 roomEntity를 업데이트 한다.
            postEntity.setPostImage(postDto.getPostImage()); // 추출한 roomEntity를 업데이트 한다.
            postEntity.setModifyDate(new Timestamp(System.currentTimeMillis()));
            postRepository.save(postEntity); // 변경된 Entity를 저장한다.

            //----------------------------------------------------------------------------------------

            // 조회한 postTagEntities를 삭제한다.
            postTagRepository.deleteAll(postTagEntities);

            // 다시 PostTagDto를 만들어서 저장
            List<PostTagDto> postTagDtos = new ArrayList<>();
            for (Long taggedUserId : postDto.getTaggedUserId()) {
                PostTagDto postTagDto = new PostTagDto();
                postTagDto.setUserId(postDto.getUserId());
                postTagDto.setPostId(postId);
                postTagDto.setRoomId(postDto.getRoomId());
                postTagDto.setTaggedUserId(taggedUserId);
                postTagDto.setCreateDate(postDto.getCreateDate());
                postTagDto.setModifyDate(postDto.getModifyDate());
                postTagDtos.add(postTagDto);
            }

            // PostTagDto를 PostTagEntity로 변환하여 저장
            List<PostTagEntity> postTagEntityList = new ArrayList<>();
            for (PostTagDto postTagDto : postTagDtos) {
                PostTagEntity postTagEntity = PostTagEntity.toPostTagEntity(postTagDto);
                postTagEntityList.add(postTagEntity);
            }
            // postTagRepository에 저장
            postTagRepository.saveAll(postTagEntityList);


        } else {
            // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 postId에 해당하는 게시글이 존재하지 않습니다.");
        }
    }



    // 게시글을 삭제하는 메소드
    public void deletePostInfo(Long postId) {
    // roomId를 사용해서 roomEntity를 조회한다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

    // postId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalPostEntity.isPresent()) {
            PostEntity postEntity = OptionalPostEntity.get(); //optional 객체에서 Entity를 꺼낸다.

        // 게시글을 삭제한다.
            postRepository.delete(postEntity);

        } else {
        // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 roomId에 해당하는 방이 존재하지 않습니다.");
        }
    }



    // 게시글과 관련된 다른 테이블 삭제하는 메소드 -----> 게시글의 postId를 왜래키로 사용하는 다른 Entity들을 찾아 전부 삭제 하는 코드
    public void deletePostRelatedInfo(Long postId) {
        try {

            // #1. 사용자 태그 테이블 삭제 -----> postId를 사용하여 postTagEntity를 조회한다.
            List<PostTagEntity> postTagEntities = postTagRepository.findByPostEntityPostId(postId);
            if (!postTagEntities.isEmpty()) {// 조회된 postTagEntity가 있는지 확인
                // 조회된 각각의 postTagEntity를 삭제한다.
                for (PostTagEntity postTagEntity : postTagEntities) {
                    postTagRepository.delete(postTagEntity);
                }
            } else {
                // postId에 해당하는 사용자 태그 Entity가 존재하지 않을 경우 예외 처리를 수행합니다.
                throw new RuntimeException("해당 postId에 해당하는 사용자 태그 Entity가 존재하지 않습니다.");
            }

            // #2. [게시글 좋아요] 테이블 삭제
            // LikeEntity와 PostEntity 간에 양방향 관계가 설정되어 있습니다.
            // 이를 활용하여 LikeEntity를 조회할 때 postId를 사용하는 대신 PostEntity를 이용할 수 있습니다.
            List<LikeEntity> likeEntities = likeRepository.findByPostEntity_PostId(postId);
            if (!likeEntities.isEmpty()) {
                for (LikeEntity likeEntity : likeEntities) {
                    likeRepository.delete(likeEntity);
                }
            } else {
                throw new RuntimeException("해당 postId에 해당하는 좋아요 Entity가 존재하지 않습니다.");
            }


            // #3. [부모댓글 좋아요] 테이블 삭제
            // PostCommentLikeEntity와 PostCommentEntity 간에 양방향 관계가 설정되어 있습니다.
            // 이를 활용하여 PostCommentLikeEntity를 조회할 때 postId를 사용하는 대신 PostEntity를 이용할 수 있습니다.
            List<PostCommentLikeEntity> postCommentLikeEntities = postCommentLikeRepository.findByPostCommentEntityParentCommentId(postId);
            if (!postCommentLikeEntities.isEmpty()) {
                for (PostCommentLikeEntity postCommentLikeEntity : postCommentLikeEntities) {
                    postCommentLikeRepository.delete(postCommentLikeEntity);
                }
            } else {
                throw new RuntimeException("해당 postId에 해당하는 부모댓글좋아요 Entity가 존재하지 않습니다.");
            }


            // #4 [부모댓글] + [자식댓글] 테이블 삭제
            // PostEntity : PostCommentEntity = 1 : N  --> 양방향 관계가 설정되어 있다.
            List<PostCommentEntity> postCommentEntities = commentRepository.findByPostEntityPostId(postId);
            if (!postCommentEntities.isEmpty()) {
                for (PostCommentEntity postCommentEntity : postCommentEntities) {
                    Long parentCommentId = postCommentEntity.getParentCommentId();
                    List<ChildCommentEntity> childCommentEntities = childCommentRepository.findByParentCommentParentCommentId(parentCommentId);
                    for(ChildCommentEntity childCommentEntity : childCommentEntities) {
                        childCommentRepository.delete(childCommentEntity);
                    }
                    commentRepository.delete(postCommentEntity);
                }
            } else {
                throw new RuntimeException("해당 postId에 해당하는 부모댓글 Entity가 존재하지 않습니다.");
            }

        } catch (RuntimeException e) {
            // 예외가 발생하면 콘솔에 로그를 출력합니다.
            e.printStackTrace();
            // 여기서 필요한 다른 예외 처리를 추가할 수 있습니다.
        }
        // 예외가 발생하더라도 다음 코드들이 정상적으로 실행됩니다.
    }


    // 태그될 사람들 조회시, roomId 값을 갖는 모든 RoomMemberEntity 들을 조회하는 메소드
    public List<PostTagAllDto> getRoomMemberInfoAndUserImage(Long roomId) {
        // roomId를 사용해서 roomMemberEntity 들을 조회한다.
        List<RoomMemberEntity> roomMemberEntities = roomMemberRepository.findByRoomId(roomId);

        if (roomMemberEntities != null || !roomMemberEntities.isEmpty()) {
            List<PostTagAllDto> postTagAllDtos = new ArrayList<>(); // 빈 리스트 선언

            // 방구성원 엔티티 만큼 for문 반복
            for (RoomMemberEntity roomMemberEntity : roomMemberEntities) {
                // 각 엔티티마다 userId를 추출
                Long userId = roomMemberEntity.getUserId(); // 사용자 Id 추출
                String alias = roomMemberEntity.getAlias(); // 사용자 별칭 추출

                // userRepository를 사용하여 userEntity를 조회하여 사용자 이미지를 추출
                UserEntity userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("해당 ID에 해당하는 사용자를 찾을 수 없습니다."));

                // userEntity에서 사용자 프로필 이미지 추출
                String image = userEntity.getImage();

                // 사용자 이미지, userId, alias를 PostTagAllDto 에 넣는다.
                PostTagAllDto postTagAllDto = new PostTagAllDto(userId, alias, image);

                postTagAllDtos.add(postTagAllDto); // 하나씩 리스트에 DTO를 넣는다.

            }
            return postTagAllDtos;

        } else { // roomMemberEntities가 null이거나 비어있는 경우 예외를 던집니다.
            throw new RuntimeException("해당 방에 속한 구성원이 없습니다.");
        }

    }

    // 상대방 태그시, userId로 alias를 가져오는 메소드
    public String getAlias(long userId) {
        // roomId를 사용해서 roomEntity를 조회한다.
        Optional<RoomMemberEntity> OptionalRoomMemberEntity = roomMemberRepository.findByUserId(userId);

        // postId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalRoomMemberEntity.isPresent()) {
            RoomMemberEntity roomMemberEntity = OptionalRoomMemberEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // alias 확보
            String alias = roomMemberEntity.getAlias();

            return alias;

        } else {
            // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 userId에 해당하는 방 구성원이 존재하지 않습니다.");
        }
    }

    // 상대방 태그시, userId로 사용자의 image를 가져오는 메소드
    public String getImage(long userId) {
        // userId를 사용해서 userEntity를 조회한다.
        Optional<UserEntity> OptionalUserEntity = userRepository.findById(userId);

        if(OptionalUserEntity.isPresent()) {
            UserEntity userEntity = OptionalUserEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // image 확보
            String image = userEntity.getImage();

            return image;

        } else {
            // userId에 해당하는 사용자 정보가 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 userId에 해당하는 사용자 정보가 존재하지 않습니다.");
        }
    }

    // 게시글 이미지 수정
    public void updatePostImage(Long postId, PostImageDto postImageDto) {
        // postId를 사용해서 roomEntity를 조회한다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

        // roomId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalPostEntity.isPresent()) {
            PostEntity postEntity = OptionalPostEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // PostImageDto에서 이미지 리스트를 가져와서 PostEntity의 이미지 리스트에 추가한다.
            postEntity.getPostImage().addAll(postImageDto.getPostImage());
            postEntity.setModifyDate(new Timestamp(System.currentTimeMillis()));
            postRepository.save(postEntity); // 변경된 Entity를 저장한다.

        } else {
            // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 postId에 해당하는 게시글이 존재하지 않습니다.");
        }
    }

    // 게시글 이미지 전체 삭제
    public void deletePostImage(Long postId) {
        // postId를 사용해서 roomEntity를 조회한다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

        // roomId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalPostEntity.isPresent()) {
            PostEntity postEntity = OptionalPostEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // postEntity에서 postImage 변수의 값을 삭제한다.
            postEntity.setPostImage(null);

            postRepository.save(postEntity);

        } else {
            // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 postId에 해당하는 게시글이 존재하지 않습니다.");
        }
    }


    //게시글 전체 조회 (in 특정 방)
    public Page<PostLoadAllDto> findAllPostsByRoomId(Long roomId, Pageable pageable) { // postLoadAllDto 리스트를 반환한다.


        // #1. 특정 roomId 에 해당하는 모든 postEntity들을 10개씩 List형태로 불러온다.
        Page<PostEntity> postEntityList = postRepository.findAllByRoomId(roomId,pageable);

        // #2. 결과를 저장할 리스트
        List<PostLoadAllDto> postLoadAllDtoList = new ArrayList<>();

        // #3. for문을 활용 : PostEntityList 내용들을 -> PostLoadAllDtoList 로 옮기기
        for (PostEntity postEntity : postEntityList.getContent()) {
            // #3-1 : 옮길 postLoadAllDto 객체 생성
            PostLoadAllDto postLoadAllDto = new PostLoadAllDto();

            // #3-2 : postEntity안의 userId를 이용해서, userEntity에서 name과 Image를 가져와서 postLoadAllDto 요소에 대입
            Optional<UserEntity> optionalUserEntity= userRepository.findById(postEntity.getUserId());
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                postLoadAllDto.setName(userEntity.getName());
                postLoadAllDto.setImage(userEntity.getImage());
            }
            // #3-3 : postId에 해당하는 모든 postTagEntity를 찾아서 -> taggedUserId를 모두 꺼내서 postLoadAllDto에 List형식으로 넣기
            List<PostTagEntity> postTagEntityList = postTagRepository.findByPostEntityPostId(postEntity.getPostId());
            for (PostTagEntity postTagEntity : postTagEntityList) {
                postLoadAllDto.addTaggedUserId(postTagEntity.getTaggedUserId()); // 사용자 태그를 추가하는 함수를 적용
            }

            // #3-4 : postEntity안의 나머지 요소들도, postLoadAllDto에 대입
            postLoadAllDto.setTitle(postEntity.getTitle());
            postLoadAllDto.setPostId(postEntity.getPostId());
            postLoadAllDto.setContent(postEntity.getContent());
            postLoadAllDto.setTotalLikeCount(postEntity.getTotalLikeCount());
            postLoadAllDto.setPostCommentCount(postEntity.getPostCommentCount());
            postLoadAllDto.setCreateDate(postEntity.getCreateDate());
            postLoadAllDto.setModifyDate(postEntity.getModifyDate());
            postLoadAllDto.setPostImage(postEntity.getPostImage());


            postLoadAllDtoList.add(postLoadAllDto); // 결과 리스트에 추가
        }

        // 결과 리스트를 페이지 형태로 반환
        return  new PageImpl<>(postLoadAllDtoList, pageable, postEntityList.getTotalElements());
    }

    //게시글 전체 조회 (in 마이페이지)
    public Page<PostLoadAllDto> findAllPostsByUserId(Long userId, Pageable pageable) {
        // #1. 특정 userId 에 해당하는 모든 postEntity들을 10개씩 List형태로 불러온다.
        Page<PostEntity> postEntityList = postRepository.findAllByUserId(userId,pageable);

        // #2. 결과를 저장할 리스트
        List<PostLoadAllDto> postLoadAllDtoList = new ArrayList<>();

        // #3. for문을 활용 : PostEntityList 내용들을 -> PostLoadAllDtoList 로 옮기기
        for (PostEntity postEntity : postEntityList.getContent()) {
            // #3-1 : 옮길 postLoadAllDto 객체 생성
            PostLoadAllDto postLoadAllDto = new PostLoadAllDto();

            // #3-2 : postEntity안의 userId를 이용해서, userEntity에서 name과 Image를 가져와서 postLoadAllDto 요소에 대입
            Optional<UserEntity> optionalUserEntity= userRepository.findById(userId);
            if (optionalUserEntity.isPresent()) {
                UserEntity userEntity = optionalUserEntity.get();
                postLoadAllDto.setName(userEntity.getName());
                postLoadAllDto.setImage(userEntity.getImage());
            }
            // #3-3 : postId에 해당하는 모든 postTagEntity를 찾아서 -> taggedUserId를 모두 꺼내서 postLoadAllDto에 List형식으로 넣기
            List<PostTagEntity> postTagEntityList = postTagRepository.findByPostEntityPostId(postEntity.getPostId());
            for (PostTagEntity postTagEntity : postTagEntityList) {
                postLoadAllDto.addTaggedUserId(postTagEntity.getTaggedUserId()); // 사용자 태그를 추가하는 함수를 적용
            }

            // #3-4 : postEntity안의 나머지 요소들도, postLoadAllDto에 대입
            postLoadAllDto.setTitle(postEntity.getTitle());
            postLoadAllDto.setPostId(postEntity.getPostId());
            postLoadAllDto.setContent(postEntity.getContent());
            postLoadAllDto.setTotalLikeCount(postEntity.getTotalLikeCount());
            postLoadAllDto.setPostCommentCount(postEntity.getPostCommentCount());
            postLoadAllDto.setCreateDate(postEntity.getCreateDate());
            postLoadAllDto.setModifyDate(postEntity.getModifyDate());
            postLoadAllDto.setPostImage(postEntity.getPostImage());


            postLoadAllDtoList.add(postLoadAllDto); // 결과 리스트에 추가
        }

        // 결과 리스트를 페이지 형태로 반환
        return  new PageImpl<>(postLoadAllDtoList, pageable, postEntityList.getTotalElements());
    }
}
