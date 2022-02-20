import {
  RsLobbySettingsFromJSON,
  RsLobbySettingsToJSON
} from "./RsLobbySettings.js";
import {
  RsParticipantFromJSON,
  RsParticipantToJSON
} from "./RsParticipant.js";
export var RsLobbyParticipantsConnectivityInfoEnum;
(function(RsLobbyParticipantsConnectivityInfoEnum2) {
  RsLobbyParticipantsConnectivityInfoEnum2["Disconnected"] = "DISCONNECTED";
  RsLobbyParticipantsConnectivityInfoEnum2["Active"] = "ACTIVE";
  RsLobbyParticipantsConnectivityInfoEnum2["Inactive"] = "INACTIVE";
})(RsLobbyParticipantsConnectivityInfoEnum || (RsLobbyParticipantsConnectivityInfoEnum = {}));
export function RsLobbyFromJSON(json) {
  return RsLobbyFromJSONTyped(json, false);
}
export function RsLobbyFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    creationTime: new Date(json["creationTime"]),
    lobbyID: json["lobbyID"],
    host: RsParticipantFromJSON(json["host"]),
    participants: json["participants"].map(RsParticipantFromJSON),
    participantsConnectivityInfo: json["participantsConnectivityInfo"],
    inGame: json["inGame"],
    settings: RsLobbySettingsFromJSON(json["settings"])
  };
}
export function RsLobbyToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    creationTime: value.creationTime.toISOString(),
    lobbyID: value.lobbyID,
    host: RsParticipantToJSON(value.host),
    participants: value.participants.map(RsParticipantToJSON),
    participantsConnectivityInfo: value.participantsConnectivityInfo,
    inGame: value.inGame,
    settings: RsLobbySettingsToJSON(value.settings)
  };
}
