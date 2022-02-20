<script lang="ts">
    import {RsCardInHand, RsWizardCard, TrumpColor} from "../../../generated/client";
    import {onMount} from "svelte";
    import CardInHand from "./CardInHand.svelte";
    import TrumpColorCardInHand from "./TrumpColorCardInHand.svelte";
    import {BetCard, ExtraOptions, NoExtraOptions, PlaceBetInput, SelectTrumpColorInput} from "./ExtraOptions";
    import SelectBetCardInHand from "./SelectBetCardInHand.svelte";
    import {Calculations, diffLeft, diffRight, indexToLaneMap, makeLanes} from "./Calculations";
    import {concatMap, delay, of, ReplaySubject, Subject} from "rxjs";
    import {orderCards} from "./OrderCards";
    import {registerResizeChange} from "../../dom/ResizeListener";

    export let onCardPlayed: (card: RsWizardCard) => Promise<void>;
    export let onTrumpColorSelected: (color: TrumpColor) => Promise<void>;
    export let onSelectBet: (bet: number) => Promise<void>;

    export let extraOptions: ExtraOptions = new NoExtraOptions();
    let prevExtraOptions: ExtraOptions = new NoExtraOptions();

    let shownTrumpColors: TrumpColor[] = []
    let shownBetCards: BetCard[] = [];

    export let cards: RsCardInHand[];
    export let trumpColor: "Red" | "Yellow" | "Green" | "Blue" | undefined = undefined;
    let prevCards: RsCardInHand[] = [];
    let shownCards: RsCardInHand[] = [];

    export let disabled: boolean;

    let container: HTMLElement;
    let containerBounds: DOMRectReadOnly | undefined

    let currentCardUnderMouse: undefined | any = undefined;
    let currentSelectedCard: undefined | any = undefined;

    const cardStream = new ReplaySubject<{ action: "ADD" | "REMOVE", data: RsWizardCard | undefined }>();

    onMount(() => {
        registerResizeChange(container, (newBounds) => containerBounds = newBounds)

        cardStream
            .pipe(
                concatMap(it => of(it).pipe(delay(150)))
            )
            .subscribe((val: any) => {
                if (val.action === "ADD") {
                    shownCards = [...shownCards, val.data]
                } else if (val.action === "REMOVE") {
                    shownCards = shownCards.filter(it => it.wizardCard.cardID !== val.data.wizardCard.cardID);
                } else if (val.action === "REORDER") {
                    shownCards = orderCards(shownCards, trumpColor);
                } else if (val.action === "ADD_TRUMP") {
                    shownTrumpColors = [...shownTrumpColors, val.data]
                } else if (val.action === "ADD_BET_CARD") {
                    shownBetCards = [...shownBetCards, val.data]
                }
            })
    })

    $: {
        if (cards !== prevCards) {
            currentSelectedCard = undefined;
            currentCardUnderMouse = undefined;

            for (const card of shownCards) {
                const existing = cards.filter(it => it.wizardCard.cardID === card.wizardCard.cardID);

                if (existing.length > 0) {
                    card.cmpAllowedToPlay = existing[0].cmpAllowedToPlay;
                }
            }

            const removedElements = diffLeft(prevCards, cards, it => it.wizardCard.cardID)
            const addedElements = diffRight(prevCards, cards, it => it.wizardCard.cardID)

            console.log("addedElements", addedElements)
            console.log("removedElements", removedElements)

            addedElements.forEach((addedElement) => {
                cardStream.next({action: "ADD", data: addedElement});
            })

            removedElements.forEach((removedElement) => {
                cardStream.next({action: "REMOVE", data: removedElement});
            })

            cardStream.next({action: "REORDER", data: undefined});


        }

        // ToDo: Echter Gleichheits-Check
        if (extraOptions !== prevExtraOptions) {
            if (extraOptions instanceof SelectTrumpColorInput) {
                const trumpColors = [TrumpColor.Red, TrumpColor.Blue, TrumpColor.Yellow, TrumpColor.Green];

                currentSelectedCard = undefined;
                currentCardUnderMouse = undefined;
                shownTrumpColors = [];

                for (const tc of trumpColors) {
                    cardStream.next({action: "ADD_TRUMP", data: tc})
                }
            }

            if (extraOptions instanceof PlaceBetInput) {
                currentSelectedCard = undefined;
                currentCardUnderMouse = undefined;
                shownBetCards = [];

                for (const bc of extraOptions.getBetCards()) {
                    cardStream.next({action: "ADD_BET_CARD", data: bc})
                }
            }
        }

        prevCards = cards;
        prevExtraOptions = extraOptions;
    }


    // Lanes der "normalen" Karten (nicht die Trumpauswahlkarten oder Wetteneingabekarten)
    $: laneResult = indexToLaneMap(shownCards, 10)
    $: numberOfLanes = laneResult.numberOfLanes
    $: laneInfoCards = laneResult.newArr

    // Lanes der Wetteneingabekarten
    $: betLaneResult = extraOptions instanceof PlaceBetInput ? indexToLaneMap(shownBetCards, 10) : undefined;
    $: betNumberOfLanes = betLaneResult ? betLaneResult.numberOfLanes : 0
    $: betLaneInfoCards = betLaneResult ? betLaneResult.newArr : undefined

    // Lanes der Trumpauswahlkarten
    $: trumpNumberOfLanes = extraOptions instanceof SelectTrumpColorInput ? 1 : 0

    // Mit welchem Index beginnen die Wetteingabekarten bzw. Trumpauswahlkarten,
    // damit sie über den anderen Karten angezeigt werden?
    $: extraLaneStartIndex = numberOfLanes
    $: totalNumberOfLanes = numberOfLanes + trumpNumberOfLanes + betNumberOfLanes


    $: calculations = containerBounds ? new Calculations(containerBounds.width, containerBounds.height, totalNumberOfLanes) : new Calculations(1, 1, 1);
