<script lang="ts">
    import {fade} from 'svelte/transition';

    export let error: undefined | {
        timestamp: Date,
        message: string,
    } = undefined;

    let prevError;
    let isDismissed = false;

    $: {
        if (prevError !== error) {
            isDismissed = false;
        }

        prevError = error;
    }

    export let cssClass = "";
</script>

{#if error && !isDismissed}
    <div id="alert-2" class={`flex justify-between w-full p-4 mb-4 bg-red-100 rounded-lg mt-6 ${cssClass}`}
         role="alert" transition:fade|local={{duration: 200}}>
        <div class="ml-3 text-sm font-medium text-red-700">
            {error.message}
        </div>
        <div class="flex justify-center items-center">
            <button type="button"
                    on:click={() => { isDismissed = true; }}
                    class="ml-auto -mx-1.5 -my-1.5 bg-red-100 text-red-500 rounded-lg focus:ring-2 focus:ring-red-400 p-1.5 hover:bg-red-200 inline-flex h-8 w-8"
                    data-collapse-toggle="alert-2" aria-label="Close">
                <span class="sr-only">Close</span>
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"
                     xmlns="http://www.w3.org/2000/svg">
                    <path fill-rule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clip-rule="evenodd"></path>
                </svg>
            </button>
        </div>
    </div>
{/if}