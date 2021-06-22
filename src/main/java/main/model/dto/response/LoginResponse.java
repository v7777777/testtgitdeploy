package main.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.model.entity.User;

@Data
public class LoginResponse {

  private boolean result;

  @JsonProperty("user")
  @JsonInclude(Include.NON_NULL)
  private UserLoginResponse userLoginResponse;

  public LoginResponse(User currentUser, int newPosts) {

    this.result = true;

    UserLoginResponse userLoginResponse = new UserLoginResponse();
    userLoginResponse.setId(currentUser.getId());
    userLoginResponse.setEmail(currentUser.getEmail());
    userLoginResponse.setName(currentUser.getName());
    userLoginResponse.setPhoto(currentUser.getPhoto());
    userLoginResponse.setModeration(currentUser.isModerator());
    userLoginResponse.setSettings(currentUser.isModerator() ? true : false);
    userLoginResponse.setModerationCount(newPosts);
    this.userLoginResponse = userLoginResponse;

  }

  public LoginResponse(boolean result) {
    this.result = result;
  }


}
