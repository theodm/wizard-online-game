import {exists} from "../runtime.js";
import {
  RsLobbyFromJSON,
  RsLobbyToJSON
} from "./RsLobby.js";
export function WsLobbyUpdatedAllOfFromJSON(json) {
  return WsLobbyUpdatedAllOfFromJSONTyped(json, false);
}
export function WsLobbyUpdatedAllOfFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    newLobby: !exists(json, "newLobby") ? void 0 : RsLobbyFromJSON(json["newLobby"])
  };
}
export function WsLobbyUpdatedAllOfToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    newLobby: RsLobbyToJSON(value.newLobby)
  };
}
