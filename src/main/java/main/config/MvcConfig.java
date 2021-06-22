package main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

  @Value("${upload.path}")
  String uploadPath;

  @Value("${upload.url}")
  public String uploadUrl;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler(uploadUrl + "**").
        addResourceLocations("file:" + uploadPath + "/");
  }


  @Bean(name = "multipartResolver") // https://www.baeldung.com/spring-file-upload
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setResolveLazily(true);
    return multipartResolver;
  }

}
