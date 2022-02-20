import {exists} from "../runtime.js";
import {
  RsCardInHandFromJSON,
  RsCardInHandToJSON
} from "./RsCardInHand.js";
export function RsRoundStateForPlayerNormalRoundAllOfFromJSON(json) {
  return RsRoundStateForPlayerNormalRoundAllOfFromJSONTyped(json, false);
}
export function RsRoundStateForPlayerNormalRoundAllOfFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    ownCards: !exists(json, "ownCards") ? void 0 : json["ownCards"].map(RsCardInHandFromJSON),
    numberOfCardsInHandsOfPlayers: !exists(json, "numberOfCardsInHandsOfPlayers") ? void 0 : json["numberOfCardsInHandsOfPlayers"]
  };
}
export function RsRoundStateForPlayerNormalRoundAllOfToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    ownCards: value.ownCards === void 0 ? void 0 : value.ownCards.map(RsCardInHandToJSON),
    numberOfCardsInHandsOfPlayers: value.numberOfCardsInHandsOfPlayers
  };
}
