<script lang="typescript">
    export let onSelectTrumpColor: (trumpColor: "Blue" | "Yellow" | "Green" | "Red") => Promise<void>;

    const colors = [
        {
            trumpColor: "Blue",
            cssColor: "bg-blue-400",
            cssHoverColor: "hover:bg-blue-600"
        },
        {
            trumpColor: "Yellow",
            cssColor: "bg-yellow-400",
            cssHoverColor: "hover:bg-yellow-600"
        },
        {
            trumpColor: "Green",
            cssColor: "bg-green-400",
            cssHoverColor: "hover:bg-green-600"
        },
        {
            trumpColor: "Red",
            cssColor: "bg-red-400",
            cssHoverColor: "hover:bg-red-600"
        },
    ];
</script>

<div class="flex justify-center">
    <div>
        <h1 class="flex justify-center p-2 text-lg text-white">Trumpffarbe wählen</h1>
        <div class="flex flex-wrap justify-center w-96">
            {#each colors as color}
                <button
                        class={`${color.cssColor} ${color.cssHoverColor} m-2 text-white border-2 btn btn-square btn-lg w-16 h-16 `}
                        on:click={() => onSelectTrumpColor(color.trumpColor)}></button
                >
            {/each}
        </div>
    </div>
</div>
