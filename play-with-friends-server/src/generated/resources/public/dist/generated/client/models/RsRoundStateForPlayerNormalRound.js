import {
  RsCardInHandFromJSON,
  RsCardInHandToJSON
} from "./RsCardInHand.js";
import {
  RsRoundStateForPlayerFromJSONTyped,
  RsRoundStateForPlayerToJSON
} from "./RsRoundStateForPlayer.js";
export function RsRoundStateForPlayerNormalRoundFromJSON(json) {
  return RsRoundStateForPlayerNormalRoundFromJSONTyped(json, false);
}
export function RsRoundStateForPlayerNormalRoundFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    ...RsRoundStateForPlayerFromJSONTyped(json, ignoreDiscriminator),
    ownCards: json["ownCards"].map(RsCardInHandFromJSON),
    numberOfCardsInHandsOfPlayers: json["numberOfCardsInHandsOfPlayers"]
  };
}
export function RsRoundStateForPlayerNormalRoundToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    ...RsRoundStateForPlayerToJSON(value),
    ownCards: value.ownCards.map(RsCardInHandToJSON),
    numberOfCardsInHandsOfPlayers: value.numberOfCardsInHandsOfPlayers
  };
}
