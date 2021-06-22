package main.repository;

import main.model.entity.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {

  GlobalSetting findByCode (String code);


}
