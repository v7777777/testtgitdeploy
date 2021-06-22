package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingsRequest {

  @JsonProperty("MULTIUSER_MODE")
  private Boolean multiUserMode;
  @JsonProperty("POST_PREMODERATION")
  private Boolean postPremoderation;
  @JsonProperty("STATISTICS_IS_PUBLIC")
  private Boolean statisticsIsPublic;
}
