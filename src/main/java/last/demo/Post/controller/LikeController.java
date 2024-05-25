package last.demo.Post.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Post.dto.like.LikeDto;
import last.demo.Post.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;


    // 좋아요 클릭시 -> 좋아요 1 증가/감소  && 좋아요 총 갯수 반환
    @PostMapping("/api/post/Like") // '좋아요' & '좋아요 취소' 기능을 토글하기 위한 API
    public ResponseEntity<Map<String, Object>> addLikeOrCancelLikeToPost(@RequestHeader("Authorization") String jwtAccessToken,
                                                                         @RequestParam("roomId") Long roomId,
                                                                         @RequestParam("postId") Long postId
                                                                         )
    {
        // 사용자 UID를 추출
        // 게시글과 사용자 태그를 같이 수정
        String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

        likeService.addLikeOrCancelLikeToPost(roomId,userId,postId);

        Map<String, Object> response = new HashMap<>();
        return new ResponseEntity<>(response, HttpStatus.OK); //200 응답코드 반환
    }
}
