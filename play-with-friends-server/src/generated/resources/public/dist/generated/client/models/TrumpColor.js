export var TrumpColor;
(function(TrumpColor2) {
  TrumpColor2["Red"] = "Red";
  TrumpColor2["Yellow"] = "Yellow";
  TrumpColor2["Green"] = "Green";
  TrumpColor2["Blue"] = "Blue";
  TrumpColor2["None"] = "None";
})(TrumpColor || (TrumpColor = {}));
export function TrumpColorFromJSON(json) {
  return TrumpColorFromJSONTyped(json, false);
}
export function TrumpColorFromJSONTyped(json, ignoreDiscriminator) {
  return json;
}
export function TrumpColorToJSON(value) {
  return value;
}
