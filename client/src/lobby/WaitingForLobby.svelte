<script lang="ts">
    import Spinner from "../components/Spinner.svelte";

    export let lobbyCode: string;
</script>
<div class="flex h-screen w-screen justify-center items-center">
    <Spinner tailwindTextColor="text-black"/>

    <div class="pl-4">Lobby {lobbyCode} wird geladen...</div>
</div>