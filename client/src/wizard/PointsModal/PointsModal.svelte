<script lang="ts">
    import {Player, PlayerResultInRound, Round} from "./FnPointsModal";
    import NButton from "../../components/NButton.svelte";
    import ModalWrapper from "./ModalWrapper.svelte";

    export let onBackToGame: () => Promise<void>;
    export let onStartNextRound: () => Promise<void>;
    export let mode: "Close" | "NextRound";

    export let allowedToStartNextRound: boolean = true;

    export let players: Player[];
    export let data: Round[];
</script>
<div>
    <ModalWrapper>
        <div class="w-full h-full flex justify-center lg:my-5 lg:h-auto">
            <div class="overflow-x-hidden bg-white w-full lg:w-3/6 lg:p-3">
                <table class="table-fixed w-full">
                    <thead class="bg-gray-100">
                    <tr>
                        <th class="py-1 lg:py-3 text-xs sm:text-sm font-medium tracking-wider text-gray-700 uppercase">#
                        </th>
                        {#each players as player}
                            <th class="verticalTableHeader lg:py-3 transition-all overflow-hidden whitespace-nowrap origin-center py-1 rotate-90 sm:rotate-0 text-xs sm:text-sm lg:text-md font-medium tracking-wider text-gray-700 uppercase text-center"
                                colspan="2">{player.name}</th>
                        {/each}
                    </tr>
                    </thead>
                    <tbody>
                    {#each data as round}
                        <tr class="border-b odd:bg-white even:bg-gray-50 ">
                            <td class="py-1 lg:py-3 font-medium text-center text-xs sm:text-sm lg:text-md font-medium text-gray-900 whitespace-nowrap">
                                {round.numberOfRound}
                            </td>
                            {#each players as player}
                                <td class:font-medium={round.players[player.playerPublicID].highestInRound}
                                    class="py-1 text-center border-l border-gray-300 text-xs sm:text-sm lg:text-md text-gray-900 whitespace-nowrap">
                                    {round.players[player.playerPublicID].sumOfPoints}
                                </td>
                                <td class="border-r lg:py-3 border-gray-300 text-center py-1 text-xs sm:text-sm lg:text-md  text-gray-900 whitespace-nowrap"
                                    class:bg-red-200={!round.players[player.playerPublicID].betWasCorrect}>
                                    {round.players[player.playerPublicID].bet}
                                </td>
                            {/each}
                        </tr>
                    {/each}

                    <tr>
                        <td class="py-1 lg:py-3 text-xs sm:text-sm font-medium tracking-wider text-gray-700 uppercase"></td>
                        {#each players as player}
                            <td class="lg:py-3 transition-all overflow-hidden whitespace-nowrap py-1 text-md font-medium tracking-wider text-gray-700 uppercase text-center"
                                class:border={player.rank <= 3}
                                colspan="2">{player.rank === 1 ? "🥇" : (player.rank === 2 ? "🥈" : (player.rank === 3 ? "🥉" : ""))}</td>
                        {/each}
                    </tr>
                    </tbody>
                </table>
                <div class="mt-4 flex justify-center items-center">
                    {#if mode === "Close"}
                        <NButton onClick={onBackToGame}>Zurück zum Spiel</NButton>
                    {:else }
                        <NButton onClick={onStartNextRound} disabled={!allowedToStartNextRound}>Nächste Runde starten</NButton>
                    {/if}
                </div>
            </div>
        </div>
    </ModalWrapper>
</div>
<style>
    @media (max-width: 640px) {
        .verticalTableHeader:before {
            content: '';
            padding-top: 110%; /* takes width as reference, + 10% for faking some extra padding */
            display: inline-block;
            vertical-align: middle;
        }
    }
</style>