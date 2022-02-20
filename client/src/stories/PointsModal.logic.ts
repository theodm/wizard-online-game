import {Player, PlayerResultInRound, Round} from "../wizard/PointsModal/FnPointsModal";
import {bool, integer, MersenneTwister19937} from "random-js";

const playerForIndicesMap: { [k: number]: Player } = {
    0: {
        name: "Sebastian",
        playerPublicID: "SebastianID",
        rank: 2
    },
    1: {
        name: "Theo",
        playerPublicID: "TheoID",
        rank: 1
    },
    2: {
        name: "Tabea",
        playerPublicID: "TabeaID",
        rank: 3
    },
    3: {
        name: "Dominik",
        playerPublicID: "DominikID",
        rank: 4
    },
    4: {
        name: "Steiner",
        playerPublicID: "SteinerID",
        rank: 5
    },
    5: {
        name: "Jens",
        playerPublicID: "JensID",
        rank: 6
    }
}


export function generatePlayers(numberOfPlayers: number): Player[] {
    const result: Player[] = []
    for (let i = 0; i < numberOfPlayers; i++) {
        result.push(
            playerForIndicesMap[i]
        )
    }

    return result;
}

export function generateRounds(numberOfRounds: number): Round[] {
    const result: Round[] = []

    const engine = MersenneTwister19937.autoSeed();

    for (let i = 0; i < numberOfRounds; i++) {
        const playerRounds: { [key: string]: PlayerResultInRound } = {}
        for (let j = 0; j < 6; j++) {
            playerRounds[playerForIndicesMap[j].playerPublicID] = {
                highestInRound: bool()(engine),
                bet: integer(0, numberOfRounds)(engine),
                betWasCorrect: bool()(engine),
                sumOfPoints: integer(0, 55)(engine) * 10
            }
        }

        result.push({
            numberOfRound: i + 1,
            players: playerRounds
        });
    }

    return result;
}