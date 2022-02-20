<script lang="ts">
    import RawDeck from "./RawDeck.svelte";
    import {RsOpenWizardRound} from "../../generated/client";

    export let openWizardRound: RsOpenWizardRound;
</script>
<RawDeck obersteKarte={openWizardRound.immutableRoundState.trumpCard}
         trumpColor={openWizardRound.currentRoundStateForPlayer.trumpColor}/>