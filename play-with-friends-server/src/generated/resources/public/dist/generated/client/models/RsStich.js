import {mapValues} from "../runtime.js";
import {
  RsParticipantFromJSON,
  RsParticipantToJSON
} from "./RsParticipant.js";
import {
  RsWizardCardFromJSON,
  RsWizardCardToJSON
} from "./RsWizardCard.js";
export var RsStichCmpCurrentStichColorEnum;
(function(RsStichCmpCurrentStichColorEnum2) {
  RsStichCmpCurrentStichColorEnum2["Any"] = "Any";
  RsStichCmpCurrentStichColorEnum2["Red"] = "Red";
  RsStichCmpCurrentStichColorEnum2["Yellow"] = "Yellow";
  RsStichCmpCurrentStichColorEnum2["Green"] = "Green";
  RsStichCmpCurrentStichColorEnum2["Blue"] = "Blue";
})(RsStichCmpCurrentStichColorEnum || (RsStichCmpCurrentStichColorEnum = {}));
export function RsStichFromJSON(json) {
  return RsStichFromJSONTyped(json, false);
}
export function RsStichFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    startPlayer: RsParticipantFromJSON(json["startPlayer"]),
    playedCards: json["playedCards"].map(RsWizardCardFromJSON),
    cmpPlayerToCardMap: mapValues(json["cmpPlayerToCardMap"], RsWizardCardFromJSON),
    cmpCurrentStichColor: json["cmpCurrentStichColor"]
  };
}
export function RsStichToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    startPlayer: RsParticipantToJSON(value.startPlayer),
    playedCards: value.playedCards.map(RsWizardCardToJSON),
    cmpPlayerToCardMap: mapValues(value.cmpPlayerToCardMap, RsWizardCardToJSON),
    cmpCurrentStichColor: value.cmpCurrentStichColor
  };
}
