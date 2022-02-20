<script lang="ts">
    import {fade} from 'svelte/transition';
    export let placeHolder: string = "";
    export let currentText: string;
    export let disabled: boolean = false;
    export let cssClass: string = "";
    export let onCurrentTextChanged: (newText: string) => string;

    export let validationOnceTriggered: boolean;

    export let validationFunction: (text: string) => {
        validates: boolean,
        message: string
    };

    let internalValidationOnceTriggered = false;
    let previousText: string = "";

    $: {
        if (validationFunction(previousText).validates && !validationFunction(currentText).validates) {
            internalValidationOnceTriggered = true;
        }

        previousText = currentText;
    }
    $: showValidation = (validationOnceTriggered || internalValidationOnceTriggered) && !validationFunction(currentText).validates
    $: invalidClasses = showValidation ? "border-red-500 focus:ring-red-500 focus:border-red-500" : ""
</script>


<input disabled={disabled}
        class={`py-2 bg-gray-50 border border-gray-300 text-gray-900 placeholder:text-gray-300 text-xl rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 block w-full disabled:cursor-not-allowed ${invalidClasses} ${cssClass}`}
       placeholder={placeHolder} type="text" value={currentText}
       on:input={(e) => onCurrentTextChanged(e.currentTarget.value)}/>
{#if showValidation}
    <div transition:fade|local class="text-sm mt-2 text-red-500">{validationFunction(currentText).message}</div>
{/if}