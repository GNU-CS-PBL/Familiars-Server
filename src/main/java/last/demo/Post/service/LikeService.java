package last.demo.Post.service;

import last.demo.Post.dto.like.LikeDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.like.LikeEntity;
import last.demo.Post.repository.LikeRepository;
import last.demo.Post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public void addLikeOrCancelLikeToPost(Long roomId, Long userId, Long postId) {


        //해당 게시글의 총 좋아요 개수가 필요
        // 1. 해당 게시글 Entity를 가져온다.
        Optional<PostEntity> OptionalPostEntity = postRepository.findById(postId);

        if(OptionalPostEntity.isPresent()){
            PostEntity postEntity = OptionalPostEntity.get();


            // 2. 현재 해당 게시글의 '총 좋아요 갯수'를 가져온다.
            Long currentLikeCount = Optional.ofNullable(postEntity.getTotalLikeCount()).orElse(0L);

            // 3. 사용자 고유 번호를 사용해 likeRepository 에서 해당 사용자가 좋아요를 누른 하나의 게시글 id를 불러옴
            Optional<LikeEntity> likeEntity = likeRepository.findByPostIdAndUserId(postId, userId);

            if(likeEntity.isEmpty()) {
                // 해당 likeEntity를 하나 생성해서 저장하고, postEntity의 currentLikeCount를 1추가해서 저장
                LikeEntity likeEntity1 = new LikeEntity();
                likeEntity1.setPostId(postId);
                likeEntity1.setUserId(userId);
                likeEntity1.setRoomId(roomId);
                likeRepository.save(likeEntity1);
                currentLikeCount += 1;

                // 데이터 저장 및 '총 좋아요 갯수' 반환
                postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
                postRepository.save(postEntity); // 저장

            } else {
                // 있으면 해당 likeEntity를 삭제하고, postEntity의 currentLikeCount를 1감소시켜서 저장

                likeRepository.delete(likeEntity.get());


                currentLikeCount -= 1; //총 좋아요 갯수는 1개 삭제

                // 데이터 저장 및 '총 좋아요 갯수' 반환
                postEntity.setTotalLikeCount(currentLikeCount); // 총 좋아요 갯수 업데이트
                postRepository.save(postEntity); // 저장
            }

        }
    }
}
