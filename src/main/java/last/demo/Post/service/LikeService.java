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


        //해당 게시글의 총 좋아요 개수가 필요
        // 1. 해당 게시글 Entity를 가져온다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

        if(OptionalPostEntity.isPresent()){
            PostEntity postEntity = OptionalPostEntity.get();
            // 2. 현재 해당 게시글의 '총 좋아요 갯수'를 가져온다.
            Long currentLikeCount = Optional.ofNullable(postEntity.getTotalLikeCount()).orElse(0L);

            // 3. 사용자 고유 번호를 사용해 userLikeListRepository에서 (사용자가 좋아요를 누른) 모든 게시글 id를 불러옴
            UserLikeListEntity userLikeListEntity = userLikeListRepository.findByUserId(userId)
                    .orElseGet(() -> { // 조회 결과가 없으면 람다식을 사용하여 새로운 UserLikeListEntity 객체를 생성
                        UserLikeListEntity newUserLikeListEntity = new UserLikeListEntity();
                        newUserLikeListEntity.setUserId(userId); // 사용자 고유 번호 초기화
                        return newUserLikeListEntity;
                    });

            // 4. 게시글 목록에서, 해당 게시글 Id가 있는지 확인
            boolean result = userLikeListEntity.findByPostId(postId);

            if (result) { //게시글 목록에 해당 게시글 존재 시
                // 게시글 목록에서 해당 게시글 Id를 삭제하고
                userLikeListEntity.removeLikedPostId(postId);

                // 해당 게시글 Id와 사용자 Id로 조회한 LikeEntity를 제거한다.
                Optional<LikeEntity> likeEntity = likeRepository.findByPostIdAndUserId(postId, userId);
                likeRepository.delete(likeEntity.get());

                // postEntity의 currentLikeCount를 1감소해서 저장
                currentLikeCount -= 1;

                // 데이터 저장 및 '총 좋아요 갯수' 반환
                postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
                postRepository.save(postEntity); // 저장


            } else { // 게시글 목록에 해당 게시글이 없을 경우
                // 게시글 목록에 게시글 번호를 추가해주고,
                userLikeListEntity.addLikedPostId(postId);

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
            }

        }
    }
}
