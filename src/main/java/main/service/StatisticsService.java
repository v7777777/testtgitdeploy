package main.service;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import main.model.dto.response.StatisticsResponse;
import main.model.entity.GlobalSetting;
import main.model.entity.Post;
import main.model.entity.User;
import main.model.enums.SettingValue;
import main.repository.GlobalSettingRepository;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final GlobalSettingRepository globalSettingRepository;
  private final PostRepository postRepository;
  private final UserService userService;
  private final PostVoteRepository postVoteRepository;

  public StatisticsResponse getAllStatistics(){
    StatisticsResponse response = new StatisticsResponse();

    GlobalSetting gsStatisticsIsPublic = globalSettingRepository.findByCode("STATISTICS_IS_PUBLIC");

    if(gsStatisticsIsPublic.getValue().equals(SettingValue.NO)){

       User currentUser = userService.getUserFromAuthentication(); // если не авторизован userService кидает ResponseStatusException(HttpStatus.UNAUTHORIZED, "not authorized");

      if(!currentUser.isModerator()){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized)");
    }}

    // статистика по всем постам блога
    Optional<Integer> allPosts = postRepository.countAllPosts();
    response.setPostsCount(allPosts.isPresent() ? allPosts .get() : 0);
    Optional<Integer> likesCount = postVoteRepository.countLikes();
    response.setLikesCount(likesCount.isPresent() ? likesCount .get() : 0);
    Optional<Integer> dislikesCount = postVoteRepository.countDislikes();
    response.setDislikesCount(dislikesCount.isPresent() ? dislikesCount.get() : 0);
    Optional<Integer> allPostsViews = postRepository.countAllPostsViews();
    response.setViewsCount(allPostsViews.isPresent() ? allPostsViews.get() : 0);
    Optional<Post> postOptional = postRepository.getFirstPublication();
    response.setFirstPublication(postOptional.isPresent() ?  ((postOptional.get().getTime().getEpochSecond())) : Instant.now().getEpochSecond());

    return response;
  }

  // публикаций, у который он является автором и доступные для чтения

  public StatisticsResponse getMyStatistics(){
    StatisticsResponse response = new StatisticsResponse();
    User currentUser = userService.getUserFromAuthentication();
    int userId = currentUser.getId();
    Optional<Integer> postsCount = postRepository.countMyPublishedPosts(userId);
    response.setPostsCount(postsCount.isPresent() ? postsCount.get() : 0);
    Optional<Integer> likesCount =postVoteRepository.countMyPubsLikes(userId);
    response.setLikesCount(likesCount.isPresent() ? likesCount.get() : 0);
    Optional<Integer> dislikesCount = postVoteRepository.countMyPubsDislikes(userId);
    response.setDislikesCount(dislikesCount.isPresent() ? dislikesCount.get() : 0);
    Optional<Integer> viewsCount = postRepository.countAllMyPostsViews(userId);
    response.setViewsCount(viewsCount.isPresent() ? viewsCount.get() : 0);
    Optional<Post> postOptional = postRepository.getMyFirstPublication(userId);
    response.setFirstPublication(postOptional.isPresent() ?  ((postOptional.get().getTime().getEpochSecond())) : 0);

    return response;
  }

}
