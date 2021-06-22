package main.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import main.model.dto.DateAmountView;
import main.model.dto.request.NewCommentRequest;
import main.model.dto.request.NewPostRequest;
import main.model.dto.request.RateRequest;
import main.model.dto.response.CalendarResponse;
import main.model.dto.response.DetailedPostResponse;
import main.model.dto.response.NewCommentResponse;
import main.model.dto.response.PostResponse;
import main.model.dto.response.ResultResponse;
import main.model.dto.response.listResponses.ListPostResponse;
import main.model.entity.GlobalSetting;
import main.model.entity.Post;
import main.model.entity.PostComment;
import main.model.entity.PostVote;
import main.model.entity.Tag;
import main.model.entity.User;
import main.model.enums.ModerationStatusCode;
import main.model.enums.SettingValue;
import main.repository.GlobalSettingRepository;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import main.repository.TagRepository;
import main.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postsRepository;
  private final UserRepository userRepository;
  private final TagRepository tagRepository;
  private final PostCommentRepository postCommentRepository;
  private final PostVoteRepository postVoteRepository;
  private final UserService userService;
  private final GlobalSettingRepository globalSettingRepository;


  public ListPostResponse getPosts(int offset, int limit, String mode) {

    Pageable pageable;
    int page = offset / limit;
    List<PostResponse> postsResponse = new ArrayList<>();
    ListPostResponse listPostResponse = new ListPostResponse(postsResponse);

    if (mode.equals("recent")) {

      pageable = PageRequest.of(page, limit, Sort.by("time").descending());

      Page<Post> posts = postsRepository.findAllActive(pageable);

      posts.forEach(p -> postsResponse.add(new PostResponse(p)));

      listPostResponse.setCount(posts.getTotalElements());

    } else if (mode.equals("popular")) {
      pageable = PageRequest.of(page, limit);

      Page<Post> posts = postsRepository.findAllByCommentsAmount(pageable);

      posts.forEach(p -> postsResponse.add(new PostResponse(p)));

      listPostResponse.setCount(posts.getTotalElements());
    } else if (mode.equals("best")) {

      pageable = PageRequest.of(page, limit);

      Page<Post> posts = postsRepository.findAllByLikesAmount(pageable); //

      listPostResponse.setCount(posts.getTotalElements()); //

      posts.forEach(p -> postsResponse.add(new PostResponse(p)));

    } else if (mode.equals("early")) {

      pageable = PageRequest.of(page, limit, Sort.by("time").ascending());

      Page<Post> posts = postsRepository.findAllActive(pageable);

      posts.forEach(p -> postsResponse.add(new PostResponse(p)));

      listPostResponse.setCount(posts.getTotalElements());
    }

    return listPostResponse;
  }

  public ListPostResponse searchPosts(int offset, int limit, String query) {

    Pageable pageable;
    int page = offset / limit;

    List<PostResponse> postsResponse = new ArrayList<>();
    ListPostResponse listPostResponse = new ListPostResponse(postsResponse);

    //случае, если запрос
    //пустой или содержит только пробелы, метод должен выводить все посты (запрос GET /api/post c
    //параметров mode=recent)

    if (query.equals("recent") || query.matches("\\s*")) {

      pageable = PageRequest.of(page, limit, Sort.by("time").descending());

      Page<Post> posts = postsRepository.findAllActive(pageable);

      posts.forEach(p -> postsResponse.add(new PostResponse(p)));

      listPostResponse.setCount(posts.getTotalElements());
    } else {

      pageable = PageRequest.of(page, limit);
      query = "%" + query + "%";
      Page<Post> posts = postsRepository.findByTextOrTitle(query, pageable);
      posts.forEach(p -> postsResponse.add(new PostResponse(p)));
      listPostResponse.setCount(posts.getTotalElements());
    }

    return listPostResponse;
  }

  public CalendarResponse calendar(String year) {

    List<Integer> yearsResponse = postsRepository.getYearsWithActivePosts();

    CalendarResponse calendarResponse = new CalendarResponse();

    HashMap<LocalDate, Integer> postsResponse = new HashMap<>();

    int currentYear;

    //если не передан - возвращать за текущий год

    if (year == null) {

      currentYear = LocalDate.now().getYear();
    } else {
      currentYear = Integer.parseInt(year);
    }

    List<DateAmountView> result = postsRepository.getStatisticsPostsFromYear(currentYear);

    result.forEach(r -> postsResponse.put(r.getTime(), r.getCount()));

    calendarResponse.setPosts(postsResponse);
    calendarResponse.setYears(yearsResponse);

    return calendarResponse;
  }

  public ListPostResponse getPostsByDate(int offset, int limit, String date) {

    // date - дата в формате "2019-10-15"

    int page = offset / limit;
    Pageable pageable = PageRequest.of(page, limit, Sort.by("time").descending());

    List<PostResponse> postsResponse = new ArrayList<>();
    ListPostResponse listPostResponse = new ListPostResponse(postsResponse);

    Page<Post> postsByDate = postsRepository.findAllActivePostsByDate(pageable, date);

    postsByDate.forEach(p -> postsResponse.add(new PostResponse(p)));

    listPostResponse.setCount(postsByDate.getTotalElements());

    return listPostResponse;

  }

  public ListPostResponse getPostsByTag(int offset, int limit, String tag) {

    // date - дата в формате "2019-10-15"

    int page = offset / limit;
    Pageable pageable = PageRequest.of(page, limit);
    ;
    List<PostResponse> postsResponse = new ArrayList<>();
    ListPostResponse listPostResponse = new ListPostResponse(postsResponse);

    Page<Post> postsByTag = postsRepository.findAllActivePostsByTag(pageable, tag);

    postsByTag.forEach(p -> postsResponse.add(new PostResponse(p)));

    listPostResponse.setCount(postsByTag.getTotalElements());

    return listPostResponse;

  }

  public DetailedPostResponse getPostById(int id) {

    // ЕСЛИ USER АУТЕНТИФИЦИРОВАН НАХОДИТЬ показывать ВСЕ СВОИ ПОСТЫ
    // ТЕ ПОСТ АВТОР АЙДИ РАВЕН АУТЕНТИФИЦИРОВАН АЙДИ

    Post post;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAuthenticated = !(auth instanceof AnonymousAuthenticationToken);

    if (isAuthenticated) {

      User currentUser = userService.getUserFromAuthentication();

      String email = currentUser.getEmail();

      Optional<Post> postOptional = postsRepository.findAnyPostById(id);

      if (postOptional.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
      }

      post = postOptional.get();

      // если автор искомого поста авторизован то показывать пост с любым статусом

      boolean isPostAuthor = post.getUser().getEmail().equals(email);
      boolean isModerator = currentUser.isModerator();

      // не автору не показывать не активные посты
      // но модератору показывать

      if ((!isPostAuthor && (!post.isActive() ||
          !post.getModerationStatus().equals(ModerationStatusCode.ACCEPTED)  ||
              post.getTime().isAfter(Instant.now()))) &&
          !isModerator) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not available");
      }

      // увеличение просмотров поста

      if (!isPostAuthor && !isModerator) {
        post.setViewCount(post.getViewCount() + 1);
        postsRepository.save(post);
      }

    }
    // ЕСЛИ НЕ АУТЕНТИФИЦИРОВАН НАХОДИТЬ просто ВСЕ активные ПОСТЫ
    else {

      Optional<Post> postOptional = postsRepository.findActivePostById(id);

      if (postOptional.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
      }
      post = postOptional.get();
      post.setViewCount(
          post.getViewCount() + 1);  // увеличиваем просмотры если пользователь не авторизован ?????
      postsRepository.save(post);
    }

    DetailedPostResponse detailedPostResponse = new DetailedPostResponse(post);

    return detailedPostResponse;
  }

  public ListPostResponse getMyPosts(int offset, int limit, String status) {

    int page = offset / limit;
    Pageable pageable = PageRequest.of(page, limit);

    List<PostResponse> postsResponse = new ArrayList<>();
    ListPostResponse listPostResponse = new ListPostResponse(postsResponse);

    org.springframework.security.core.userdetails.User securityUser =
        (org.springframework.security.core.userdetails.User) SecurityContextHolder
            .getContext()
            .getAuthentication().getPrincipal();

    // не нужно тк аннотациия на контроллере
//    if (!(securityUser instanceof org.springframework.security.core.userdetails.User)) {
//
//      throw new AuthenticationCredentialsNotFoundException("please authorize");
//
//    }

    User authUser = userRepository.findByEmail(
        (securityUser).getUsername()).orElseThrow(() -> new UsernameNotFoundException("not found"));

    int myId = authUser.getId();

    if (status.equals("published")) {
      Page<Post> myPublishedPosts = postsRepository.findPublishedPostsById(myId, pageable);
      myPublishedPosts.forEach(p -> postsResponse.add(new PostResponse(p)));
      listPostResponse.setCount(myPublishedPosts.getTotalElements());
    } else if (status.equals("inactive")) {
      Page<Post> myInactivePosts = postsRepository.findInactivePostsById(myId, pageable);
      myInactivePosts.forEach(p -> postsResponse.add(new PostResponse(p)));
      listPostResponse.setCount(myInactivePosts.getTotalElements());
    } else if (status.equals("pending")) {
      Page<Post> myPendingPosts = postsRepository.findPendingPostsById(myId, pageable);
      myPendingPosts.forEach(p -> postsResponse.add(new PostResponse(p)));
      listPostResponse.setCount(myPendingPosts.getTotalElements());
    } else if (status.equals("declined")) {
      Page<Post> myDeclinedPosts = postsRepository.findDeclinedPostsById(myId, pageable);
      myDeclinedPosts.forEach(p -> postsResponse.add(new PostResponse(p)));
      listPostResponse.setCount(myDeclinedPosts.getTotalElements());
    }
    return listPostResponse;
  }

  public ResultResponse addPost(NewPostRequest newPostRequest) {

    ResultResponse newPostResponse = new ResultResponse();

    Map<String, String> errors =  checkNewPostForErrors(newPostRequest);

    if (!errors.isEmpty()) {
      newPostResponse.setErrors(errors);
      newPostResponse.setResult(false);
      return newPostResponse;
    }

    Post newPost = new Post();

    newPost.merge(newPostRequest);

    GlobalSetting postPremoderation = globalSettingRepository.findByCode("POST_PREMODERATION");

    if(postPremoderation.getValue().equals(SettingValue.YES)){
      newPost.setModerationStatus(ModerationStatusCode.NEW);
    }
    else if(postPremoderation.getValue().equals(SettingValue.NO)){
      newPost.setModerationStatus(ModerationStatusCode.ACCEPTED);
    }

    List<Tag> tags = getTagsListFromRequest(newPostRequest.getTags());

    newPost.setTags(tags);

    User author = userService.getUserFromAuthentication();

    newPost.setUser(author);

    postsRepository.save(newPost);

    newPostResponse.setResult(true);

    return newPostResponse;
  }

  public ResultResponse editPost(int id, NewPostRequest editedPostRequest) {

    ResultResponse editedPostResponse = new ResultResponse();
    Post postToEdit;

    Optional<Post> postOptional = postsRepository.findAnyPostById(id);

    Map<String, String> errors =  checkNewPostForErrors(editedPostRequest);

    if (postOptional.isEmpty()) {
      errors.put("not found", "post doesn't exist");
    }

    if (!errors.isEmpty()) {
      editedPostResponse.setErrors(errors);
      editedPostResponse.setResult(false);
      return editedPostResponse;
    }

    Instant time = Instant.ofEpochSecond(editedPostRequest.getTimestamp());

    if (time.isBefore(Instant.now())) {
      time = Instant.now();
    }

    postToEdit = postOptional.get();

    postToEdit.merge(editedPostRequest);

    List<Tag> tags = getTagsListFromRequest(editedPostRequest.getTags());
    postToEdit.setTags(tags);

    // Пост должен сохраняться со статусом модерации NEW, если его изменил автор, и статус модерации не
    // должен изменяться, если его изменил модератор.

    // кто авторизован и автор поста который меняется

    String email = ((org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication()
        .getPrincipal())
        .getUsername();

    if (postToEdit.getUser().getEmail().equals(email)) {
      postToEdit.setModerationStatus(ModerationStatusCode.NEW);
    }

    postsRepository.save(postToEdit);
    editedPostResponse.setResult(true);
    return editedPostResponse;

  }

  public NewCommentResponse addComment(NewCommentRequest newCommentRequest) {

    NewCommentResponse newCommentResponse = new NewCommentResponse();
    Map<String, String> errors = new HashMap<>();

    // any тк если нужно оставить комент к своему посту
    // нельзя оставить комент к несвоему неактивному посту тк он не отображается нигде

    Post commentedPost = postsRepository
        .findAnyPostById(newCommentRequest.getPostId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "post not found"));

    Optional<PostComment> parentCommentOptional = Optional.empty();

   // int parentId; null is 0 no npe

    if (newCommentRequest.getParentId() != 0) {

      PostComment parentComment = postCommentRepository
          .findById(newCommentRequest.getParentId())
          .orElseThrow(
              () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "comment not found"));

      parentCommentOptional = Optional.of(parentComment);
    }

    if (newCommentRequest.getText().isEmpty()) {
      errors.put("text", "текст комментария");
      newCommentResponse.setErrors(errors);
      newCommentResponse.setResult(false);
      return newCommentResponse;
    }

    PostComment commentToAdd = new PostComment();
    commentToAdd.setPost(commentedPost);
    if (!parentCommentOptional.isEmpty()) {
      commentToAdd.setPostComment(parentCommentOptional.get());
    }

    commentToAdd.setText(newCommentRequest.getText());


    commentToAdd.setTime(Instant.now());
    User author = userService.getUserFromAuthentication();
    commentToAdd.setUser(author);

    commentToAdd = postCommentRepository.save(commentToAdd);
    newCommentResponse.setId(commentToAdd.getId());
    return newCommentResponse;
  }

  private Map<String, String> checkNewPostForErrors(NewPostRequest newPostRequest) {

    Map<String, String> errors =  new HashMap<>();

    if (newPostRequest.getTitle().isEmpty()) {
      errors.put("title", "title is empty");
    }
    if (!newPostRequest.getTitle().isEmpty() && newPostRequest.getTitle().length() < 3) {
      errors.put("title", "title is too short");
    }
    if (!newPostRequest.getTitle().isEmpty() && newPostRequest.getTitle().length() > 255) {
      errors.put("title", "title is too long");
    }
    if (newPostRequest.getText().isEmpty()) {
      errors.put("text", "text is empty");
    }
    if (!newPostRequest.getText().isEmpty() && newPostRequest.getText().length() < 50) {
      errors.put("text", "text is too short");
    }

    return errors;
  }

  private List<Tag> getTagsListFromRequest(List<String> tagsFromRequest) {

    List<Tag> tags = new ArrayList<>();

    tagsFromRequest.forEach(t -> {
      {

        Tag tag;

        Optional<Tag> tagOptional = tagRepository.findByName(t);

        if (tagOptional.isEmpty()) {
          tag = new Tag();
          tag.setName(t);
          tagRepository.save(tag);
        } else {
          tag = tagOptional.get();
        }

        tags.add(tag);

      }
    });

    return tags;

  }

  public ResultResponse rate(RateRequest rateRequest, int value) {

    ResultResponse rateResponse = new ResultResponse();

    User authUser = userService.getUserFromAuthentication();

    Post postToRate = postsRepository
        .findAnyPostById(rateRequest.getId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "post not found"));

    Optional<PostVote> postVoteOptional = postVoteRepository.findByUserAndPostIds(authUser.getId(), postToRate.getId());

    if(postVoteOptional.isEmpty()){

      PostVote postVote = new PostVote();
      postVote.setValue(value == 1 ? true : false); // true = 1 = like false = 0 = dislike
      postVote.setUser(authUser);
      postVote.setPost(postToRate);
      postVote.setTime(Instant.now());
      postVoteRepository.save(postVote);
      rateResponse.setResult(true);
      return rateResponse;
    }

    PostVote existing = postVoteOptional.get();

    if(existing.isValue() && value == 1) {rateResponse.setResult(false);}
    if(!existing.isValue() && value == 0) {rateResponse.setResult(false);}
    if(!existing.isValue() && value == 1) {
      existing.setValue(true);
      postVoteRepository.save(existing);
      rateResponse.setResult(true);
    }
    if(existing.isValue() && value == 0) {
      existing.setValue(false);
      postVoteRepository.save(existing);
      rateResponse.setResult(true);
    }

    return rateResponse;

  }

  // true = 1 = like false = 0 = dislike


}
