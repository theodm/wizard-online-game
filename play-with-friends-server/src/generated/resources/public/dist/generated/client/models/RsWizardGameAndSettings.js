import {exists} from "../runtime.js";
export function RsWizardGameAndSettingsFromJSON(json) {
  return RsWizardGameAndSettingsFromJSONTyped(json, false);
}
export function RsWizardGameAndSettingsFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    dummy: !exists(json, "dummy") ? void 0 : json["dummy"]
  };
}
export function RsWizardGameAndSettingsToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    dummy: value.dummy
  };
}
