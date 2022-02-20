<script lang="ts">
    import {ApiClient} from "../client/ApiClient";
    import {PlayerStore} from "../session/PlayerStore";
    import WaitingForLobby from "./WaitingForLobby.svelte";
    import ScLobby from "./ScLobby.svelte";

    export let lobbyCode: string;
    export let apiClient: ApiClient;
    export let playerStore: PlayerStore | undefined;
</script>
{#if !playerStore}
    <WaitingForLobby {lobbyCode}/>
{:else}
    <ScLobby {lobbyCode} {apiClient} {playerStore}/>
{/if}



