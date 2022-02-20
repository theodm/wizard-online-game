<script lang="ts">
    import RawHand from "./RawHand.svelte";
    import {
        RsRoundStateForPlayer,
        RsRoundStateForPlayerFirstRound,
        RsRoundStateForPlayerNormalRound,
        RsWizardCard, TrumpColor
    } from "../../generated/client";
    import {RsOpenWizardRound} from "../../generated/client/models/RsOpenWizardRound";
    import NewHand from "./NewHand/NewHand.svelte";
    import {PlaceBetInput, SelectTrumpColorInput} from "./NewHand/ExtraOptions";
    import {delay} from "../../utils/delay";

    export let onCardPlayed: (card: RsWizardCard | null) => Promise<void>;
    export let onTrumpColorSelected: (color: TrumpColor) => Promise<void>;
    export let onSelectBet: (bet: number) => Promise<void>;

    export let openWizardRound: RsOpenWizardRound;
    export let localPlayerPublicID: string;

    function cards(openWizardRound: RsOpenWizardRound) {
        if (openWizardRound.currentRoundStateForPlayer.type === "RsRoundStateForPlayerFirstRound") {
            return (openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound).cmpLocalPlayerHasPlayedCard ? [] : [];
        } else {
            return (openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerNormalRound).ownCards;
        }
    }


    /**
     * Wie viele Stiche wurden bisher angesagt?
     */
    function currentBets(roundState: RsRoundStateForPlayer) {
        const bets = Object.entries(roundState.bets)
            .filter((it) => it !== null)
            .map((it) => it[1])
            .reduce((a, b) => a + b);

        return bets;
    }

    /**
     * Ist der Spieler der letzte Spieler, der eine Wette abgeben muss?
     */
    function isLastInRound(roundStateForPlayer: RsRoundStateForPlayer, playerPublicID: string) {
        const bets = Object.entries(roundStateForPlayer.bets)
            .filter((it) => it[1] === null)

        if (bets.length > 1) {
            // Es sind noch mehrere Tipps abzugeben, daher ist der Spieler nicht
            // der letzte.
            return false;
        }

        if (bets.length === 0) {
            throw new Error("bets.length sollte an dieser Stelle niemals 0 sein.")
        }

        return bets[0][0] === playerPublicID;
    }

    /**
     * Welcher Wert darf nicht ausgewählt werden? Ein Wert darf nicht ausgewählt werden,
     * wenn der Spieler der letzte in der Reihe ist und das Spiel sonst aufgehen würde.
     */
    function ignoredBet(openWizardRound: RsOpenWizardRound) {
        const currentRoundState = openWizardRound.currentRoundStateForPlayer as RsRoundStateForPlayerFirstRound;

        if (isLastInRound(currentRoundState, localPlayerPublicID)) {
            return openWizardRound.immutableRoundState.numberOfCards - currentBets(currentRoundState);
        }

        return undefined;
    }

    $: currentPhase = openWizardRound.currentRoundStateForPlayer.phase;
    $: isLocalPlayersTurn = openWizardRound.currentRoundStateForPlayer.currentPlayer.userPublicID ===
        localPlayerPublicID;

    $: extraOptionsSelectTrumpColor = currentPhase === "SelectTrumpPhase" && isLocalPlayersTurn ? new SelectTrumpColorInput() : undefined;
    $: extraOptionsSelectBetInput = currentPhase === "BettingPhase" && isLocalPlayersTurn ? new PlaceBetInput(openWizardRound.immutableRoundState.numberOfCards, ignoredBet(openWizardRound)) : undefined;
    $: extraOptions = extraOptionsSelectTrumpColor ? extraOptionsSelectTrumpColor : (extraOptionsSelectBetInput ? extraOptionsSelectBetInput : undefined)

    $: {
        const isFirstRound = openWizardRound.currentRoundStateForPlayer.type === "RsRoundStateForPlayerFirstRound";

        if (isFirstRound && isLocalPlayersTurn && currentPhase === "PlayingPhase") {
            (async () => {
                await delay(1000)

                onCardPlayed(null)
            })()
        }
    }
</script>
<NewHand
        {onCardPlayed}
        trumpColor={openWizardRound.currentRoundStateForPlayer.trumpColor}
        onSelectBet={onSelectBet}
        cards={cards(openWizardRound)}
        onTrumpColorSelected={onTrumpColorSelected}
        extraOptions={extraOptions}
        disabled={!isLocalPlayersTurn}
/>