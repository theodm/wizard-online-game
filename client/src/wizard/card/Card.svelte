<script lang="ts">
    import {RsNumberColorCard, RsNumberColorCardColorEnum, RsWizardCard} from "../../generated/client";

    import RawCard from "./RawCard.svelte";

    export let onMouseEnter: (() => void) | undefined;
    export let onMouseLeave: (() => void) | undefined;
    export let onClick: (() => void) | undefined;
    export let onClickOutside: (() => void) | undefined;

    export let card: RsWizardCard;
    export let cssClass: string = "";

    export let overlay: boolean;

    function _cardColor(card: RsWizardCard): "Red" | "Green" | "Blue" | "Yellow" | "Null" | "Joker" {
        function convertColor(rsColor: RsNumberColorCardColorEnum) {
            switch (rsColor) {
                case "Red":
                    return "Red";
                case "Blue":
                    return "Blue";
                case "Yellow":
                    return "Yellow";
                case "Green":
                    return "Green";
            }

            throw new Error("Die Farbe " + rsColor + " ist ungültig.")
        }

        switch (card.type) {
            case "RsNumberColorCard":
                return convertColor((card as RsNumberColorCard).color);
            case "RsNull":
                return "Null";
            case "RsJoker":
                return "Joker";
        }

        throw new Error("Der Type " + card.type + " ist ungültig.")
    }

    function _cardText(card: RsWizardCard): string {
        switch (card.type) {
            case "RsNumberColorCard":
                return "" + (card as RsNumberColorCard).number;
            case "RsNull":
                return "N";
            case "RsJoker":
                return "J";
        }

        throw new Error("Der Type " + card.type + " ist ungültig.")
    }


    $: cardColor = _cardColor(card)
    $: cardText = _cardText(card)
</script>

<RawCard
        {cssClass}
        cardColor={cardColor}
        textInTheMiddle={cardText}
        textOnTheSides={cardText}
        {overlay}
        {onMouseEnter}
        {onMouseLeave}
        {onClick}
        {onClickOutside}
        {...$$restProps}
/>
