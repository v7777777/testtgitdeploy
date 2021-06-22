package main.model.dto.request;

import lombok.Data;

@Data
public class EditMyProfileRequest {


  private String email;
  private String name;
  private String password;
  private String photo;
  private boolean removePhoto;

}
