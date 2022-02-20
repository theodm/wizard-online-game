<script lang="ts">
    import Card from "../../card/Card.svelte";
    import {RsCardInHand, RsWizardCard, RsWizardCardCmpTrumpColorEnum, TrumpColor} from "../../../generated/client";
    import {Calculations} from "./Calculations";
    import RawCard from "../../card/RawCard.svelte";

    export let color: TrumpColor;
    export let calculations: Calculations;

    export let containerWidth: number;
    export let numberOfCardsInLane: number;

    export let cardLaneIndex: number;
    export let cardIndex: number;

    export let onSelect: (card: TrumpColor) => void;
    export let onUnSelect: (card: TrumpColor) => void;
    export let onMouseEnter: (card: TrumpColor) => void;
    export let onMouseLeave: (card: TrumpColor) => void;
    export let onFinallySelected: (card: TrumpColor) => void;

    export let currentCardUnderMouse: TrumpColor | undefined;
    export let currentSelectedCard: TrumpColor | undefined;

    export let externallyDisabled: boolean = false;

    function convertTrumpColor(rsColor: TrumpColor): "Red" | "Blue" | "Yellow" | "Green" | "Null" {
        switch (rsColor) {
            case TrumpColor.Red:
                return "Red";
            case TrumpColor.Blue:
                return "Blue";
            case TrumpColor.Yellow:
                return "Yellow";
            case TrumpColor.Green:
                return "Green";
        }
    }

    $: isUnderMouse = currentCardUnderMouse === color;
    $: isCurrentlySelected = currentSelectedCard === color;
    $: isElevated = isUnderMouse || isCurrentlySelected;

    $: cardPositioning = calculations.cardPosition(cardLaneIndex, numberOfCardsInLane, cardIndex, isElevated)

    $: left = cardPositioning.left
    $: bottom = cardPositioning.bottom
    $: zIndex = 10 - cardLaneIndex
    $: width = cardPositioning.width

    $: otherCardSelected = currentSelectedCard && currentSelectedCard !== color
    $: disabled = otherCardSelected || externallyDisabled

    $: finalTrumpColor = convertTrumpColor(color)
</script>

<RawCard
        cssClass={`absolute ${!disabled ? "hover:cursor-pointer" : ""}`}
        onClick={() => !disabled ? (!isCurrentlySelected ? onSelect(color) : onFinallySelected(color)) : null }
      onClickOutside={isCurrentlySelected ? () => { onUnSelect(color); } : undefined}
      onMouseEnter={() => !disabled && onMouseEnter(color)}
      onMouseLeave={() => !disabled && onMouseLeave(color)}
      cardColor={finalTrumpColor}
      style={`
            left: ${left + "px"};
            bottom: ${bottom + "px"};

            z-index: ${zIndex};

            width: ${width + "px"};
        `}
        textInTheMiddle=""
        textOnTheSides=""
      overlay={disabled}/>