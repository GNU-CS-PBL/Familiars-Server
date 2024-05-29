package last.demo.Post.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.dto.post.PostDto;
import last.demo.Post.dto.post.utils.PostLoadAllDto;
import last.demo.Post.dto.post.utils.PostTagAllDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.service.PostService;
import last.demo.Room.dto.utils.RoomLoadAllDto;
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
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;


    // roomId는 param으로 받는다.
    // '태그될 사용자들의 UID 값' + 게시글 작성내용들을 DTO로 받는다.
    // postId는 추출

    // 게시글 생성 & 사용자 태그 생성
    @PostMapping(value = "/api/post")
    public ResponseEntity<String> CreatePost(@RequestHeader("Authorization") String jwtAccessToken,
                                             @RequestParam("roomId") Long roomId,
                                             @RequestBody PostDto postDto) throws Exception {
        try {
            //-------------------------------------- 게시물 생성--------------------------------------------------------

            // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // PostDto에 위의 값들을 담아 저장한다. -> 해당 postId 즉, PK값을 추출
            postDto.setRoomId(roomId);
            postDto.setUserId(userId);
            PostEntity savedPostEntity = postService.savePostInfo(postDto);


            //-------------------------------------- 사용자 태그 생성-----------------------------------------------------

            // 1. 저장된 게시글의 PK 값을 추출한다.
            Long postId = savedPostEntity.getPostId();

            //2. userId, roomId, postId, postTageDto를 가지고서 게시글 태그 테이블을 각각 생성한다. -> for문 돌림
            postService.createPostTag(userId, roomId, postId, postDto);

            return ResponseEntity.ok("게시물이 성공적으로 생성되었습니다.");

        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물을 생성하는 도중에 문제가 발생했습니다.");
        }
    }



    // 게시글 수정 & 사용자 태그 수정
    @PutMapping(value = "/api/post")
    public ResponseEntity<Object> UpdatePost(@RequestHeader("Authorization") String jwtAccessToken,
                                             @RequestParam("postId") Long postId,
                                             @RequestParam("roomId") Long roomId,
                                             @RequestBody PostDto postDto) {
        try {
            // 게시글과 사용자 태그를 같이 수정
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // PostDto에 위의 값들을 담아 저장한다.
            postDto.setRoomId(roomId);
            postDto.setUserId(userId);
            postDto.setUserId(postId);

            postService.updateRoomInfo(postId, postDto);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "수정 완료");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 응답코드 반환

        } catch (RuntimeException e) {
            // 예외가 발생한 경우, 적절한 에러 메시지를 포함한 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 응답코드 반환
        }
    }



    // 게시글 삭제
    @DeleteMapping(value = "/api/post")
    public ResponseEntity<Object> deletePostInfo(@RequestHeader("Authorization") String jwtAccessToken,
                                                 @RequestParam("postId") Long postId) {
        try {
            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            //게시글을 바로 삭제하기에 앞서, 게시글 테이블의 postId를 왜래키로 사용하는 다른 Entity도 모두 삭제해줘야 한다.
            postService.deletePostRelatedInfo(postId);

            // 게시글 테이블에서 해당 postId에 해당하는 Entity를 찾고 삭제한다.
            postService.deletePostInfo(postId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "삭제 완료");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 응답코드 반환

        } catch (RuntimeException e) {
            // 예외가 발생한 경우, 적절한 에러 메시지를 포함한 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 응답코드 반환
        }
    }



    // 태그될 사용자들 조회
    @GetMapping(value = "/api/postTag")
    public ResponseEntity<Object> GetPostTagAll(@RequestHeader("Authorization") String jwtAccessToken,
                                                @RequestParam("roomId") Long roomId) {
        try {
            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // roomId를 이용해서 -> 방 구성원 테이블에서 , 해당 방 안에 있는 userId, alias 모두 가져온다.
            // 그 중 userId를 이용해 사용자 테이블에서, 사용자의 image를 가져온다.
            // 3가지 정보(userId, alias, image)를 DTO에 담아 리스트로 만들어 반환한다.
            List<PostTagAllDto> results = postService.getRoomMemberInfoAndUserImage(roomId);
            return new ResponseEntity<>(results, HttpStatus.OK); // 200 응답코드와 함께 results 반환

        } catch (RuntimeException e) {
            // 예외가 발생한 경우, 적절한 에러 메시지를 포함한 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 응답코드 반환
        }

    }


    //게시글 전체 조회 (in 특정 방)----------------------------------------------------------------------------------------
    //(클라이언트는 요청시 page 값을 같이 param 으로 전달해야한다. 안할시 default 값은 page = 0 이다.)
    @GetMapping(value = "/api/post")
    public ResponseEntity<Map<String, Object>> getAllPostsInTheRoom(@RequestHeader("Authorization") String jwtAccessToken,
                                                                    @RequestParam("roomId") Long roomId ,
                                                                    @PageableDefault(size = 10, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
        // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
        String jwtToken = jwtAccessToken.substring(7);
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

        // postLoadAllDtoList 는 postLoadAllDto 들을 리스트 형태로 가지고 있는 자료구조
        Page<PostLoadAllDto> postLoadAllDtoList = postService.findAllPostsByRoomId(roomId, pageable);  //서비스 객체에서 조회한 데이터 여러개를 DTO 객체에 담아서 List 자료구조에 주입
        Map<String, Object> response = new HashMap<>();
        response.put("boardTotalList", postLoadAllDtoList.getContent());
        return ResponseEntity.ok(response); //이 코드는 맵 객체(response)를 반환합니다.
                                            // 클라이언트는 이 데이터를 JSON 형식으로 받아 사용할 수 있습니다.
        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "게시물을 조회하는 도중에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }



    //게시글 전체 조회 (in 마이 페이지)----------------------------------------------------------------------------------------
    // 사용자 UID가 필요하다. -> postEntity에서 사용자 UID로 조회한 모든 게시글을 가져온다.
    @GetMapping(value = "/api/post/myPage")
    public ResponseEntity<Map<String, Object>> getAllPostsInMyPage(@RequestHeader("Authorization") String jwtAccessToken,
                                                                    @PageableDefault(size = 10, sort = "postId", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // postLoadAllDtoList 는 postLoadAllDto 들을 리스트 형태로 가지고 있는 자료구조
            Page<PostLoadAllDto> postLoadAllDtoList = postService.findAllPostsByUserId(userId, pageable);  //서비스 객체에서 조회한 데이터 여러개를 DTO 객체에 담아서 List 자료구조에 주입
            Map<String, Object> response = new HashMap<>();
            response.put("boardTotalList", postLoadAllDtoList.getContent());
            return ResponseEntity.ok(response); //이 코드는 맵 객체(response)를 반환합니다.

        } catch (Exception e) {
            // 예외가 발생한 경우, 에러 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "게시물을 조회하는 도중에 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

}
