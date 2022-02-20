export var RsNumberColorCardColorEnum;
(function(RsNumberColorCardColorEnum2) {
  RsNumberColorCardColorEnum2["Red"] = "Red";
  RsNumberColorCardColorEnum2["Yellow"] = "Yellow";
  RsNumberColorCardColorEnum2["Green"] = "Green";
  RsNumberColorCardColorEnum2["Blue"] = "Blue";
})(RsNumberColorCardColorEnum || (RsNumberColorCardColorEnum = {}));
export var RsNumberColorCardCmpTrumpColorEnum;
(function(RsNumberColorCardCmpTrumpColorEnum2) {
  RsNumberColorCardCmpTrumpColorEnum2["Red"] = "Red";
  RsNumberColorCardCmpTrumpColorEnum2["Yellow"] = "Yellow";
  RsNumberColorCardCmpTrumpColorEnum2["Green"] = "Green";
  RsNumberColorCardCmpTrumpColorEnum2["Blue"] = "Blue";
  RsNumberColorCardCmpTrumpColorEnum2["None"] = "None";
})(RsNumberColorCardCmpTrumpColorEnum || (RsNumberColorCardCmpTrumpColorEnum = {}));
export function RsNumberColorCardFromJSON(json) {
  return RsNumberColorCardFromJSONTyped(json, false);
}
export function RsNumberColorCardFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    color: json["color"],
    number: json["number"],
    cmpTrumpColor: json["cmpTrumpColor"]
  };
}
export function RsNumberColorCardToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    color: value.color,
    number: value.number,
    cmpTrumpColor: value.cmpTrumpColor
  };
}
