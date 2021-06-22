package main.repository;

import java.util.Optional;
import main.model.entity.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {

  @Modifying // to execute INSERT, UPDATE, DELETE queries
  @Transactional // only crud methods are transactional by default
  @Query(nativeQuery = true, value = "delete from captcha_codes where time < (NOW() - INTERVAL :time MINUTE) AND id >= 0")
  int deleteExpiredCaptchas(@Param("time")int time);

  @Query(nativeQuery = true, value = "select * from captcha_codes where secret_code = :secret")
  Optional<CaptchaCode> checkSecret(@Param("secret")String secret);


}
