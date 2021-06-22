package main.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.entity.GlobalSetting;
import main.model.enums.SettingValue;

@Data
@NoArgsConstructor
public class SettingsResponse {

  @JsonProperty("MULTIUSER_MODE")
  private boolean multiuserMode;
  @JsonProperty("POST_PREMODERATION")
  private boolean postPremoderation;
  @JsonProperty("STATISTICS_IS_PUBLIC")
  private boolean statisticsIsPublic;

  public SettingsResponse(List<GlobalSetting> allSettings) {

    this.multiuserMode = convertSettingValueToBoolean(
        allSettings.stream().filter(gs -> gs.getCode().equals("MULTIUSER_MODE")).findFirst().get()
            .getValue());
    this.postPremoderation = convertSettingValueToBoolean(
        allSettings.stream().filter(gs -> gs.getCode().equals("POST_PREMODERATION")).findFirst()
            .get().getValue());
    this.statisticsIsPublic = convertSettingValueToBoolean(
        allSettings.stream().filter(gs -> gs.getCode().equals("STATISTICS_IS_PUBLIC")).findFirst()
            .get().getValue());

  }

  private boolean convertSettingValueToBoolean(SettingValue value) {

    if (value.equals(SettingValue.YES)) {
      return true;
    } else {
      return false;
    }
  }

}


