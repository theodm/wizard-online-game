import {exists} from "../runtime.js";
export var RsNumberColorCardAllOfColorEnum;
(function(RsNumberColorCardAllOfColorEnum2) {
  RsNumberColorCardAllOfColorEnum2["Red"] = "Red";
  RsNumberColorCardAllOfColorEnum2["Yellow"] = "Yellow";
  RsNumberColorCardAllOfColorEnum2["Green"] = "Green";
  RsNumberColorCardAllOfColorEnum2["Blue"] = "Blue";
})(RsNumberColorCardAllOfColorEnum || (RsNumberColorCardAllOfColorEnum = {}));
export function RsNumberColorCardAllOfFromJSON(json) {
  return RsNumberColorCardAllOfFromJSONTyped(json, false);
}
export function RsNumberColorCardAllOfFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    color: !exists(json, "color") ? void 0 : json["color"],
    number: !exists(json, "number") ? void 0 : json["number"]
  };
}
export function RsNumberColorCardAllOfToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    color: value.color,
    number: value.number
  };
}
