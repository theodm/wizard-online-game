import {
  WsUpdateConnectivityFromJSONTyped
} from "./index.js";
export function WsLobbyRequestFromJSON(json) {
  return WsLobbyRequestFromJSONTyped(json, false);
}
export function WsLobbyRequestFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  if (!ignoreDiscriminator) {
    if (json["type"] === "WsUpdateConnectivity") {
      return WsUpdateConnectivityFromJSONTyped(json, true);
    }
  }
  return {
    type: json["type"]
  };
}
export function WsLobbyRequestToJSON(value) {
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
