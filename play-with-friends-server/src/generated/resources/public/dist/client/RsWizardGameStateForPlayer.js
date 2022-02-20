function calcPointsForRound(bet, actual) {
  const wasCorrect = bet === actual;
  const pointsCorrect20 = wasCorrect ? 20 : 0;
  const pointsCorrect = wasCorrect ? bet * 10 : 0;
  const pointsIncorrect = !wasCorrect ? -Math.abs(actual - bet) * 10 : 0;
  return pointsCorrect20 + pointsCorrect + pointsIncorrect;
}
function openWizardRound_getPointsForRoundAndPlayer(openWizardRound, playerPublicID) {
  const roundStateForPlayer = openWizardRound.currentRoundStateForPlayer;
  return calcPointsForRound(roundStateForPlayer.bets[playerPublicID], roundStateForPlayer.sticheOfPlayer[playerPublicID]);
}
function finishedWizardRound_getPointsForRoundAndPlayer(round, playerPublicID) {
  return calcPointsForRound(round.bets[playerPublicID], round.sticheOfPlayer[playerPublicID]);
}
function openWizardRound_getBetForPlayer(openWizardRound, playerPublicID) {
  const roundStateForPlayer = openWizardRound.currentRoundStateForPlayer;
  return roundStateForPlayer.bets[playerPublicID];
}
function finishedWizardRound_getBetForPlayer(state, playerPublicID, numberOfCardsInRound) {
  return state.oldRounds[numberOfCardsInRound - 1].bets[playerPublicID];
}
export function getBetForRoundAndPlayer(state, playerPublicID, numberOfCardsInRound) {
  const openWizardRound = state.openWizardRound;
  if (numberOfCardsInRound == openWizardRound.immutableRoundState.numberOfCards) {
    return openWizardRound_getBetForPlayer(openWizardRound, playerPublicID);
  }
  return finishedWizardRound_getBetForPlayer(state, playerPublicID, numberOfCardsInRound);
}
export function getPointsSumForRoundAndPlayer(state, playerPublicID, numberOfCardsInRound) {
  const openWizardRound = state.openWizardRound;
  let sum = 0;
  if (numberOfCardsInRound == openWizardRound.immutableRoundState.numberOfCards) {
    const pointsCurrentRound = openWizardRound_getPointsForRoundAndPlayer(openWizardRound, playerPublicID);
    sum += pointsCurrentRound;
  }
  for (let i = 0; i < Math.min(state.oldRounds.length, numberOfCardsInRound); i++) {
    const pointsInRound = finishedWizardRound_getPointsForRoundAndPlayer(state.oldRounds[i], playerPublicID);
    sum += pointsInRound;
  }
  return sum;
}
