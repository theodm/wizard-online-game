import {RsOpenWizardRound} from "../../generated/client";

const indexToColorMap = new Map([
    [0, {color: "text-red-600", backgroundColor: "bg-red-300"}],
    [1, {color: "text-green-600", backgroundColor: "bg-green-300"}],
    [2, {color: "text-blue-600", backgroundColor: "bg-blue-300"}],
    [3, {color: "text-yellow-600", backgroundColor: "bg-yellow-300"}],
    [4, {color: "text-violet-600", backgroundColor: "bg-violet-300"}],
    [5, {color: "text-orange-600", backgroundColor: "bg-orange-300"}],
])


export function playerColors(
    openWizardRound: RsOpenWizardRound,
    playerID: string
) {
    console.log("indexToColorMap: ", indexToColorMap)

    let userIDToIndexMap = new Map(openWizardRound
        .immutableRoundState
        .players
        .map((it, index) => [it.userPublicID, index] as [string, number])
        .map(it => [it[0], indexToColorMap.get(it[1])])
    );

    console.log("userIDToIndexMap: ", userIDToIndexMap)

    return userIDToIndexMap.get(
        playerID
    )!;
}


export function playerBackgroundColor(
    openWizardRound: RsOpenWizardRound,
    playerID: string
) {
    return playerColors(openWizardRound, playerID).backgroundColor;
}

export function playerTextColor(
    openWizardRound: RsOpenWizardRound,
    playerID: string
) {
    return playerColors(openWizardRound, playerID).color;
}