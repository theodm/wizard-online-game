export function CreateLobbyResponseFromJSON(json) {
  return CreateLobbyResponseFromJSONTyped(json, false);
}
export function CreateLobbyResponseFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    lobbyCode: json["lobbyCode"]
  };
}
export function CreateLobbyResponseToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    lobbyCode: value.lobbyCode
  };
}
