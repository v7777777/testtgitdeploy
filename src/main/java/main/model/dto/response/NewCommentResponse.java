package main.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;
import lombok.Data;

@Data
public class NewCommentResponse {

  @JsonInclude(Include.NON_NULL)
  private Integer id = null;
  @JsonInclude(Include.NON_NULL)
  private Map<String, String> errors;
  @JsonInclude(Include.NON_NULL)
  private Boolean result = null;

}
