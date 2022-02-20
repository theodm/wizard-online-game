export var RsNullCmpTrumpColorEnum;
(function(RsNullCmpTrumpColorEnum2) {
  RsNullCmpTrumpColorEnum2["Red"] = "Red";
  RsNullCmpTrumpColorEnum2["Yellow"] = "Yellow";
  RsNullCmpTrumpColorEnum2["Green"] = "Green";
  RsNullCmpTrumpColorEnum2["Blue"] = "Blue";
  RsNullCmpTrumpColorEnum2["None"] = "None";
})(RsNullCmpTrumpColorEnum || (RsNullCmpTrumpColorEnum = {}));
export function RsNullFromJSON(json) {
  return RsNullFromJSONTyped(json, false);
}
export function RsNullFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    cmpTrumpColor: json["cmpTrumpColor"]
  };
}
export function RsNullToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    cmpTrumpColor: value.cmpTrumpColor
  };
}
