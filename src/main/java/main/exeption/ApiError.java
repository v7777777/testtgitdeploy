package main.exeption;

import java.io.Serializable;
import lombok.Data;

@Data
public class ApiError implements Serializable {

  public ApiError(String message) {
    this.message = message;
  }

  private String message;

}
