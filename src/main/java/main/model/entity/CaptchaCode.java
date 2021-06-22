package main.model.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "captcha_codes")
@Data
public class CaptchaCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "time", nullable = false)
  private Instant time;

  @Column(name = "code", nullable = false)
  private String code; // код, отображаемый на картинкке капчи

  @Column(name = "secret_code", nullable = false)
  private String secretCode; // код, передаваемый в параметре

}
