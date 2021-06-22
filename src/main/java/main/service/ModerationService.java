package main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.PostModerationRequest;
import main.model.dto.response.PostResponse;
import main.model.dto.response.ResultResponse;
import main.model.dto.response.listResponses.ListPostResponse;
import main.model.entity.Post;
import main.model.entity.User;
import main.model.enums.ModerationStatusCode;
import main.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

  private final UserService userService;
  private final PostRepository postsRepository;

  public ListPostResponse getModerationPostList(int offset, int limit, String status){

//    status - статус модерации:
//    new - новые, необходима модерация
//    declined - отклонённые мной
//    accepted - утверждённые мной

    int page = offset / limit;
    Pageable pageable = PageRequest.of(page, limit, Sort.by("time").descending());
    List<PostResponse> postsResponse = new ArrayList<>();
    ListPostResponse listPostResponse = new ListPostResponse(postsResponse);

    if(status.equals("new")) {

    // Считаются посты имеющие статус NEW и не проверерны модератором.

    Page<Post> allPendingPosts = postsRepository.getAllPendingPosts(pageable);

    allPendingPosts.forEach(p -> postsResponse.add(new PostResponse(p)));

    listPostResponse.setCount(allPendingPosts.getTotalElements());
    }
    else if ( status.equals("declined")){

      User currentModerator = userService.getUserFromAuthentication();
      int moderatorId = currentModerator.getId();

      Page<Post> allDeclinedByMePosts = postsRepository.getAllDeclinedByMePosts(moderatorId, pageable);

      allDeclinedByMePosts.forEach(p -> postsResponse.add(new PostResponse(p)));

      listPostResponse.setCount(allDeclinedByMePosts.getTotalElements());
    }
    else if ( status.equals("accepted")){

      User currentModerator = userService.getUserFromAuthentication();
      int moderatorId = currentModerator.getId();

      Page<Post> allAcceptedByMePosts = postsRepository.getAllAcceptedByMePosts(moderatorId, pageable);

      allAcceptedByMePosts.forEach(p -> postsResponse.add(new PostResponse(p)));

      listPostResponse.setCount(allAcceptedByMePosts.getTotalElements());
    }

    return listPostResponse;

  }

  public ResultResponse moderatePost (PostModerationRequest request){
    ResultResponse response = new ResultResponse();

    Optional<Post> postToModerateOptional = postsRepository.findAnyPostById(request.getPostId());

   if(postToModerateOptional.isEmpty()) {
      response.setResult(false);
      return response;
    }

    Post postToModerate= postToModerateOptional.get();
    User currentModerator = userService.getUserFromAuthentication();

    postToModerate.setModerationStatus(getModerationStatusCode(request.getDecision()));
    postToModerate.setModerator(currentModerator);
    postsRepository.save(postToModerate);

    response.setResult(true);

    return response;

  }

  private ModerationStatusCode getModerationStatusCode(String decision){
    if(decision.equals("accept")){return ModerationStatusCode.ACCEPTED; }
    else if(decision.equals("decline")){return ModerationStatusCode.DECLINED;}
    return null;
  }

}
