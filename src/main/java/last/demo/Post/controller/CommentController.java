package last.demo.Post.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Post.dto.comment.ChildCommentDto;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

            // jwtAccessToken으로부터 사용자 UID를 추출가능한지 확인(검증과정)
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

    //부모 댓글 삭제
    @DeleteMapping(value = "/api/post/postComment")
    public ResponseEntity<String> DeletePostComment(@RequestHeader("Authorization") String jwtAccessToken,
                                                    @RequestParam("parentCommentId") Long parentCommentId) throws Exception {
        try {

            // jwtAccessToken으로부터 사용자 UID를 추출가능한지 확인(검증과정)
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // parentCommentId 를 이용해서 삭제
            commentService.deletePostCommentInfo(parentCommentId);

            return ResponseEntity.ok("부모댓글이 성공적으로 삭제되었습니다..");

        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부모댓글을 삭제하는 도중에 문제가 발생했습니다.");
        }
    }


    // 부모 댓글 시간순 전체 조회
    // 대댓글 시간순 전체 조회


    //대댓글 생성
    @PostMapping(value = "/api/post/postCommentReply")
    public ResponseEntity<String> CreateChildComment(@RequestHeader("Authorization") String jwtAccessToken,
                                                    @RequestParam("postId") Long postId, @RequestParam("roomId") Long roomId,
                                                     @RequestParam("parentCommentId") Long parentCommentId,
                                                    @RequestBody ChildCommentDto childCommentDto) throws Exception {
        try {

            // jwtAccessToken으로부터 사용자 UID를 추출가능한지 확인(검증과정)
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            childCommentDto.setRoomId(roomId);
            childCommentDto.setPostId(postId);
            childCommentDto.setUserId(userId);

            commentService.saveChildCommentInfo(childCommentDto,parentCommentId);

            return ResponseEntity.ok("대댓글이 성공적으로 생성되었습니다.");

        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("대댓글을 생성하는 도중에 문제가 발생했습니다.");
        }
    }


    //대댓글 삭제
    @DeleteMapping(value = "/api/post/postCommentReply")
    public ResponseEntity<String> DeleteChildComment(@RequestHeader("Authorization") String jwtAccessToken,
                                                    @RequestParam("childCommentId") Long childCommentId) throws Exception {
        try {

            // jwtAccessToken으로부터 사용자 UID를 추출가능한지 확인(검증과정)
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // childCommentId 를 이용해서 삭제
            commentService.deleteChildCommentInfo(childCommentId);

            return ResponseEntity.ok("대댓글이 성공적으로 삭제되었습니다..");

        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("대댓글을 삭제하는 도중에 문제가 발생했습니다.");
        }
    }
}
