<script lang="ts">

    import {fade} from 'svelte/transition';
    import {RsShortLobbyInfo} from "../generated/client";
    import Spinner from "../components/Spinner.svelte";

    export let disabled: boolean = false;
    export let currentLobbies: RsShortLobbyInfo[] | "Loading" | "Error";
    export let onLobbyJoinClicked: (lobbyCode: string) => Promise<void>;

    function lobbyDescription(lobbyInfo: RsShortLobbyInfo) {
        return lobbyInfo.players.map(it => it.userName).reduce((a, b) => a + ", " + b)
    }

    function lobbyDisabled(lobbyInfo: RsShortLobbyInfo): string | false {
        if (lobbyInfo.isInGame) {
            return "Spiel ist bereits gestartet.";
        }

        if (lobbyInfo.players.length === 6) {
            return "Lobby ist bereits voll."
        }

        return false;
    }

    function lobbyColor(lobbyInfo: RsShortLobbyInfo) {
        if (lobbyInfo.isInGame) {
            return "bg-red-200";
        }

        if (lobbyInfo.players.length === 6) {
            return "bg-yellow-200";
        }

        return "bg-green-200";
    }

    function sortedLobbies(lobbies: RsShortLobbyInfo[]) {
        return lobbies.sort((a, b) => {
            if (b.isInGame && !a.isInGame) {
                return -1;
            }

            if (b.players.length > a.players.length) {
                return -1;
            }

            return 1;
        })
    }

    let currentLobbyCodeLoading: string | undefined = undefined

    async function _onLobbyJoinClicked(lobbyCode: string) {
        currentLobbyCodeLoading = lobbyCode
        try {
            await onLobbyJoinClicked(lobbyCode)
        } finally {
            currentLobbyCodeLoading = undefined;
        }
    }

</script>
<p class="flex justify-center py-2 pt-6 text-lg">Lobby beitreten</p>

<div class="w-full flex justify-center pt-4">
    <div class="w-80 text-gray-900 bg-white rounded-lg border border-gray-200">
        {#if currentLobbies === "Loading"}
            <div class="h-40 flex justify-center items-center bg-gray-100">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-black" xmlns="http://www.w3.org/2000/svg" fill="none"
                     viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor"
                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
            </div>
        {:else if currentLobbies === "Error"}
            <div class="h-40 flex justify-center items-center bg-gray-100">
                Fehler beim Laden.
            </div>
        {:else if currentLobbies.length === 0}
            <div class="h-40 flex justify-center items-center bg-gray-100">
                Keine offenen Lobbies.
            </div>
        {:else}
            {#each sortedLobbies(currentLobbies) as lobby}
                <button transition:fade|local={{duration: 200}}
                        data-tooltip-target="tooltip-default"
                        type="button"
                        disabled={lobbyDisabled(lobby) || currentLobbyCodeLoading || disabled}
                        on:click={() => _onLobbyJoinClicked(lobby.lobbyID)}
                        class="inline-flex relative items-center py-2 px-4 w-full first-of-type:rounded-t-lg first-of-type:rounded-b-lg border-b border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-2 focus:ring-blue-700 focus:text-blue-700 disabled:text-black disabled:cursor-not-allowed disabled:bg-gray-100">
                    <div class="flex justify-between items-center w-full">
                        <div class="truncate w-1/2 text-left">
                            {lobbyDescription(lobby)}
                        </div>
                        {#if lobby.lobbyID === currentLobbyCodeLoading}
                            <Spinner tailwindTextColor="text-primary"/>
                        {:else}
                            <div class={"h-6 rounded-md " + lobbyColor(lobby) + " text-2xs p-2 flex justify-center items-center"}>
                                {lobby.players.length} / 6
                            </div>
                        {/if}

                    </div>
                </button>
            {/each}

            <div id="tooltip-default" role="tooltip"
                 class="inline-block absolute invisible z-10 py-2 px-3 text-sm font-medium text-white bg-gray-900 rounded-lg shadow-sm opacity-0 transition-opacity duration-300 tooltip dark:bg-gray-700">
                Tooltip content
                <div class="tooltip-arrow" data-popper-arrow></div>
            </div>
        {/if}


    </div>
</div>