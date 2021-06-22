package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class NewPostRequest {

  private long timestamp;
  @JsonProperty("active")
  private boolean isActive;
  private String title;
  private List<String> tags;
  private String text;

}
