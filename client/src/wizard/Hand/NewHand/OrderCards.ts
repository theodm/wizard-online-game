import {RsCardInHand, RsWizardCard, RsWizardCardCmpTrumpColorEnum} from "../../../generated/client";

function getRandomInt(min: number, max: number) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min;
}

export function randomCard() {
    const r = getRandomInt(0, 60);
    const m = (r % 13) + 1;

    if (r < 13) {
        return createCard("R" + m)
    } else if (r < 26) {
        return createCard("Y" + m)
    } else if (r < 39) {
        return createCard("G" + m)
    } else if (r < 52) {
        return createCard("B" + m)
    } else if (r < 56) {
        return createCard("J")
    } else  {
        return createCard("N")
    }
}

export function createCard(
    str: string
): RsWizardCard {
    if (str === "J") {
        return {
            cardID: "R1",
            type: "RsJoker",
            cmpTrumpColor: RsWizardCardCmpTrumpColorEnum.None
        }
    }

    if (str === "N") {
        return {
            cardID: "N1",
            type: "RsNull",
            cmpTrumpColor: RsWizardCardCmpTrumpColorEnum.None
        }
    }

    const color = str.substring(0, 1);
    const nmbr = Number.parseInt(str.substring(1));

    let colorEnum: "Yellow" | "Green" | "Red" | "Blue" = "Yellow";
    switch (color) {
        case "Y":
            colorEnum = "Yellow";
            break;
        case "G":
            colorEnum = "Green";
            break;
        case "R":
            colorEnum = "Red";
            break;
        case "B":
            colorEnum = "Blue";
            break;
    }

    return {
        type: "RsNumberColorCard",
        // @ts-ignore
        cmpTrumpColor: colorEnum,
        // @ts-ignore
        color: colorEnum,
        number: nmbr
    }
}

export function cardToNumber(
    card: RsWizardCard,
    trumpColor: "Yellow" | "Green" | "Red" | "Blue" | undefined
) {
    if (card.type === "RsJoker") {
        return 0;
    }

    if (card.type === "RsNumberColorCard") {
        let multiplicator;

        if (card.cmpTrumpColor === trumpColor) {
            multiplicator = 0;
        } else {
            const remainingColorsOrder = ["Red", "Green", "Yellow", "Blue"]
                .filter(it => it !== trumpColor)

            multiplicator = remainingColorsOrder.indexOf(card.cmpTrumpColor!) + 1

            if (multiplicator == 0) {
                throw new Error("should not happen")
            }
        }

        return 13 * multiplicator + (14 - card.number!)
    }

    if (card.type === "RsNull") {
        return 2000;
    }

    throw new Error("should not happen")
}

export function orderCards(
    cards: RsCardInHand[],
    trumpColor: "Yellow" | "Green" | "Red" | "Blue" | undefined
) {
    return [...cards].sort((a, b) => {
        return cardToNumber(b.wizardCard, trumpColor) - cardToNumber(a.wizardCard, trumpColor);
    })
}