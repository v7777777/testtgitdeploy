package main.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.NewCommentRequest;
import main.model.dto.request.NewPostRequest;
import main.model.dto.request.PostModerationRequest;
import main.model.dto.request.RateRequest;
import main.model.dto.response.CalendarResponse;
import main.model.dto.response.DetailedPostResponse;
import main.model.dto.response.NewCommentResponse;
import main.model.dto.response.ResultResponse;
import main.model.dto.response.listResponses.ListPostResponse;
import main.model.dto.response.listResponses.ListTagResponse;
import main.service.ModerationService;
import main.service.PostService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiPostController {

  private final PostService postService;
  private final TagService tagService;
  private final ModerationService moderationService;


  @GetMapping("/post")
  public ResponseEntity<ListPostResponse> getPosts(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam(required = false, defaultValue = "recent") String mode) {

    return ResponseEntity.ok(postService.getPosts(offset, limit, mode));

  }

  @GetMapping("/post/search")
  public ResponseEntity<ListPostResponse> searchPosts(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam(required = false, defaultValue = "recent") String query) {

    return ResponseEntity.ok(postService.searchPosts(offset, limit, query));

  }

  @GetMapping("/tag")
  public ResponseEntity<ListTagResponse> getTags(@RequestParam(required = false) String query) {

    return ResponseEntity.ok(tagService.getTags(query));

  }

  @GetMapping("/calendar")
  public ResponseEntity<CalendarResponse> calendar(@RequestParam(required = false) String year) {

    return ResponseEntity.ok(postService.calendar(year));
  }


  @GetMapping("/post/byDate")
  public ResponseEntity<ListPostResponse> getPostsByDate(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam String date) {

    return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));

  }

  @GetMapping("/post/byTag")
  public ResponseEntity<ListPostResponse> getPostsByTag(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam String tag) {

    return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));

  }

  @GetMapping("/post/{id}")
  public ResponseEntity<DetailedPostResponse> getPostById(
      @PathVariable int id) {

    return ResponseEntity.ok(postService.getPostById(id));

  }

  @GetMapping("/post/my")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ListPostResponse> getMyPosts(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam(required = false, defaultValue = "inactive") String status
  ) {

    return ResponseEntity.ok(postService.getMyPosts(offset, limit, status));

  }

  @PostMapping("/post")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ResultResponse> addPost(@RequestBody NewPostRequest newPost) {

    return ResponseEntity.ok(postService.addPost(newPost));

  }

  @PutMapping("/post/{id}")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ResultResponse> editPost(@PathVariable int id,
      @RequestBody NewPostRequest editedPost) {

    return ResponseEntity.ok(postService.editPost(id, editedPost));

  }

  @PostMapping("/comment")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<NewCommentResponse> addComment(@RequestBody NewCommentRequest newComment) {

    NewCommentResponse response = postService.addComment(newComment);

    Optional<Boolean> resultOptional = Optional.ofNullable(response.getResult());

    if (resultOptional.isPresent()) {
      return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(response);

  }

  @PostMapping("/post/like")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ResultResponse> like(@RequestBody RateRequest likeRequest) {

    return ResponseEntity.ok(postService.rate(likeRequest, 1));  // true = 1 = like false = 0 = dislike

  }

  @PostMapping("/post/dislike")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ResultResponse> dislike(@RequestBody RateRequest dislikeRequest) {

    return ResponseEntity.ok(postService.rate(dislikeRequest, 0));  // true = 1 = like false = 0 = dislike

  }

  @GetMapping("/post/moderation")
  @PreAuthorize("hasAuthority('user:moderate')")
  public ResponseEntity<ListPostResponse> getModerationPostList(
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "10") int limit,
      @RequestParam(required = false, defaultValue = "new") String status){

    return ResponseEntity.ok(moderationService.getModerationPostList(offset, limit, status));

  }

  @PostMapping("/moderation")
  @PreAuthorize("hasAuthority('user:moderate')")
  public ResponseEntity<ResultResponse> moderatePost(@RequestBody PostModerationRequest request) {

    return ResponseEntity.ok(moderationService.moderatePost(request));  // true = 1 = like false = 0 = dislike

  }

}