package main.model.dto.response.listResponses;

import java.util.List;
import lombok.Data;
import main.model.dto.response.PostResponse;

@Data
public class ListPostResponse {

  private long count;

  private List<PostResponse> posts;

  public ListPostResponse(List<PostResponse> posts) {
    this.posts = posts;

  }

}
