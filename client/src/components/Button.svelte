<script lang="typescript">
    import {createEventDispatcher} from "svelte";

    import Spinner from "./Spinner.svelte";

    export let isLoading = false;
    export let cssClass = "";

    const dispatch = createEventDispatcher<{
        /**
         * Lobby-Code!
         */
        onClick: void;
    }>();
</script>

<button
        type="button"
        class="{cssClass} px-4 py-2 font-medium text-white bg-indigo-600 border border-transparent rounded-md shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-opacity-50 focus:ring-indigo-500 text-md"
        on:click={() => dispatch("onClick")}
>
    {#if isLoading}
        <Spinner/>
    {/if}
    <slot></slot>
</button>
