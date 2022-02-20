import {exists} from "../runtime.js";
export var RsJokerCmpTrumpColorEnum;
(function(RsJokerCmpTrumpColorEnum2) {
  RsJokerCmpTrumpColorEnum2["Red"] = "Red";
  RsJokerCmpTrumpColorEnum2["Yellow"] = "Yellow";
  RsJokerCmpTrumpColorEnum2["Green"] = "Green";
  RsJokerCmpTrumpColorEnum2["Blue"] = "Blue";
  RsJokerCmpTrumpColorEnum2["None"] = "None";
})(RsJokerCmpTrumpColorEnum || (RsJokerCmpTrumpColorEnum = {}));
export function RsJokerFromJSON(json) {
  return RsJokerFromJSONTyped(json, false);
}
export function RsJokerFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    cmpTrumpColor: !exists(json, "cmpTrumpColor") ? void 0 : json["cmpTrumpColor"]
  };
}
export function RsJokerToJSON(value) {
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
