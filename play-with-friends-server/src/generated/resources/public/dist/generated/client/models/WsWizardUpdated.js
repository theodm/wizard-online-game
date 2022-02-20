import {
  RsWizardGameStateForPlayerFromJSON,
  RsWizardGameStateForPlayerToJSON
} from "./RsWizardGameStateForPlayer.js";
import {
  WsLobbyResponseFromJSONTyped,
  WsLobbyResponseToJSON
} from "./WsLobbyResponse.js";
export function WsWizardUpdatedFromJSON(json) {
  return WsWizardUpdatedFromJSONTyped(json, false);
}
export function WsWizardUpdatedFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    ...WsLobbyResponseFromJSONTyped(json, ignoreDiscriminator),
    newWizardState: RsWizardGameStateForPlayerFromJSON(json["newWizardState"])
  };
}
export function WsWizardUpdatedToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    ...WsLobbyResponseToJSON(value),
    newWizardState: RsWizardGameStateForPlayerToJSON(value.newWizardState)
  };
}
