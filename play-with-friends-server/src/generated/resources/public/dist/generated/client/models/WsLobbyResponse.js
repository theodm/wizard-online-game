import {
  WsLobbyUpdatedFromJSONTyped,
  WsWizardUpdatedFromJSONTyped
} from "./index.js";
export function WsLobbyResponseFromJSON(json) {
  return WsLobbyResponseFromJSONTyped(json, false);
}
export function WsLobbyResponseFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  if (!ignoreDiscriminator) {
    if (json["type"] === "WsLobbyUpdated") {
      return WsLobbyUpdatedFromJSONTyped(json, true);
    }
    if (json["type"] === "WsWizardUpdated") {
      return WsWizardUpdatedFromJSONTyped(json, true);
    }
  }
  return {
    type: json["type"]
  };
}
export function WsLobbyResponseToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    type: value.type
  };
}
