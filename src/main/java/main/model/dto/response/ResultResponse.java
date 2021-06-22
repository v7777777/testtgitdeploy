package main.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Map;
import lombok.Data;

@Data
public class ResultResponse {

  private boolean result;

  @JsonInclude(Include.NON_NULL)
  private Map<String, String> errors;

}
