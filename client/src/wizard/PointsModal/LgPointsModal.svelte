<script lang="ts">
    import {RsOpenWizardRound, RsWizardGameStateForPlayer} from "../../generated/client";
    import {getBetForRoundAndPlayer, getPointsSumForRoundAndPlayer} from "../../client/RsWizardGameStateForPlayer";
    import {calcRoundsAndPlayers} from "./FnPointsModal";
    import PointsModal from "./PointsModal.svelte";

export let wizardGameStateForPlayer: RsWizardGameStateForPlayer;
export let localPlayerPublicID: string;

export let onBackToGame: () => Promise<void>;
export let onStartNextRound: () => Promise<void>;

export let mode: "Close" | "NextRound";

$: roundsAndPlayers = calcRoundsAndPlayers(wizardGameStateForPlayer)
$: rounds = roundsAndPlayers[0]
$: players = roundsAndPlayers[1]
$: allowedToStartNextRound = wizardGameStateForPlayer.openWizardRound!.currentRoundStateForPlayer.currentPlayer.userPublicID !== localPlayerPublicID
</script>
<PointsModal data={rounds} players={players} {mode} {onBackToGame} {onStartNextRound}/>