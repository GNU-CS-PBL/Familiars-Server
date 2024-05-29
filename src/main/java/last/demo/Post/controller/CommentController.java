package last.demo.Post.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Post.dto.comment.ChildCommentDto;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.dto.comment.utils.ChildCommentLoadAllDto;
import last.demo.Post.dto.comment.utils.MyPagePostCommentLoadAllDto;
import last.demo.Post.dto.comment.utils.PostCommentLoadAllDto;
import last.demo.Post.dto.post.utils.PostLoadAllDto;
import last.demo.Post.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



    // 부모 댓글 전체 조회 (특정 게시글의 '댓글 아이콘' 눌렀을 때)
    @GetMapping(value = "/api/post/postComment")
    public ResponseEntity<Map<String, Object>> getAllPostCommentsInThePost(@RequestHeader("Authorization") String jwtAccessToken,
                                                                           @RequestParam("postId") Long postId) {
        try {
            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // 서비스 객체에서 조회한 데이터 여러개를 DTO 객체에 담아서 List 자료구조에 주입
            List<PostCommentLoadAllDto> postCommentLoadAllDtoList = commentService.findAllPostCommentByPostId(postId);
            Map<String, Object> response = new HashMap<>();
            response.put("postCommentLoadAllDtoList", postCommentLoadAllDtoList);

            // 이 코드는 맵 객체(response)를 반환합니다.
            // 클라이언트는 이 데이터를 JSON 형식으로 받아 사용할 수 있습니다.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "부모댓글을 조회하는 도중에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 대댓글 시간순 전체 조회 ( 특정 부모댓글의 '답글달기' 눌렀을 때)
    @GetMapping(value = "/api/post/postCommentReply")
    public ResponseEntity<Map<String, Object>> getAllChildCommentsInThePost(@RequestHeader("Authorization") String jwtAccessToken,
                                                                           @RequestParam("parentCommentId") Long parentCommentId) {
        try {
            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // 서비스 객체에서 조회한 데이터 여러개를 DTO 객체에 담아서 List 자료구조에 주입
            List<ChildCommentLoadAllDto> childCommentLoadAllDtoList = commentService.findAllChildCommentByPostId(parentCommentId);
            Map<String, Object> response = new HashMap<>();
            response.put("childCommentLoadAllDtoList", childCommentLoadAllDtoList);

            // 이 코드는 맵 객체(response)를 반환합니다.
            // 클라이언트는 이 데이터를 JSON 형식으로 받아 사용할 수 있습니다.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "대댓글을 조회하는 도중에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 내가쓴 부모 댓글 조회 (마이페이지)
    @GetMapping(value = "/api/post/postComment/MyPage")
    public ResponseEntity<Map<String, Object>> getAllPostCommentsInTheMyPage(@RequestHeader("Authorization") String jwtAccessToken) {
        try {
            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // 서비스 객체에서 조회한 데이터 여러개를 DTO 객체에 담아서 List 자료구조에 주입
            List<MyPagePostCommentLoadAllDto> MyPagePostCommentLoadAllDtoList = commentService.findAllPostCommentByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("postCommentLoadAllDtoList", MyPagePostCommentLoadAllDtoList);

            // 이 코드는 맵 객체(response)를 반환합니다.
            // 클라이언트는 이 데이터를 JSON 형식으로 받아 사용할 수 있습니다.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "마이페이지에서 부모댓글을 조회하는 도중에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
