package main.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import main.model.enums.GlobalSettingValue;
import main.model.enums.SettingValue;

@Entity
@Table(name = "global_settings")
@Data
public class GlobalSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

 // @Enumerated(EnumType.STRING)
 // @Column(name = "code",columnDefinition =
     // "enum('MULTIUSER_MOD','POST_PREMODERATION','STATISTICS_IS_PUBLIC')", nullable = false)

  @Transient // field is not to be serialized
      GlobalSettingValue globalSettingValue;

  @Column(name = "code", nullable = false)
  String code;
  @Column(name = "name", nullable = false)
  String name;

  @PostLoad  //@PostLoad — вызывается после загрузки данных сущности из БД.
  void fillTransient() {
    this.globalSettingValue = GlobalSettingValue.of(name);
  }

  @PrePersist // @PrePersist — вызывается как только инициирован вызов persist() и исполняется перед остальными действиями.
  void fillPersistent() {
    this.name = globalSettingValue.getName();
    this.code = globalSettingValue.toString();
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "value",columnDefinition = "enum('YES','NO')", nullable = false)
  private SettingValue value;


}
