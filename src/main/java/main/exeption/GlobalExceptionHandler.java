package main.exeption;

import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // ----- !!! net::ERR_CONNECTION_RESET in brouser 500 failed to load response data

//  запрос был отправлен, но данные не полностью размещены из-за MaxUploadSizeExceededException
//      (были загружены только частичные данные).
//  В браузере, если сообщение запроса не будет завершено, ответ будет проглочен.

  // exception will be triggered before the request is mapped to controller.

//  @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
//  protected ResponseEntity<ResultResponse> handleMaxUploadSizeExceededException(
//      MaxUploadSizeExceededException ex, WebRequest req) {
//
//    ResultResponse res = new ResultResponse();
//    res.setResult(false);
//    Map<String, String> err = new HashMap<>();
//    err.put("file size", "This file is too big, it exeeds 10 mb");
//    res.setErrors(err);
//
//    ResponseEntity<ResultResponse> r = ResponseEntity.ok(res);
//
//    System.out.println(r.toString() + "  ---------   r.toString()");
//    System.out.println(r.getStatusCode() + "  ---------   r.getStatusCode()");
//
//    return r;
//  }

//  @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
//  protected String handleMaxUploadSizeExceededException(
//      RedirectAttributes ra) {
//
//    ResultResponse res = new ResultResponse();
//    ra.addFlashAttribute("message", "too big file");
//
//    return "forward:/";
//  }


  @ExceptionHandler(value = {IOException.class})
  protected ResponseEntity<ApiError> handleIOException(
      IOException ex, WebRequest req) {

    ApiError error = new ApiError("trouble handling the image");

    return ResponseEntity.badRequest().body(error);

  }

  @ExceptionHandler(value = {MessagingException.class})
  protected ResponseEntity<ApiError> handleMessagingException(
      MessagingException ex, WebRequest req) {

    ApiError error = new ApiError("trouble with email service");

    return ResponseEntity.badRequest().body(error);

  }
}





