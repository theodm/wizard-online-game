export function CreateUserResponseFromJSON(json) {
  return CreateUserResponseFromJSONTyped(json, false);
}
export function CreateUserResponseFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    userPublicID: json["userPublicID"],
    userKey: json["userKey"]
  };
}
export function CreateUserResponseToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    userPublicID: value.userPublicID,
    userKey: value.userKey
  };
}
