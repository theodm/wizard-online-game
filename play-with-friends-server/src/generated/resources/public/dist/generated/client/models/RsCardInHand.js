import {
  RsWizardCardFromJSON,
  RsWizardCardToJSON
} from "./RsWizardCard.js";
export function RsCardInHandFromJSON(json) {
  return RsCardInHandFromJSONTyped(json, false);
}
export function RsCardInHandFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    wizardCard: RsWizardCardFromJSON(json["wizardCard"]),
    cmpAllowedToPlay: json["cmpAllowedToPlay"]
  };
}
export function RsCardInHandToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    wizardCard: RsWizardCardToJSON(value.wizardCard),
    cmpAllowedToPlay: value.cmpAllowedToPlay
  };
}
