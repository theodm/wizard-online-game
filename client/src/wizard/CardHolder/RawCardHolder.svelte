<script lang="typescript">
    import Card from "../card/Card.svelte";

    import CardPlaceHolder from "../card/CardPlaceHolder.svelte";
    import type {CardHolderDef} from "./CardHolderDef"
    import {onMount} from "svelte";
    import {registerResizeChange} from "../dom/ResizeListener";
    import {fade} from "svelte/transition";
    import RawCard from "../card/RawCard.svelte";
    import {RsOpenWizardRound} from "../../generated/client";

    export let cards: CardHolderDef[];
    export let openWizardRound: RsOpenWizardRound;

    let container: HTMLElement;
    let containerBounds: DOMRectReadOnly | undefined = undefined

    onMount(() => {
        registerResizeChange(container, (newBounds) => containerBounds = newBounds);

    })

    function getMaxCardWidth(containerBounds: DOMRectReadOnly) {
        return (350 * containerBounds?.height) / 669
    }

    function getCardWidth(containerBounds: DOMRectReadOnly) {
        return Math.min(containerBounds?.width * 0.25, getMaxCardWidth(containerBounds));
    }

    const overlapY = 1 / 7;

    function getBetCardPos(card: CardHolderDef) {
        if (card.card && card.colorCard) {
            return 2;
        }

        if (card.colorCard || card.card) {
            return 1
        }

        return 0;
    }

    function getCardPos(card: CardHolderDef) {
        return 0;
    }

    function getColorCardPos(card: CardHolderDef) {
        if (card.card) {
            return 1;
        }

        return 0;
    }

    function getCardTop(containerBounds: DOMRectReadOnly, pos: number) {
        return getCardHeight(containerBounds) * overlapY * pos;
    }

    function getCardHeight(containerBounds: DOMRectReadOnly) {
        return getCardWidth(containerBounds) * (22 / 14)
    }

    $: obersteKarte = openWizardRound.immutableRoundState.trumpCard
    $: trumpColor = openWizardRound.currentRoundStateForPlayer.trumpColor
    $: numberOfRemainingCards = (60 - openWizardRound.immutableRoundState.numberOfCards * openWizardRound.immutableRoundState.players.length) - (openWizardRound.immutableRoundState.trumpCard ? 1 : 0)
</script>

<div class="relative w-full h-full flex flex-wrap justify-center items-center"
     bind:this={container}>

    {#each cards as card}
        <div class="flex flex-col items-center">
            <h1 class={`text-center transition-all ${card.isActive ? card.playerColorClass : ""}`} class:font-medium={card.isActive} style={`font-size: ${getCardWidth(containerBounds) * 0.10}px;`}
                >
                {card.playerName}
            </h1>

            <div class="relative">
                {#if card.numberCard !== undefined && card.numberCard !== null}
                    <RawCard
                            cssClass="absolute"
                            style={`z-index: 3; width: ${getCardWidth(containerBounds)}px; top: ${getCardTop(containerBounds, getBetCardPos(card))}px;`}
                            cardColor={"Null"}
                            textInTheMiddle={card.numberCard}
                            textOnTheSides={""}
                    />
                {/if}

                {#if card.colorCard !== undefined}
                    <RawCard
                            cssClass="absolute"
                            style={`z-index: 2; width: ${getCardWidth(containerBounds)}px; top: ${getCardTop(containerBounds, getColorCardPos(card))}px;`}
                            cardColor={card.colorCard}
                            textInTheMiddle={""}
                            textOnTheSides={""}
                    />
                {/if}

                {#if card.card}
                    <Card cssClass="absolute"
                          style={`z-index: 1; width: ${getCardWidth(containerBounds)}px; top: ${getCardTop(containerBounds, getCardPos(card))}px;`}
                          card={card.card}/>
                {/if}
                <CardPlaceHolder style={`width: ${getCardWidth(containerBounds)}px;`} middleText={card.middleText}/>
            </div>

            <div class="flex w-full justify-around" style={`padding: ${getCardWidth(containerBounds) * 0.04}px`}>
                {#if card.numOfBet && card.numOfBet !== "?"}
                    <div in:fade class="transition h-full flex items-center">
                        <div style={`width: ${getCardWidth(containerBounds) * 0.12}px; font-size: ${getCardWidth(containerBounds) * 0.08}px;`}
                             class="h-full text-center">
                            {card.numOfBet}
                        </div>
                        <img src="/dices.svg" style={`width: ${getCardWidth(containerBounds) * 0.12}px;`} class=""/>
                    </div>
                {/if}
                {#if card.numOfStiche}
                    <div in:fade class="transition h-full flex items-center">
                        <img src="/deck_of_cards.svg" style={`width: ${getCardWidth(containerBounds) * 0.12}px;`}
                             class=""/>
                        <div style={`width: ${getCardWidth(containerBounds) * 0.12}px; font-size: ${getCardWidth(containerBounds) * 0.08}px;`}
                             class="h-full text-center">
                            {card.numOfStiche}
                        </div>
                    </div>
                {/if}
            </div>


        </div>
    {/each}
    <div class="relative" style={`width: ${getCardWidth(containerBounds) / 2 * (obersteKarte && obersteKarte.type === "RsJoker" && trumpColor ? 1.50 : 1)}px;`}>

        {#if obersteKarte}
            <Card card={obersteKarte} style={`width: ${getCardWidth(containerBounds) / 2}px;`} middleText={""}/>
        {:else}
            <CardPlaceHolder style={`width: ${getCardWidth(containerBounds) / 2}px;`}/>
        {/if}

        {#if obersteKarte && obersteKarte.type === "RsJoker" && trumpColor}
            <RawCard
                    cssClass="absolute"
                    style={`z-index: 1; width: ${getCardWidth(containerBounds) / 2}px; top: 0; left: ${getCardWidth(containerBounds) / 4}px;`}
                    cardColor={trumpColor}
                    textInTheMiddle={""}
                    textOnTheSides={""}
            />
        {/if}

        <div class="transition flex w-full justify-around" style={`padding: ${getCardWidth(containerBounds) * 0.02}px`}>
            <div in:fade class="transition h-full flex items-center">
                <div style={`width: ${getCardWidth(containerBounds) * 0.06}px; font-size: ${getCardWidth(containerBounds) * 0.04}px;`}
                     class="h-full text-center">
                    {numberOfRemainingCards}
                </div>
                <img src="/playing-cards.svg" style={`width: ${getCardWidth(containerBounds) * 0.06}px;`} class=""/>
            </div>
            <div in:fade class="transition h-full flex items-center">
                <img src="/rounds.svg" style={`width: ${getCardWidth(containerBounds) * 0.06}px;`}
                     class=""/>
                <div style={`width: ${getCardWidth(containerBounds) * 0.06}px; font-size: ${getCardWidth(containerBounds) * 0.04}px;`}
                     class="h-full text-center">
                    {openWizardRound.immutableRoundState.numberOfCards}
                </div>
            </div>
        </div>
    </div>
    <div>

    </div>
</div>
