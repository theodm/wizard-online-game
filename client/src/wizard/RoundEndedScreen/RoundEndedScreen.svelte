<script lang="typescript">
    import {getBetForRoundAndPlayer, getPointsSumForRoundAndPlayer} from "../../client/RsWizardGameStateForPlayer";

    import type {
        RsRoundStateForPlayerFirstRound,
        RsRoundStateForPlayerNormalRound,
        RsWizardGameStateForPlayer,
    } from "../../generated/client";

    export let wizardState: RsWizardGameStateForPlayer;
    export let disabled: boolean;
    export let onNextRoundStarted: () => Promise<void>;

    $: openWizardRound = wizardState.openWizardRound!;
    $: roundStateForPlayer = openWizardRound.currentRoundStateForPlayer as
        | RsRoundStateForPlayerNormalRound
        | RsRoundStateForPlayerFirstRound;

</script>

<div class="bg-white border-1 border-black h-full">
    <table class="w-full">
        <thead class="">
        <th class="px-4 py-2 border border-black">#</th>
        {#each openWizardRound.immutableRoundState.players as player}
            <th class="px-4 py-2 border border-black" colspan="2">{player.userName}</th>
        {/each}
        </thead>
        {#each Array(openWizardRound.immutableRoundState.numberOfCards) as _, i}
            <tr>
                <td class="px-4 py-2 text-center border border-black">{i + 1}</td>

                {#each openWizardRound.immutableRoundState.players as player}
                    <td class="px-4 py-2 text-center border border-black">{getPointsSumForRoundAndPlayer(wizardState, player.userPublicID, i + 1)}</td>
                    <td class="px-4 py-2 text-center border border-black">{getBetForRoundAndPlayer(wizardState, player.userPublicID, i + 1)}</td>
                {/each}
            </tr>
        {/each}
    </table>
    <div class="flex justify-center w-full mt-10">
        <button class="btn btn-primary" disabled={disabled} on:click={() => onNextRoundStarted()}>Weiter</button>
    </div>
</div>
