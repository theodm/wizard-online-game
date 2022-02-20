import {exists} from "../runtime.js";
export var WsUpdateConnectivityAllOfNewConnectivityEnum;
(function(WsUpdateConnectivityAllOfNewConnectivityEnum2) {
  WsUpdateConnectivityAllOfNewConnectivityEnum2["Disconnected"] = "DISCONNECTED";
  WsUpdateConnectivityAllOfNewConnectivityEnum2["Active"] = "ACTIVE";
  WsUpdateConnectivityAllOfNewConnectivityEnum2["Inactive"] = "INACTIVE";
})(WsUpdateConnectivityAllOfNewConnectivityEnum || (WsUpdateConnectivityAllOfNewConnectivityEnum = {}));
export function WsUpdateConnectivityAllOfFromJSON(json) {
  return WsUpdateConnectivityAllOfFromJSONTyped(json, false);
}
export function WsUpdateConnectivityAllOfFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    type: !exists(json, "type") ? void 0 : json["type"],
    newConnectivity: !exists(json, "newConnectivity") ? void 0 : json["newConnectivity"]
  };
}
export function WsUpdateConnectivityAllOfToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    type: value.type,
    newConnectivity: value.newConnectivity
  };
}
