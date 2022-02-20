export function RsUserFromJSON(json) {
  return RsUserFromJSONTyped(json, false);
}
export function RsUserFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    userName: json["userName"],
    userPublicID: json["userPublicID"],
    userKey: json["userKey"]
  };
}
export function RsUserToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    userName: value.userName,
    userPublicID: value.userPublicID,
    userKey: value.userKey
  };
}
