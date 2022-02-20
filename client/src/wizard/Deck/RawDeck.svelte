<script lang="ts">

    import {RsNumberColorCard, RsWizardCard, RsWizardCardCmpTrumpColorEnum} from "../../generated/client";
    import Card from "../card/Card.svelte";
    import CardPlaceHolder from "../card/CardPlaceHolder.svelte";
    import RawCard from "../card/RawCard.svelte";

    export let obersteKarte: RsWizardCard | null;
    export let trumpColor: RsWizardCardCmpTrumpColorEnum | undefined;

    function convertTrumpColor(rsColor: RsWizardCardCmpTrumpColorEnum): "Red" | "Blue" | "Yellow" | "Green" | "Null" {
        switch (rsColor) {
            case RsWizardCardCmpTrumpColorEnum.Red:
                return "Red";
            case RsWizardCardCmpTrumpColorEnum.Blue:
                return "Blue";
            case RsWizardCardCmpTrumpColorEnum.Yellow:
                return "Yellow";
            case RsWizardCardCmpTrumpColorEnum.Green:
                return "Green";
            case RsWizardCardCmpTrumpColorEnum.None:
                return "Null";
        }
    }

    function _trumpMiddleText(trumpColor: RsWizardCardCmpTrumpColorEnum | undefined) {
        if (!trumpColor) {
            return "?"
        }

        if (trumpColor === RsWizardCardCmpTrumpColorEnum.None) {
            return "∅"
        }

        return ""
    }

    $: finalTrumpColor = trumpColor ? convertTrumpColor(trumpColor) : "Null";
    $: trumpMiddleText = _trumpMiddleText(trumpColor)
</script>

<div class="h-full flex flex-row items-center">
    <div class="p-1 flex flex-col">
        <h1 class="pb-1 text-h-xs text-center text-white">
            Stapel
        </h1>
        {#if obersteKarte}
            <Card cssClass="portrait:card-w-sm landscape:card-h-sm" card={obersteKarte}/>
        {:else}
            <CardPlaceHolder cssClass="portrait:card-w-sm landscape:card-h-sm"/>
        {/if}
    </div>
    <div class="p-1 flex flex-col">
        <h1 class="pb-1 text-h-xs text-center text-white">
            Farbe
        </h1>
        <RawCard cssClass="portrait:card-w-sm landscape:card-h-sm" cardColor={finalTrumpColor} textInTheMiddle={trumpMiddleText} textOnTheSides=""/>
    </div>
</div>

<style>
</style>
