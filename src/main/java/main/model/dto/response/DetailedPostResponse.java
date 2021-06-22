package main.model.dto.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import main.model.entity.Post;

@Data
public class DetailedPostResponse {

  private int id;
  private long timestamp;
  private boolean isActive;
  private UserPostResponse user;
  private String title;
  private String text;
  private int likeCount;
  private int dislikeCount;
  private int viewCount;
  private List<CommentDetailedPostResponse> comments;
  private List<String> tags;

  public DetailedPostResponse(Post p) {

    UserPostResponse userResponse = new UserPostResponse();
    userResponse.setId(p.getUser().getId());
    userResponse.setName(p.getUser().getName());

    this.id = p.getId();
    this.timestamp = p.getTime().getEpochSecond();
    this.isActive = p.isActive();
    this.user = userResponse;
    this.title = p.getTitle();
    this.text = p.getText();
    this.viewCount = p.getViewCount();
    this.likeCount = calculateLikes(p);
    this.dislikeCount = calculateDislikes(p);
    this.comments = getCommentsList(p);
    this.tags = getTagsList(p);
  }

  private int calculateDislikes(Post p) {

    return (int) p.getPostVotes().stream().filter(vote -> vote.isValue() == false).count();
  }

  private int calculateLikes(Post p) {

    return (int) p.getPostVotes().stream().filter(vote -> vote.isValue() == true).count();
  }

  private List<CommentDetailedPostResponse> getCommentsList(Post p) {

    List<CommentDetailedPostResponse> comments = new ArrayList<>();

    p.getPostComments().forEach(postComment -> {
      {

        CommentDetailedPostResponse commentDetailedPostResponse = new CommentDetailedPostResponse();
        UserDetailedPostCommentResponse userDetailedPostCommentResponse = new UserDetailedPostCommentResponse();

        userDetailedPostCommentResponse.setId(postComment.getUser().getId());
        userDetailedPostCommentResponse.setName(postComment.getUser().getName());
        userDetailedPostCommentResponse.setPhoto(postComment.getUser().getPhoto());

        commentDetailedPostResponse.setId(postComment.getId());
        commentDetailedPostResponse.setText(postComment.getText());
        commentDetailedPostResponse.setUser(userDetailedPostCommentResponse);

        comments.add(commentDetailedPostResponse);

      }
    });

    return comments;

  }

  private List<String> getTagsList(Post p) {

    List<String> tags = new ArrayList<>();

    p.getTags().forEach(tag -> tags.add(tag.getName()));

    return tags;


  }

}
