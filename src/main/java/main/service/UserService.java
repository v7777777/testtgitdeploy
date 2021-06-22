package main.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.EditMyProfileFormWrapper;
import main.model.dto.request.EditMyProfileRequest;
import main.model.dto.response.ResultResponse;
import main.model.entity.User;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ImageService imageService;

  @Value("${upload.path}")
  public String uploadPath;

  @Value("${upload.url}")
  public String uploadUrl;

  public ResultResponse editMyProfile(
      EditMyProfileFormWrapper form) throws IOException {

    ResultResponse response = new ResultResponse();
    Map<String, String> errors = new HashMap<>();

    // photo всегда не null здесь
    Optional<String> nameOptional = Optional
        .ofNullable(form.getName()); // фронт отправляет пустой запрос
    Optional<String> emailOptional = Optional.ofNullable(form.getEmail());
    Optional<String> passwordOptional = Optional.ofNullable(form.getPassword());

    User currentUser = getUserFromAuthentication();

    checkAndSetNameEmailPassword(nameOptional, emailOptional, passwordOptional, currentUser, errors);

    MultipartFile photo = form.getPhoto();

    if (photo.getSize() > 1048576) {
      errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
    }

    if (!errors.isEmpty()) {
      response.setResult(false);
      response.setErrors(errors);
      return response;
    }

    String originalFilename = photo.getOriginalFilename();
    String storeName = UUID.randomUUID().toString() + "_" + originalFilename;
    File destination = new File(uploadPath + "/" + storeName);
    int originalFileLastDot = (originalFilename != null) ? originalFilename.lastIndexOf(".") : -1;
    String type = (originalFilename != null) ? originalFilename.substring(originalFileLastDot + 1) : "";

    imageService.resizeAndSaveImage(photo, type, destination, "profile"); // 36*36

    currentUser.setPhoto(uploadUrl
        + storeName);  // url запрос на эндпоинт /img/ ищет в папке upload --> ресупс хендлер  / на сервере в корне upload

    userRepository.save(currentUser);
    response.setResult(true);

    return response;
  }

  public ResultResponse editMyProfile(
      EditMyProfileRequest request) {

    ResultResponse response = new ResultResponse();
    Map<String, String> errors = new HashMap<>();
    User currentUser = getUserFromAuthentication();

    Optional<String> nameOptional = Optional
        .ofNullable(request.getName()); // фронт отправляет пустой запрос
    Optional<String> emailOptional = Optional.ofNullable(request.getEmail());
    Optional<String> passwordOptional = Optional.ofNullable(request.getPassword());

    checkAndSetNameEmailPassword(nameOptional, emailOptional, passwordOptional, currentUser, errors);

    if (!errors.isEmpty()) {
      response.setResult(false);
      response.setErrors(errors);
      return response;
    }

    Optional<Boolean> isRemovePhotoOptional = Optional.ofNullable(request.isRemovePhoto());

    if(isRemovePhotoOptional.isPresent()) {

      Optional <String> currentPhotoOptional = Optional.of(currentUser.getPhoto());

      if (isRemovePhotoOptional.get() && currentPhotoOptional.isPresent()) {
        currentUser.setPhoto(null);
      }
    }

    userRepository.save(currentUser);
    response.setResult(true);

    return response;
  }

  // if call only from  @PreAuthorize method no exception thrown
  protected User getUserFromAuthentication() {

    Authentication auth =  SecurityContextHolder
        .getContext().getAuthentication();

    if(auth instanceof AnonymousAuthenticationToken) {throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not authorized");
    }

    String email = ((org.springframework.security.core.userdetails.User) auth
        .getPrincipal())
        .getUsername();

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

    return user;
  }

  private void checkAndSetNameEmailPassword(
      Optional<String> nameOptional,
      Optional<String> emailOptional,
      Optional<String> passwordOptional,
      User currentUser,
      Map<String, String> errors) {

    if (nameOptional.isPresent()) {

      String name = nameOptional.get();

      if (name.matches("\\s+")) {
        errors.put("name", "Имя не указано");
      }

      currentUser.setName(name);
    }
    if (emailOptional.isPresent()) {

      String newEmail = emailOptional.get();

      if (!newEmail.equals(currentUser.getEmail())) {

        Optional<User> userAlreadyExists = userRepository.findByEmail(newEmail);

        if (userAlreadyExists.isPresent()) {
          errors.put("email", "Этот e-mail уже зарегистрирован");
          // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this email is already registered");
        } else {
          currentUser.setEmail(newEmail);
        }
      }
      // else ничего не делать тк пришел старый email
    }
    if (passwordOptional.isPresent()) {

      String newPassword = passwordOptional.get();

      if (newPassword.length() < 6) {
        errors.put("password", "Пароль короче 6-ти символов");
      } else {
        currentUser.setPassword(passwordEncoder.encode(newPassword));
      }
    }

  }


}
