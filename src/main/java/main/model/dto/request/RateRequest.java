package main.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RateRequest {
@JsonProperty("post_id")
private int id;

}
