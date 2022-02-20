import {
  RsImmutableRoundStateFromJSON,
  RsImmutableRoundStateToJSON
} from "./RsImmutableRoundState.js";
import {
  RsRoundStateForPlayerFromJSON,
  RsRoundStateForPlayerToJSON
} from "./RsRoundStateForPlayer.js";
export function RsOpenWizardRoundFromJSON(json) {
  return RsOpenWizardRoundFromJSONTyped(json, false);
}
export function RsOpenWizardRoundFromJSONTyped(json, ignoreDiscriminator) {
  if (json === void 0 || json === null) {
    return json;
  }
  return {
    immutableRoundState: RsImmutableRoundStateFromJSON(json["immutableRoundState"]),
    currentRoundStateForPlayer: RsRoundStateForPlayerFromJSON(json["currentRoundStateForPlayer"])
  };
}
export function RsOpenWizardRoundToJSON(value) {
  if (value === void 0) {
    return void 0;
  }
  if (value === null) {
    return null;
  }
  return {
    immutableRoundState: RsImmutableRoundStateToJSON(value.immutableRoundState),
    currentRoundStateForPlayer: RsRoundStateForPlayerToJSON(value.currentRoundStateForPlayer)
  };
}
