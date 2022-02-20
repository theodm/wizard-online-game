import {
  RsShortLobbyInfoFromJSON,
  RsShortLobbyInfoToJSON
} from "./RsShortLobbyInfo.js";
export function RsLobbiesResponseFromJSON(json) {
  return RsLobbiesResponseFromJSONTyped(json, false);
}
export function RsLobbiesResponseFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    lobbies: json["lobbies"].map(RsShortLobbyInfoFromJSON)
  };
}
export function RsLobbiesResponseToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    lobbies: value.lobbies.map(RsShortLobbyInfoToJSON)
  };
}
