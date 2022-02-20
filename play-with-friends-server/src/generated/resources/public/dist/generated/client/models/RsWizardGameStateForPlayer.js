import {exists} from "../runtime.js";
import {
  RsFinishedWizardRoundFromJSON,
  RsFinishedWizardRoundToJSON
} from "./RsFinishedWizardRound.js";
import {
  RsOpenWizardRoundFromJSON,
  RsOpenWizardRoundToJSON
} from "./RsOpenWizardRound.js";
export function RsWizardGameStateForPlayerFromJSON(json) {
  return RsWizardGameStateForPlayerFromJSONTyped(json, false);
}
export function RsWizardGameStateForPlayerFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    oldRounds: json["oldRounds"].map(RsFinishedWizardRoundFromJSON),
    openWizardRound: !exists(json, "openWizardRound") ? void 0 : RsOpenWizardRoundFromJSON(json["openWizardRound"])
  };
}
export function RsWizardGameStateForPlayerToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    oldRounds: value.oldRounds.map(RsFinishedWizardRoundToJSON),
    openWizardRound: RsOpenWizardRoundToJSON(value.openWizardRound)
  };
}
