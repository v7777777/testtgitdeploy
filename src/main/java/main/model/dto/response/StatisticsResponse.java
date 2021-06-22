package main.model.dto.response;

import lombok.Data;

@Data
public class StatisticsResponse {

  private int postsCount;//21
  private int likesCount;//5
  private int dislikesCount;//10
  private int viewsCount;//27
  private long firstPublication; //3    дата первой публикации

}
