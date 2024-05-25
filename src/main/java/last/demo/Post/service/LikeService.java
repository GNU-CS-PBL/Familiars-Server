package last.demo.Post.service;

import last.demo.Post.dto.like.LikeDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.like.LikeEntity;
import last.demo.Post.entity.like.UserLikeListEntity;
import last.demo.Post.repository.LikeRepository;
import last.demo.Post.repository.PostRepository;
import last.demo.Post.repository.UserLikeListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserLikeListRepository userLikeListRepository;

    public void addLikeOrCancelLikeToPost(Long roomId, Long userId, Long postId) {

        // 해당 게시글 Entity를 가져온다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

        if(OptionalPostEntity.isPresent()){ // 게시글 엔티티가 존재하는 경우.
            PostEntity postEntity = OptionalPostEntity.get();

            // 1. 현재 해당 게시글의 '총 좋아요 갯수'를 가져온다.
            Long currentLikeCount = Optional.ofNullable(postEntity.getTotalLikeCount()).orElse(0L);

            // 2. 사용자 고유 번호를 사용해 userLikeListRepository (사용자 좋아요 리스트) 조회
            Optional<UserLikeListEntity> OptionalUserLikeListEntity = userLikeListRepository.findByUserId(userId);

            if(OptionalUserLikeListEntity.isPresent()){
                UserLikeListEntity userLikeListEntity = OptionalUserLikeListEntity.get();

                // 3. 게시글 번호 리스트 가져오기
                List<Long> postIdList = userLikeListEntity.getPostId();
                Long targetPostId = postId; // 찾고자 하는 postId 입력

                // 4. 게시글 목록에서, 해당 게시글 Id가 있는지 확인
                if (postIdList.contains(targetPostId)) {
                    // postIdList 안에 targetPostId가 존재함
                    userLikeListEntity.removeLikedPostId(postId); // 게시글 목록에서 해당 게시글 Id를 삭제
                    userLikeListRepository.save(userLikeListEntity); // 저장

                    // 해당 게시글 Id와 사용자 Id로 조회한 -> LikeEntity를 제거한다.
                    Optional<LikeEntity> optionalLikeEntity = likeRepository.findByPostIdAndUserId(postId, userId);
                    if (optionalLikeEntity.isPresent()) {
                        LikeEntity likeEntity = optionalLikeEntity.get();
                        likeRepository.delete(likeEntity);
                    } else {
                        // 만약 조회된 LikeEntity가 없는 경우에 대한 처리
                    }

                    // postEntity의 currentLikeCount를 1감소해서 저장
                    currentLikeCount -= 1;

                    // 데이터 저장 및 '총 좋아요 갯수' 반환
                    postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
                    postRepository.save(postEntity); // 저장
                    System.out.println("좋아요 취소");

                } else {
                    // postIdList 안에 targetPostId가 존재하지 않음
                    // 게시글 목록에 게시글 번호를 추가해주고 & 저장
                    userLikeListEntity.addLikedPostId(postId);
                    userLikeListRepository.save(userLikeListEntity);

                    // 해당 likeEntity를 하나 생성해서 저장하고,
                    LikeEntity likeEntity1 = new LikeEntity();
                    likeEntity1.setPostId(postId);
                    likeEntity1.setUserId(userId);
                    likeEntity1.setRoomId(roomId);
                    likeRepository.save(likeEntity1);

                    // postEntity의 currentLikeCount를 1추가해서 저장
                    currentLikeCount += 1;

                    // 데이터 저장 및 '총 좋아요 갯수' 반환
                    postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
                    postRepository.save(postEntity); // 저장
                    System.out.println("좋아요 추가1");
                }
            } else {
                // 사용자가 어떠한 게시글에도 좋아요를 안 누른 상태라면

                // 1. 새로운 UserLikeListEntity 객체를 생성
                UserLikeListEntity newUserLikeListEntity = new UserLikeListEntity();
                newUserLikeListEntity.setUserId(userId); // 사용자 고유 번호 초기화
                newUserLikeListEntity.addLikedPostId(postId); // 게시글 목록에 게시글 번호를 추가해준다.
                userLikeListRepository.save(newUserLikeListEntity); // 저장

                // 2. 새로운 LikeEntity 객체를 생성 & postEntity에 저장
                LikeEntity newLikeEntity = new LikeEntity();
                newLikeEntity.setPostId(postId);
                newLikeEntity.setUserId(userId);
                newLikeEntity.setRoomId(roomId);
                likeRepository.save(newLikeEntity);

                // postEntity의 currentLikeCount를 1추가해서 저장
                currentLikeCount += 1;

                // 데이터 저장 및 '총 좋아요 갯수' 반환
                postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
                postRepository.save(postEntity); // 저장
                System.out.println("좋아요 추가2");
            }



        }
    }
}
