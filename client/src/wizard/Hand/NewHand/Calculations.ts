import {RsWizardCard} from "../../../generated/client";

/**
 * Wieviele Karten dürfen maximal in einer Reihe sein?
 */
const numOfCards = 10;

// Wie sehr dürfen Karten horizontal überlappen (das ist ein Maximalwert!)
// In diesem Fall: Karten dürfen Karten maximal 12 / 20 bedeckt werden.
const overlapPercentageX = 13 / 20;
const minOverlapPercentageX = 4 / 20;

// Wie sehr dürfen Karten vertikal überlappen (das ist ein Maximalwert!)
// In diesem Fall: Karten dürfen Karten maximal 16 / 20 bedeckt werden.
const overlapPercentageY = 16 / 20;
const minOverlapPercentageY = 12 / 20;


export class Calculations {
    private scaleFactor: number = 1.0;

    private pxContainerWidth: number = 1.0;
    private pxContainerHeight: number = 1.0;
    private pxCardWidth: number = 1.0;
    private pxCardHeight: number = 1.0;
    private pxTranslateYOffset: number = 1.0;
    private numberOfLanes: number = 1.0;

    /**
     * Skalierungsfaktor, der die gesamten
     * @private
     */
    private _scaleFactorExtendToWidth() {
        const cardPercentage = 1 / numOfCards;
        const negativeOverlapPercentageOf100 = overlapPercentageX * cardPercentage;
        const initialPercentage = cardPercentage;
        const initialPercentageAllCards = (numOfCards - 1) * (initialPercentage - negativeOverlapPercentageOf100) + initialPercentage;

        return 0.01 / initialPercentageAllCards;
    }

    private _maxScaleFactorForHeight() {
        const n = this.numberOfLanes + 1;
        const h = this.pxContainerHeight;
        const p = (100 / numOfCards) * this.pxContainerWidth;
        const o = 1 - overlapPercentageY

        // h = n * (o * card_height)
        // h = n * (o * card_width * (22/14))
        // h = n * (o * p * s * (22/14))
        // solve for s

        const scaleFactor = (7 * h) / (11 * n * o * p);

        // console.log(scaleFactor);

        return scaleFactor;
    }


    private _cardWidth(pxContainerWidth: number) {
        return (100 / numOfCards) * this.scaleFactor * pxContainerWidth;
    }

    private _cardHeight() {
        return this.pxCardWidth * (22 / 14)
    }

    private _translateYOffset() {
        return this.pxCardHeight * (1 - this.optimalOverlapY());
    }

    constructor(pxContainerWidth: number, pxContainerHeight: number, numberOfLanes: number) {
        this.numberOfLanes = numberOfLanes;
        this.pxContainerWidth = pxContainerWidth;
        this.pxContainerHeight = pxContainerHeight;

        this.scaleFactor = Math.min(this._scaleFactorExtendToWidth(), this._maxScaleFactorForHeight())
        this.pxCardWidth = this._cardWidth(pxContainerWidth);
        this.pxCardHeight = this._cardHeight()
        this.pxTranslateYOffset = this._translateYOffset();
    }

    private optimalOverlapXForLane(numberOfCardsInLane: number) {
        const optimalOverlapPercentageX = (this.pxCardWidth * numberOfCardsInLane - this.pxContainerWidth) / (this.pxCardWidth * (numberOfCardsInLane - 1))

        return Math.min(overlapPercentageX, Math.max(minOverlapPercentageX, optimalOverlapPercentageX));
    }

    private optimalOverlapY() {
        //return overlapPercentageY;
        const h = this.pxContainerHeight;
        const n = this.numberOfLanes + 1;
        const c = this.pxCardHeight;

        // h = n * ((1 - o) * c), solve for o
        const optimalOverlapPercentageY = 1 - (h / (c * n))

        // console.log("numberOfLanesToUse", n, "optimalOverlapPercentageY", optimalOverlapPercentageY)
        return Math.min(overlapPercentageY, Math.max(minOverlapPercentageY, optimalOverlapPercentageY));
    }

