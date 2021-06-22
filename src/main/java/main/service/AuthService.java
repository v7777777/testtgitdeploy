package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.github.cage.token.RandomTokenGenerator;
import java.security.Principal;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.LoginRequest;
import main.model.dto.request.RegistrationRequest;
import main.model.dto.response.CaptchaResponse;
import main.model.dto.response.LoginResponse;
import main.model.dto.response.ResultResponse;
import main.model.entity.CaptchaCode;
import main.model.entity.GlobalSetting;
import main.model.entity.User;
import main.model.enums.SettingValue;
import main.repository.CaptchaCodeRepository;
import main.repository.GlobalSettingRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final CaptchaCodeRepository captchaCodeRepository;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final PostRepository postRepository;
  private final PasswordEncoder passwordEncoder;
  private final GlobalSettingRepository globalSettingRepository;

  @Value("${delete.expiredCaptcha}")
  public String deleteExpiredCaptchaTime;

  public CaptchaResponse captcha() {

    //Также метод должен удалять устаревшие капчи из таблицы. Время устаревания должно быть задано в
    //конфигурации приложения (по умолчанию, 1 час).

    int deleteTime = 60;

    if (!deleteExpiredCaptchaTime.isEmpty()) {
      deleteTime = Integer.parseInt(deleteExpiredCaptchaTime);
    }

    captchaCodeRepository.deleteExpiredCaptchas(deleteTime);

    CaptchaResponse captchaResponse = new CaptchaResponse();
    CaptchaCode captchaCode = new CaptchaCode();

    Cage cage = new GCage();

    // token generator to produce strings for the image

    String token = new RandomTokenGenerator(new Random(), 5, 1).next();
    byte[] imageByte = cage.draw(token); // Generate an image and return it in a byte array.

    String encodedString = Base64.getEncoder().encodeToString(imageByte);
    String encodedStringWithPrefix = "data:image/png;base64, " + encodedString;
    String secret = (RandomStringUtils.randomAlphanumeric(22)).toLowerCase();

    // ---------проверить на уникальность ?? !! сделать запрос цикл вайл ??

    captchaCode.setSecretCode(secret);
    captchaCode.setCode(token);
    captchaCode.setTime(Instant.now());

    captchaCodeRepository.save(captchaCode);

    captchaResponse.setSecret(secret);
    captchaResponse.setImage(encodedStringWithPrefix);

    return captchaResponse;
  }

  public ResultResponse register(RegistrationRequest request) {

    // после того как пользователь
    //вводит данные каптчи, отправляется форма содержащая текст-расшифровка каптчи пользователем и
    //secret. Сервис ищет по значению secret запись о каптче в таблице и сравнивает ввод пользователя
    //со значением поля code таблицы captcha_codes. На основе сравнения решается - каптча введена
    //верно или нет.

    // response captcha - код на рисунке / secret возвращает полученную строку
    // БД code - код на рисунке / secret_code - строка

    // удалить старые каптчи здесь тоже???? ---


    GlobalSetting multiUserMode = globalSettingRepository.findByCode("MULTIUSER_MODE");

    if(multiUserMode.getValue().equals(SettingValue.NO)){throw new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Регистрация закрыта");
    }

    ResultResponse resultResponse = new ResultResponse();

    Map<String, String> errors = checkRegistrationRequestForErrors(request);

    if(!errors.isEmpty()){
      resultResponse.setResult(false);
      resultResponse.setErrors(errors);
      return resultResponse;
    }

    User newUser = new User();
    newUser.setEmail(request.getEmail());
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    newUser.setName(request.getName());
    newUser.setModerator(false);
    newUser.setRegTime(Instant.now());
    userRepository.save(newUser);
    resultResponse.setResult(true);

    return resultResponse;
  }

  public LoginResponse login(LoginRequest loginRequest) {

    Authentication auth = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
            loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(auth);

    org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth
        .getPrincipal();

    return getLoginResponse(user.getUsername());

  }

  public LoginResponse check(Principal principal) {

    if (principal == null) {
      return new LoginResponse(false);
    }

    return getLoginResponse(principal.getName());
  }

  private LoginResponse getLoginResponse(String email) {

    User currentUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));

    int newPostsCount = currentUser.isModerator() ? postRepository.countNewPosts() : 0;

    LoginResponse loginResponse = new LoginResponse(currentUser, newPostsCount);
    loginResponse.setResult(true);

    return loginResponse;
  }

  private Map<String, String> checkRegistrationRequestForErrors(RegistrationRequest request) {

    Map<String, String> errors = new HashMap<>();

    String email = request.getEmail();
    Optional<User> userOptional = userRepository.findByEmail(email);

    if (!userOptional.isEmpty()) {
      errors.put("email","Этот e-mail уже зарегистрирован");
    }

    String name = request.getName();

    if (name.matches("\\s+") || name.isEmpty()) {
      errors.put("name", "Имя указано неверно");
    }

    String password = request.getPassword();

    if (password.length() < 6) {
      errors.put("password", "Пароль короче 6-ти символов");
    }

    captchaCodeRepository.deleteExpiredCaptchas(Integer.parseInt(deleteExpiredCaptchaTime));

    Optional<CaptchaCode> captchaCodeOptional = captchaCodeRepository
        .checkSecret(request.getCaptchaSecret());

    if (captchaCodeOptional.isEmpty()) {
      errors.put("captcha expired", "срок кода с картинки истек, обновите страницу и попробуйте еще раз");
    }
    else {

      CaptchaCode captchaCodeCurrent = captchaCodeOptional.get();

      if (!request.getCaptcha().equals(captchaCodeCurrent.getCode())) {
        errors.put("captcha", "Код с картинки введён неверно");
      }
    }

    return errors;
  }


}
