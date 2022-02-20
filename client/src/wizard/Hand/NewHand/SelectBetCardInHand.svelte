<script lang="ts">
    import Card from "../../card/Card.svelte";
    import {RsCardInHand, RsWizardCard, RsWizardCardCmpTrumpColorEnum, TrumpColor} from "../../../generated/client";
    import {Calculations,} from "./Calculations";
    import RawCard from "../../card/RawCard.svelte";
    import {BetCard} from "./ExtraOptions";

    export let betCard: BetCard;
    export let calculations: Calculations;

    export let containerWidth: number;
    export let numberOfCardsInLane: number;

    export let cardLaneIndex: number;
    export let cardIndex: number;

    export let onSelect: (card: BetCard) => void;
    export let onUnSelect: (card: BetCard) => void;
    export let onMouseEnter: (card: BetCard) => void;
    export let onMouseLeave: (card: BetCard) => void;
    export let onFinallySelected: (card: BetCard) => void;

    export let currentCardUnderMouse: BetCard | undefined;
    export let currentSelectedCard: BetCard | undefined;

    export let externallyDisabled: boolean = false;

    $: isUnderMouse = currentCardUnderMouse === betCard;
    $: isCurrentlySelected = currentSelectedCard === betCard;
    $: isElevated = isUnderMouse || isCurrentlySelected;

    $: cardPositioning = calculations.cardPosition(cardLaneIndex, numberOfCardsInLane, cardIndex, isElevated)

    $: left = cardPositioning.left
    $: bottom = cardPositioning.bottom
    $: zIndex = 10 - cardLaneIndex
    $: width = cardPositioning.width

    $: otherCardSelected = currentSelectedCard && currentSelectedCard !== betCard
    $: isNotAllowedBet = !betCard.allowedToChoose
    $: disabled = otherCardSelected || externallyDisabled || isNotAllowedBet
</script>

<RawCard
        cssClass={`absolute ${!disabled ? "hover:cursor-pointer" : ""}`}
        onClick={() => !disabled ? (!isCurrentlySelected ? onSelect(betCard) : onFinallySelected(betCard)) : null }
      onClickOutside={isCurrentlySelected ? () => { onUnSelect(betCard); } : undefined}
      onMouseEnter={() => !disabled && onMouseEnter(betCard)}
      onMouseLeave={() => !disabled && onMouseLeave(betCard)}
      cardColor={"Null"}
      style={`
            left: ${left + "px"};
            bottom: ${bottom + "px"};

            z-index: ${zIndex};

            width: ${width + "px"};
        `}
        textInTheMiddle={betCard.number}
        textOnTheSides={betCard.number}
      overlay={disabled}/>