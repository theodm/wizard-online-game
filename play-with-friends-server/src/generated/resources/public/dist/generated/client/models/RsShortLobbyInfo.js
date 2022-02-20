import {
  RsParticipantFromJSON,
  RsParticipantToJSON
} from "./RsParticipant.js";
export function RsShortLobbyInfoFromJSON(json) {
  return RsShortLobbyInfoFromJSONTyped(json, false);
}
export function RsShortLobbyInfoFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    lobbyID: json["lobbyID"],
    players: json["players"].map(RsParticipantFromJSON),
    isInGame: json["isInGame"]
  };
}
export function RsShortLobbyInfoToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    lobbyID: value.lobbyID,
    players: value.players.map(RsParticipantToJSON),
    isInGame: value.isInGame
  };
}
