<script lang="typescript">
    export let actionText: string;
</script>

<div class="flex flex-col h-full">
    <div class="flex-0 items-center p-2">
        <div class="w-full h-full text-h-xs text-center text-white">{actionText}</div>
    </div>

    <div class="flex-1 items-center justify-center flex portrait:flex-col landscape:flex-row ">
        <slot name="cards"/>
        <slot name="deck"/>
    </div>

    <div class="flex-1 items-center justify-center">
        <slot name="input"/>
    </div>
    <div class="flex-0 w-full justify-center">
        <slot name="hand"/>
    </div>
</div>
