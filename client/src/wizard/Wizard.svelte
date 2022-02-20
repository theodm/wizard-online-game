<script lang="ts">
    import WizardUI from "./WizardUI.svelte";
    import {LobbySocket} from "../lobby/LobbySocket";
    import {PlayerStore} from "../session/PlayerStore";
    import {RsWizardGameStateForPlayer} from "../generated/client";
    import {delayWhen} from "rxjs";

    export let lobbySocket: LobbySocket;
    export let playerStore: PlayerStore;

    let wizardState: RsWizardGameStateForPlayer;
    const wizardSocket = lobbySocket.wizard();

    lobbySocket
        .wizardStream()
        .subscribe(it => wizardState = it);


</script>
<WizardUI localPlayerPublicID={playerStore.getPublicID()} wizardState={wizardState} onCardPlayed={(card) => {
    if (card) {
        wizardSocket.playCard(card)
    } else {
        wizardSocket.playSingleCard()
    }
}} onNextRoundStarted={() => wizardSocket.finishRound()} onPlaceBet={(bet) => wizardSocket.placeBet(bet)}
          onSelectTrumpColor={(trumpColor) => wizardSocket.selectTrumpColor(trumpColor)}/>