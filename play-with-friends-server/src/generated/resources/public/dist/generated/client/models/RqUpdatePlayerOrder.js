export function RqUpdatePlayerOrderFromJSON(json) {
  return RqUpdatePlayerOrderFromJSONTyped(json, false);
}
export function RqUpdatePlayerOrderFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    newPlayerOrder: json["newPlayerOrder"]
  };
}
export function RqUpdatePlayerOrderToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    newPlayerOrder: value.newPlayerOrder
  };
}
