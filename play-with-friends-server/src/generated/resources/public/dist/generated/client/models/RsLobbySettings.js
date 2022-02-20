import {
  RsWizardGameAndSettingsFromJSON,
  RsWizardGameAndSettingsToJSON
} from "./RsWizardGameAndSettings.js";
export function RsLobbySettingsFromJSON(json) {
  return RsLobbySettingsFromJSONTyped(json, false);
}
export function RsLobbySettingsFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    publicLobby: json["publicLobby"],
    currentSelectedGameAndSettings: RsWizardGameAndSettingsFromJSON(json["currentSelectedGameAndSettings"])
  };
}
export function RsLobbySettingsToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    publicLobby: value.publicLobby,
    currentSelectedGameAndSettings: RsWizardGameAndSettingsToJSON(value.currentSelectedGameAndSettings)
  };
}
