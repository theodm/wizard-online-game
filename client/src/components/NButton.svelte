<script lang="ts">
    import Spinner from "./Spinner.svelte";

    export let cssClass: string = "";
    export let onClick: () => Promise<void>;
    export let disabled: boolean;
    export let outline: boolean = false;

    export let color: ""
    let loading = false;

    export async function _onClick() {
        loading = true;

        try {
            await onClick();
        } finally {
            loading = false;
        }
    }

    $: classesOutline = outline ? "bg-white border border-blue-700 text-blue-700 hover:text-white" : "bg-blue-700 text-white"
</script>

<button type="button"
        disabled={disabled}
        on:click={_onClick}
        class="shadow-md hover:bg-blue-800 disabled:bg-gray-500 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-lg px-5 py-2.5 text-center mb-2 disabled:cursor-not-allowed {cssClass} {classesOutline}"
>
    {#if !loading}
        <slot></slot>
    {:else}
        <Spinner/>
    {/if}
</button>