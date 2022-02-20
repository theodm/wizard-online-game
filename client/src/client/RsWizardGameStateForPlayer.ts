import type {
  RsFinishedWizardRound,
  RsOpenWizardRound,
  RsRoundStateForPlayerFirstRound,
  RsRoundStateForPlayerNormalRound,
  RsWizardGameStateForPlayer,
} from "../generated/client";

function calcPointsForRound(bet: number, actual: number) {
  const wasCorrect = bet === actual;

  const pointsCorrect20 = wasCorrect ? 20 : 0;
  const pointsCorrect = wasCorrect ? bet * 10 : 0;
  const pointsIncorrect = !wasCorrect ? -Math.abs(actual - bet) * 10 : 0;

  return pointsCorrect20 + pointsCorrect + pointsIncorrect;
}

function openWizardRound_getPointsForRoundAndPlayer(
  openWizardRound: RsOpenWizardRound,
  playerPublicID: string
) {
  const roundStateForPlayer = openWizardRound.currentRoundStateForPlayer as
    | RsRoundStateForPlayerNormalRound
    | RsRoundStateForPlayerFirstRound;

  return calcPointsForRound(
    roundStateForPlayer.bets[playerPublicID],
    roundStateForPlayer.sticheOfPlayer[playerPublicID]
  );
}

function finishedWizardRound_getPointsForRoundAndPlayer(
  round: RsFinishedWizardRound,
  playerPublicID: string
) {
  return calcPointsForRound(
    round.bets[playerPublicID],
    round.sticheOfPlayer[playerPublicID]
  );
}

function openWizardRound_getBetForPlayer(
  openWizardRound: RsOpenWizardRound,
  playerPublicID: string
) {
  const roundStateForPlayer = openWizardRound.currentRoundStateForPlayer as
    | RsRoundStateForPlayerNormalRound
    | RsRoundStateForPlayerFirstRound;

  return roundStateForPlayer.bets[playerPublicID];
}

function finishedWizardRound_getBetForPlayer(
  state: RsWizardGameStateForPlayer,
  playerPublicID: string,
  numberOfCardsInRound: number
) {
  return state.oldRounds[numberOfCardsInRound - 1].bets[playerPublicID];
}

export function getBetForRoundAndPlayer(
  state: RsWizardGameStateForPlayer,
  playerPublicID: string,
  numberOfCardsInRound: number
) {
  const openWizardRound = state.openWizardRound!;

  if (
    numberOfCardsInRound == openWizardRound.immutableRoundState.numberOfCards
  ) {
    return openWizardRound_getBetForPlayer(openWizardRound, playerPublicID);
  }

  return finishedWizardRound_getBetForPlayer(
    state,
    playerPublicID,
    numberOfCardsInRound
  );
}

export function getPointsSumForRoundAndPlayer(
  state: RsWizardGameStateForPlayer,
  playerPublicID: string,
  numberOfCardsInRound: number
) {
  const openWizardRound = state.openWizardRound!;

  let sum = 0;

  // Die abgefrage Runde ist die aktuell offene
  // Runde. Daher die Punkte aus der aktuellen Runde berücksichtigen.
  if (
    numberOfCardsInRound == openWizardRound.immutableRoundState.numberOfCards
  ) {
    const pointsCurrentRound = openWizardRound_getPointsForRoundAndPlayer(
      openWizardRound,
      playerPublicID
    );

    sum += pointsCurrentRound;
  }

  for (
    let i = 0;
    i < Math.min(state.oldRounds.length, numberOfCardsInRound);
    i++
  ) {
    const pointsInRound = finishedWizardRound_getPointsForRoundAndPlayer(
      state.oldRounds[i],
      playerPublicID
    );

    sum += pointsInRound;
  }

  return sum;
}
