package main.controller;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import main.model.dto.request.EditMyProfileFormWrapper;
import main.model.dto.request.EditMyProfileRequest;
import main.model.dto.request.SettingsRequest;
import main.model.dto.response.InitResponse;
import main.model.dto.response.ResultResponse;
import main.model.dto.response.SettingsResponse;
import main.model.dto.response.StatisticsResponse;
import main.service.ImageService;
import main.service.SettingService;
import main.service.StatisticsService;
import main.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

  private final InitResponse initResponse;
  private final SettingService settingService;
  private final UserService userService;
  private final ImageService imageService;
  private final StatisticsService statisticsService;

  @GetMapping("init")
  public ResponseEntity<InitResponse> init() {

    return ResponseEntity.ok(initResponse);

  }

  @GetMapping("/settings")
  public ResponseEntity<SettingsResponse> settings() {

    return ResponseEntity.ok(settingService.getSettings());

  }

  @RequestMapping(value = "/profile/my", method = RequestMethod.POST, consumes = {
      MediaType.MULTIPART_FORM_DATA_VALUE})
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ResultResponse> editMyProfile(
      @ModelAttribute EditMyProfileFormWrapper form)
      throws IOException {

    return ResponseEntity.ok(userService.editMyProfile(form));
  }

  @RequestMapping(value = "/profile/my", method = RequestMethod.POST, consumes = {
      MediaType.APPLICATION_JSON_VALUE})
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<ResultResponse> editMyProfile(
      @RequestBody EditMyProfileRequest request) {

    return ResponseEntity.ok(userService.editMyProfile(request));
  }

  @PostMapping("/image")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<Object> image(@RequestParam MultipartFile image)
      throws IOException {

    Object response = imageService.uploadImage(image);

    if (response instanceof ResultResponse) {
      return ResponseEntity.badRequest().body(response);
    }

    return ResponseEntity.ok(response);

  }

  @GetMapping("/statistics/all")
  public ResponseEntity<StatisticsResponse> getAllStatistics() {

    return ResponseEntity.ok(statisticsService.getAllStatistics());

  }

  @GetMapping("/statistics/my")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<StatisticsResponse> getMyStatistics() {

    return ResponseEntity.ok(statisticsService.getMyStatistics());

  }

  @PutMapping("/settings")
  @PreAuthorize("hasAuthority('user:moderate')") // при смене поля в базе нужно сделать logout
  public ResponseEntity<ResultResponse> changeSettings(@RequestBody SettingsRequest request) {
    return ResponseEntity.ok(settingService.changeSettings(request));

  }


}