<script lang="typescript">
    export let maxBet: number;
    export let ignoredBet: number | undefined;
    export let disabled: boolean;

    export let onPlaceBet: (bet: number) => Promise<void>;
</script>

<div class="flex justify-center ">
    <div>
        <h1 class="flex justify-center p-1 text-h-xs text-white">Tipp abgeben</h1>
        <div class="w-full flex items-center justify-center">
            <div class="flex flex-wrap justify-center items-center w-5/12">
                {#each Array(16) as _, i}
                    <button
                            class="m-1 text-black bg-white border-2 btn btn-square portrait:btn-w-sm landscape:btn-h-sm hover:bg-gray-600 hover:text-white"
                            disabled={i > maxBet || i === ignoredBet || disabled}
                            on:click={() => onPlaceBet(i)}
                    >{i}</button
                    >
                {/each}
            </div>
        </div>
    </div>
</div>
