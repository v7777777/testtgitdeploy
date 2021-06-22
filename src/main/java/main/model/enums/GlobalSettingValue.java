package main.model.enums;

import java.util.stream.Stream;
import javax.persistence.Column;


public enum GlobalSettingValue {

  MULTIUSER_MODE("Многопользовательский режим"),
  POST_PREMODERATION( "Премодерация постов"),
  STATISTICS_IS_PUBLIC("Показывать всем статистику блога");

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Column(name = "name", nullable = false)
  private String name; // название имя настройки

  GlobalSettingValue(String name) {

    this.name = name;
  }

  public static GlobalSettingValue of(String name) {  // получить перем emum по строке
    return Stream.of(GlobalSettingValue.values())
        .filter(p -> p.getName().equals(name))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }



}



