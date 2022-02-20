<script lang="ts">
    import Card from "../../card/Card.svelte";
    import {RsCardInHand, RsWizardCard} from "../../../generated/client";
    import {Calculations} from "./Calculations";

    export let card: RsCardInHand;
    export let calculations: Calculations;

    export let containerWidth: number;
    export let numberOfCardsInLane: number;

    export let cardLaneIndex: number;
    export let cardIndex: number;

    export let onSelect: (card: RsCardInHand) => void;
    export let onUnSelect: (card: RsCardInHand) => void;
    export let onMouseEnter: (card: RsCardInHand) => void;
    export let onMouseLeave: (card: RsCardInHand) => void;
    export let onFinallySelected: (card: RsCardInHand) => void;

    export let currentCardUnderMouse: RsCardInHand | undefined;
    export let currentSelectedCard: RsCardInHand | undefined;

    export let externallyDisabled: boolean = false;

    $: isUnderMouse = currentCardUnderMouse === card;
    $: isCurrentlySelected = currentSelectedCard === card;
    $: isElevated = isUnderMouse || isCurrentlySelected;

    $: cardPositioning = calculations.cardPosition(cardLaneIndex, numberOfCardsInLane, cardIndex, isElevated)

    $: left = cardPositioning.left
    $: bottom = cardPositioning.bottom
    $: zIndex = 10 - cardLaneIndex
    $: width = cardPositioning.width

    $: notAllowedToPlay = !card.cmpAllowedToPlay
    $: otherCardSelected = currentSelectedCard && currentSelectedCard !== card
    $: disabled = notAllowedToPlay || otherCardSelected || externallyDisabled
</script>

<Card cssClass={`absolute ${!disabled ? "hover:cursor-pointer" : ""}`}
      onClick={() => !disabled ? (!isCurrentlySelected ? onSelect(card) : onFinallySelected(card)) : null }
      onClickOutside={isCurrentlySelected ? () => { onUnSelect(card); } : undefined}
      onMouseEnter={() => !disabled && onMouseEnter(card)}
      onMouseLeave={() => !disabled && onMouseLeave(card)}

      style={`
            left: ${left + "px"};
            bottom: ${bottom + "px"};

            z-index: ${zIndex};

            width: ${width + "px"};
        `} card={card.wizardCard}
      overlay={disabled}/>