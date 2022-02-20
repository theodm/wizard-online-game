export var RqUpdateConnectivityNewConnectivityEnum;
(function(RqUpdateConnectivityNewConnectivityEnum2) {
  RqUpdateConnectivityNewConnectivityEnum2["Disconnected"] = "DISCONNECTED";
  RqUpdateConnectivityNewConnectivityEnum2["Active"] = "ACTIVE";
  RqUpdateConnectivityNewConnectivityEnum2["Inactive"] = "INACTIVE";
})(RqUpdateConnectivityNewConnectivityEnum || (RqUpdateConnectivityNewConnectivityEnum = {}));
export function RqUpdateConnectivityFromJSON(json) {
  return RqUpdateConnectivityFromJSONTyped(json, false);
}
export function RqUpdateConnectivityFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    newConnectivity: json["newConnectivity"]
  };
}
export function RqUpdateConnectivityToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    newConnectivity: value.newConnectivity
  };
}
