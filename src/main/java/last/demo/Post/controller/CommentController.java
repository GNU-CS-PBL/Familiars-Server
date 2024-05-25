package last.demo.Post.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    //부모 댓글 생성
    @PostMapping(value = "/api/post/postComment")
    public ResponseEntity<String> CreatePostComment(@RequestHeader("Authorization") String jwtAccessToken,
                                                    @RequestParam("postId") Long postId, @RequestParam("roomId") Long roomId,
                                                    @RequestBody PostCommentDto postCommentDto) throws Exception {
        try {

            // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            postCommentDto.setRoomId(roomId);
            postCommentDto.setPostId(postId);
            postCommentDto.setUserId(userId);

            commentService.savePostCommentInfo(postCommentDto);

            return ResponseEntity.ok("부모댓글이 성공적으로 생성되었습니다.");

        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부모댓글을 생성하는 도중에 문제가 발생했습니다.");
        }
    }

    // **** 부모 댓글을 하나 쓰거나 삭제할 때 마다, 게시글 postEntity에 댓글 수를 입력해주거나 빼주어야 한다.

    // * 특정 게시글을 눌렀을때, 모든 부모댓글 조회 (postId 로 전체조회)
    // * 부모 댓글을 눌렀을 때, 모든 대댓글 조회 (postId로 전체조회)

       /* 1st 안
   1. 내가쓴 댓글 모두 조회
                수정
                삭제 + 관련된 대댓글
   2. 내가쓴 대댓글 모두 조회
                수정
                삭제                        */



    /*  2nd 안
    마이페이지에서, 내가쓴 댓글 + 대댓글을 시간순으로 모두 조회한다.
    1. 내가쓴 댓글+대댓글 전체 조회
    2. 내가쓴 댓글+대댓글  수정
    3. 내가쓴 댓글+대댓글  삭제                      */



}
