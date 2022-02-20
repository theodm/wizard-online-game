import {
  RsRoundStateForPlayerFromJSONTyped,
  RsRoundStateForPlayerToJSON
} from "./RsRoundStateForPlayer.js";
export function RsRoundStateForPlayerFirstRoundFromJSON(json) {
  return RsRoundStateForPlayerFirstRoundFromJSONTyped(json, false);
}
export function RsRoundStateForPlayerFirstRoundFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    ...RsRoundStateForPlayerFromJSONTyped(json, ignoreDiscriminator),
    cardsOfOtherPlayers: json["cardsOfOtherPlayers"],
    cmpLocalPlayerHasPlayedCard: json["cmpLocalPlayerHasPlayedCard"]
  };
}
export function RsRoundStateForPlayerFirstRoundToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    ...RsRoundStateForPlayerToJSON(value),
    cardsOfOtherPlayers: value.cardsOfOtherPlayers,
    cmpLocalPlayerHasPlayedCard: value.cmpLocalPlayerHasPlayedCard
  };
}
