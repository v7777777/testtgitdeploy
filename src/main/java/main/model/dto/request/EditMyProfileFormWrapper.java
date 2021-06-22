package main.model.dto.request;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data

public class EditMyProfileFormWrapper {


  private MultipartFile photo;
  private boolean removePhoto;
  private String name;
  private String email;
  private String password;


}
