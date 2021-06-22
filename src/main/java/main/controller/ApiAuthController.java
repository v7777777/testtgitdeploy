package main.controller;

import java.security.Principal;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.ChangePasswordRequest;
import main.model.dto.request.LoginRequest;
import main.model.dto.request.RegistrationRequest;
import main.model.dto.request.RestorePasswordRequest;
import main.model.dto.response.CaptchaResponse;
import main.model.dto.response.LoginResponse;
import main.model.dto.response.ResultResponse;
import main.service.AuthService;
import main.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

  private final AuthService authService;
  private final PasswordService passwordService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

    return ResponseEntity.ok(authService.login(loginRequest));

  }

  @GetMapping("/logout")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<LoginResponse> logout() {

    SecurityContextHolder.getContext().setAuthentication(null);

    return ResponseEntity.ok(new LoginResponse(true));
  }

  @GetMapping("/check")
  public ResponseEntity<LoginResponse> check(Principal principal) {

    return ResponseEntity.ok(authService.check(principal));

  }

  @GetMapping("/captcha")
  public ResponseEntity<CaptchaResponse> captcha() {

    return ResponseEntity.ok(authService.captcha());

  }

  @PostMapping("/register")
  public ResponseEntity<ResultResponse> register(
      @RequestBody RegistrationRequest registrationRequest) {

    return ResponseEntity.ok(authService.register(registrationRequest));

  }

  @PostMapping("/restore")
  public ResponseEntity<ResultResponse> restorePassword(
      @RequestBody RestorePasswordRequest email) throws MessagingException {

    return ResponseEntity.ok(passwordService.restore(email));
  }

  @PostMapping("/password")
  public ResponseEntity<ResultResponse> changePassword(
      @RequestBody ChangePasswordRequest request)  {

    return ResponseEntity.ok(passwordService.changePassword(request));

  }
}


