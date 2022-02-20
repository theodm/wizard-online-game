import {exists} from "../runtime.js";
import {
  RsWizardGameStateForPlayerFromJSON,
  RsWizardGameStateForPlayerToJSON
} from "./RsWizardGameStateForPlayer.js";
export function WsWizardUpdatedAllOfFromJSON(json) {
  return WsWizardUpdatedAllOfFromJSONTyped(json, false);
}
export function WsWizardUpdatedAllOfFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    newWizardState: !exists(json, "newWizardState") ? void 0 : RsWizardGameStateForPlayerFromJSON(json["newWizardState"])
  };
}
export function WsWizardUpdatedAllOfToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    newWizardState: RsWizardGameStateForPlayerToJSON(value.newWizardState)
  };
}