</script>

<div class="w-full relative h-full" bind:this={container}>

    {#each laneInfoCards as card, cardIndex (card.element.wizardCard.cardID)}
        <CardInHand
                {currentSelectedCard}
                card={card.element}
                cardIndex={card.cardInLaneIndex}
                cardLaneIndex={card.laneIndex}
                {calculations}
                containerWidth={containerBounds ? containerBounds.width : 1000}
                {currentCardUnderMouse}
                numberOfCardsInLane={card.numberOfElementsInLane}
                onMouseEnter={(card) => currentCardUnderMouse = card}
                onMouseLeave={(card) => { if (currentCardUnderMouse === card) { currentCardUnderMouse = undefined } }}
                onFinallySelected={(card) => {
                    currentSelectedCard = undefined;
                    currentCardUnderMouse = undefined;
                    onCardPlayed(card.wizardCard)
                }}
                onSelect={(card) => currentSelectedCard = card }
                onUnSelect={(card) => currentSelectedCard = undefined }
                externallyDisabled={disabled || (extraOptions && (extraOptions.type === "SelectTrumpColorInput" || extraOptions.type === "PlaceBetInput"))}
        />
    {/each}
    {#if extraOptions && extraOptions.type === "SelectTrumpColorInput"}
        {#each shownTrumpColors as color, colorIndex (color)}
            <TrumpColorCardInHand
                    {currentSelectedCard}
                    {color}
                    {calculations}
                    cardIndex={colorIndex}
                    cardLaneIndex={extraLaneStartIndex}
                    containerWidth={containerBounds ? containerBounds.width : 1000}
                    {currentCardUnderMouse}
                    numberOfCardsInLane={shownTrumpColors.length}
                    onMouseEnter={(card) => currentCardUnderMouse = card}
                    onMouseLeave={(card) => { if (currentCardUnderMouse === card) { currentCardUnderMouse = undefined } }}
                    onSelect={(card) => currentSelectedCard = card }
                    onUnSelect={(card) => currentSelectedCard = undefined }
                    onFinallySelected={(color) => {
                    currentSelectedCard = undefined;
                    currentCardUnderMouse = undefined;
                    onTrumpColorSelected(color);
                    }}
            />
        {/each}
    {/if}

    {#if extraOptions && extraOptions.type === "PlaceBetInput"}
        {#each betLaneInfoCards as card, cardIndex (card.element)}
            <SelectBetCardInHand
                    {currentSelectedCard}
                    betCard={card.element}
                    {calculations}

                    card={card.element}
                    cardIndex={card.cardInLaneIndex}
                    cardLaneIndex={card.laneIndex + extraLaneStartIndex}
                    containerWidth={containerBounds ? containerBounds.width : 1000}
                    {currentCardUnderMouse}
                    numberOfCardsInLane={card.numberOfElementsInLane}
                    onMouseEnter={(card) => currentCardUnderMouse = card}
                    onMouseLeave={(card) => { if (currentCardUnderMouse === card) { currentCardUnderMouse = undefined } }}
                    onSelect={(card) => currentSelectedCard = card }
                    onUnSelect={(card) => currentSelectedCard = undefined }
                    onFinallySelected={(card) => {
                    currentSelectedCard = undefined;
                    currentCardUnderMouse = undefined;
                    onSelectBet(card.number);} }
            />
        {/each}
    {/if}
</div>
