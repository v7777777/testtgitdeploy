package main.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.ChangePasswordRequest;
import main.model.dto.request.RestorePasswordRequest;
import main.model.dto.response.ResultResponse;
import main.model.entity.CaptchaCode;
import main.model.entity.User;
import main.repository.CaptchaCodeRepository;
import main.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

  private final JavaMailSender emailSender;
  private final UserRepository userRepository;
  private final CaptchaCodeRepository captchaCodeRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${appEmail.email}")
  public String email;

  @Value("${blog.title}")
  public String blogTitle;

  @Value("${delete.expiredCaptcha}")
  public String deleteExpiredCaptchaTime;


  public ResultResponse restore(RestorePasswordRequest restorePasswordRequest)
      throws MessagingException {

    ResultResponse response = new ResultResponse();

    Optional<User> userOptional = userRepository.findByEmail(restorePasswordRequest.getEmail());

    if (userOptional.isEmpty()) {
      response.setResult(false);
      return response;
    }

    User user = userOptional.get();

    String code = (RandomStringUtils.randomAlphabetic(45)).toLowerCase();
    user.setCode(code);
    userRepository.save(user);

    sendMessage(code, user);
    response.setResult(true);

    return response;

  }

  public ResultResponse changePassword(ChangePasswordRequest request){

    ResultResponse response = new ResultResponse();
    Map<String, String> errors = new HashMap<>();

    Optional<User> userOptional = checkErrors(request, errors);

    if(!errors.isEmpty()){
      response.setResult(false);
      response.setErrors(errors);
      return response;
    }

    User userToChangePassword = userOptional.get();
    userToChangePassword.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(userToChangePassword);

    response.setResult(true);

    return response;

  }

  private Optional<User> checkErrors(ChangePasswordRequest request, Map<String, String> errors){

    String password = request.getPassword();

    if (password.length() < 6) {
      errors.put("password", "Пароль короче 6-ти символов");
    }

    Optional<User> userOptional = userRepository.findByCode(request.getCode());

    if (userOptional.isEmpty()) {
      errors.put("code", "Ссылка для восстановления пароля устарела <a href = http://localhost:8080/login/restore-password />Запросить ссылку снова</a>");
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

    return userOptional;

  }

  private void sendMessage(String code, User user) throws MessagingException {

    //  <p> текст сообщения <a href="../../example/knob.html"(адрес ссылки) > текст ссылки </a> текст сообщения </p>

    String restoreLink =
        "<a href = http://localhost:8080/login/change-password/" + code + ">"
            + " http://localhost:8080/login/change-password/" + code + "</a>";

    MimeMessage mimeMessage = emailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

    helper.setFrom("DevPub");
    helper.setTo(user.getEmail());
    helper.setSubject("Ссылка на восстановление пароля на " + blogTitle);
    helper.setText("<p>Для восстановления пароля пройдите по ссылке " + restoreLink + "</p>", true);

    emailSender.send(mimeMessage);

  }



}
