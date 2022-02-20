import {exists} from "../runtime.js";
export function RsRoundStateForPlayerFirstRoundAllOfFromJSON(json) {
  return RsRoundStateForPlayerFirstRoundAllOfFromJSONTyped(json, false);
}
export function RsRoundStateForPlayerFirstRoundAllOfFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    cardsOfOtherPlayers: !exists(json, "cardsOfOtherPlayers") ? void 0 : json["cardsOfOtherPlayers"],
    cmpLocalPlayerHasPlayedCard: !exists(json, "cmpLocalPlayerHasPlayedCard") ? void 0 : json["cmpLocalPlayerHasPlayedCard"]
  };
}
export function RsRoundStateForPlayerFirstRoundAllOfToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    cardsOfOtherPlayers: value.cardsOfOtherPlayers,
    cmpLocalPlayerHasPlayedCard: value.cmpLocalPlayerHasPlayedCard
  };
}
