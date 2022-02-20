import {RsWizardGameStateForPlayer} from "../../generated/client";
import {getBetForRoundAndPlayer, getPointsSumForRoundAndPlayer} from "../../client/RsWizardGameStateForPlayer";

export interface PlayerResultInRound {
    highestInRound: boolean;
    sumOfPoints: number;
    bet: number;
    betWasCorrect: boolean;
}

export interface Round {
    numberOfRound: number;
    players: { [key: string]: PlayerResultInRound };
}

export interface Player {
    playerPublicID: string;
    name: string;
    rank: number;
}

export function calcRoundsAndPlayers(wizardGameStateForPlayer: RsWizardGameStateForPlayer) {
    let openWizardRound = wizardGameStateForPlayer.openWizardRound!;

    const rounds: Round[] = [];
    for (let i = 1; i <= openWizardRound.immutableRoundState.numberOfCards; i++) {
        const playerRounds: { [key: string]: PlayerResultInRound } = {};

        for (const player of openWizardRound.immutableRoundState.players) {
            const sumOfPoints = getPointsSumForRoundAndPlayer(wizardGameStateForPlayer, player.userPublicID, i);
            const bet = getBetForRoundAndPlayer(wizardGameStateForPlayer, player.userPublicID, i);

            const betWasCorrect = sumOfPoints > (i > 1 ? getPointsSumForRoundAndPlayer(wizardGameStateForPlayer, player.userPublicID, i - 1) : 0);

            playerRounds[player.userPublicID] = {
                highestInRound: false,
                bet: bet,
                betWasCorrect: betWasCorrect,
                sumOfPoints: sumOfPoints
            }
        }

        Object.values(playerRounds)
            .reduce((prev, current) => (prev.sumOfPoints > current.sumOfPoints) ? prev : current)
            .highestInRound = true

        rounds.push({
            numberOfRound: i,
            players: playerRounds
        })
    }

    const players: Player[] = []
    for (const player of openWizardRound.immutableRoundState.players) {
        const lastRound = rounds[rounds.length - 1];

        const playerRanks: { [key: string]: number } = Object.entries(
            lastRound
                .players
        )
            .sort((a, b) => b[1].sumOfPoints - a[1].sumOfPoints)
            .map((it, index) => {
                const obj: { [key: string]: number } = {}

                obj[it[0]] = index + 1

                return obj
            })
            .reduce((prev, current) => ({ ...prev, ...current }))

        players.push({
            name: player.userName,
            playerPublicID: player.userPublicID,
            rank: playerRanks[player.userPublicID]
        })
    }


    return [rounds, players]
}