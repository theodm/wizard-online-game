export function RsParticipantFromJSON(json) {
  return RsParticipantFromJSONTyped(json, false);
}
export function RsParticipantFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    userPublicID: json["userPublicID"],
    userName: json["userName"]
  };
}
export function RsParticipantToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    userPublicID: value.userPublicID,
    userName: value.userName
  };
}
