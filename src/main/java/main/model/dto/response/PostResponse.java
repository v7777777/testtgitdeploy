package main.model.dto.response;

import lombok.Data;
import main.model.entity.Post;

@Data
public class PostResponse {

  private int id;
  private long timestamp;
  private UserPostResponse user;
  private String title;
  private String announce;
  private int likeCount;
  private int dislikeCount;
  private int commentCount;
  private int viewCount;

  public PostResponse(Post p) {

    UserPostResponse userResponse = new UserPostResponse();
    userResponse.setId(p.getUser().getId());
    userResponse.setName(p.getUser().getName());

    this.id = p.getId();
    this.timestamp = p.getTime().getEpochSecond();
    this.user = userResponse;
    this.title = p.getTitle();
    this.commentCount = p.getPostComments().size();
    this.viewCount = p.getViewCount();

    this.announce = getAnnounce(p.getText());
    this.likeCount = calculateLikes(p);
    this.dislikeCount = calculateDislikes(p);
  }

  private int calculateDislikes(Post p) {

    return (int) p.getPostVotes().stream().filter(vote -> vote.isValue() == false).count();
  }

  private int calculateLikes(Post p) {

    return (int) p.getPostVotes().stream().filter(vote -> vote.isValue() == true).count();
  }

  private String getAnnounce(String postText) {

    // длина не более 150 символов, все HTML теги должны быть удалены
    //конце полученной строки добавить троеточие ...

    String shortenString = postText.replaceAll("\\<.*?\\>", "");

    if (shortenString.length() > 150) {
      shortenString = shortenString.substring(0, 150);
      int lastSpaceIndex = shortenString.lastIndexOf(" ");
      shortenString = shortenString.substring(0, lastSpaceIndex) + " ...";
    }

    shortenString = shortenString.replaceAll("&ensp;", " ");
    shortenString = shortenString.replaceAll("&emsp;", " ");
    shortenString = shortenString.replaceAll("&nbsp;", " ");
    return shortenString;
  }
}