    private cardCenterOffset(
        numberOfCardsInLane: number
    ) {
        const sizeOfToBeCentered = (Math.min(numberOfCardsInLane, numOfCards) - 1) * (1 - this.optimalOverlapXForLane(numberOfCardsInLane)) * this.pxCardWidth + this.pxCardWidth;

        return (this.pxContainerWidth / 2) - (sizeOfToBeCentered / 2);
    }

    cardPosition(
        lane: number,
        numberOfCardsInLane: number,
        indexInLane: number,
        isElevated: boolean
    ) {
        const bottom = lane * this.pxTranslateYOffset - (this.pxCardHeight * this.optimalOverlapY()) + (isElevated ? this.pxTranslateYOffset : 0);
        const left = this.cardCenterOffset(numberOfCardsInLane) + indexInLane * (this.pxCardWidth * (1 - this.optimalOverlapXForLane(numberOfCardsInLane)))

        return {
            width: this.pxCardWidth,
            bottom: bottom,
            left: left
        }
    }
}

export function indexToLaneMap<T>(arr: Array<T>, sliceLength: number): {
    numberOfLanes: number,
    newArr: {
        element: T,
        laneIndex: number;
        cardInLaneIndex: number;
        numberOfElementsInLane: number;
    }[]
} {
    const remainder = arr.length % sliceLength;
    const numberOfLanes = Math.floor(arr.length / sliceLength) + (remainder > 0 ? 1 : 0);

    const cardsPerLaneWithoutRemainder = Math.floor(arr.length / numberOfLanes)
    const numberOfLanesWithOneCardMore = arr.length % numberOfLanes;

    let indexInArray = 0;

    const result: {
        element: T,
        laneIndex: number;
        cardInLaneIndex: number;
        numberOfElementsInLane: number;
    }[] = [];

    for (let li = 0; li < numberOfLanes; li++) {
        const additionalCards = li < numberOfLanesWithOneCardMore ? 1 : 0;
        const elementsToPop = cardsPerLaneWithoutRemainder + additionalCards;

        let j = 0;
        for (let i = indexInArray; i < indexInArray + elementsToPop; i++) {
            result.push({
                element: arr[i],
                laneIndex: li,
                numberOfElementsInLane: elementsToPop,
                cardInLaneIndex: j
            });

            j++
        }

        indexInArray += elementsToPop;
    }

    return {
        numberOfLanes: numberOfLanes,
        newArr: result
    };
}

export function makeLanes<T>(arr: Array<T>, sliceLength: number): T[][] {
    const remainder = arr.length % sliceLength;
    const numberOfLanes = Math.floor(arr.length / sliceLength) + (remainder > 0 ? 1 : 0);

    const cardsPerLaneWithoutRemainder = Math.floor(arr.length / numberOfLanes)
    const numberOfLanesWithOneCardMore = arr.length % numberOfLanes;

    let indexInArray = 0;

    const result: T[][] = [];
    for (let li = 0; li < numberOfLanes; li++) {
        const additionalCards = li < numberOfLanesWithOneCardMore ? 1 : 0;
        const elementsToPop = cardsPerLaneWithoutRemainder + additionalCards;

        result.push(arr.slice(indexInArray, indexInArray + elementsToPop));
        indexInArray += elementsToPop;
    }

    return result;
}

/**
 * Welche Elemente sind in a aber nicht in b?
 */
export function diffLeft<T>(a: Array<T>, b: Array<T>, idFn: (it: T) => any = (it) => it) {
    return a
        .filter(x => !b.map(idFn).includes(idFn(x)));
}

/**
 * Welche Elemente sind in b aber nicht in a?
 */
export function diffRight<T>(a: Array<T>, b: Array<T>, idFn: (it: T) => any = (it) => it) {
    return diffLeft(b, a, idFn)
}