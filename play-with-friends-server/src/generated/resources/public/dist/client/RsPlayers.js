export function firstRoundDidLocalPlayerAlreadyPlayHisCard(openWizardRound, localUserPublicID) {
  const currentRoundState = openWizardRound.currentRoundStateForPlayer;
  return currentRoundState.currentStich.playedCards.length > getPlayerIndex(openWizardRound, localUserPublicID);
}
export function getPlayerIndex(openWizardRound, userPublicID) {
  return openWizardRound.immutableRoundState.players.findIndex((it) => it.userPublicID === userPublicID);
}
