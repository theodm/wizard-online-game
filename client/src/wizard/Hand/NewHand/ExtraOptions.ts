
export interface ExtraOptions {
    readonly type: "SelectTrumpColorInput" | "PlaceBetInput" | "NoExtraOptions";
}

export class NoExtraOptions implements ExtraOptions {
    readonly type = "NoExtraOptions";
}

export class SelectTrumpColorInput implements ExtraOptions {
    readonly type = "SelectTrumpColorInput";
}

export interface BetCard {
    number: number,
    allowedToChoose: boolean
}

export class PlaceBetInput {
    readonly type = "PlaceBetInput";

    constructor(
        readonly maxBet: number,
        readonly notAllowedBet: number | undefined
    ) {
    }

    getBetCards(): BetCard[] {
        const allowedBets: BetCard[] = [];

        for (let i = 0; i <= this.maxBet; i++) {
            allowedBets.push({
                number: i,
                allowedToChoose: i != this.notAllowedBet
            })
        }

        return allowedBets;
    }
}

