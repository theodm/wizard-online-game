<!--<script lang="ts">-->
<!--    import {createPopperActions} from 'svelte-popperjs';-->

<!--    export let content: string | false | undefined;-->
<!--    export let placement: "right" | "left" | "top" | "bottom" = "right";-->
<!--    const [popperRef, popperContent] = createPopperActions({-->
<!--        placement: "right"-->
<!--    });-->

<!--    const popperOptions = {-->
<!--        modifiers: [-->
<!--            {name: 'offset', options: {offset: [0, 16]}},-->
<!--        ],-->
<!--    };-->

<!--    let showTooltip = false;-->
<!--</script>-->

<!--{#if content}-->
<!--    <div-->
<!--            use:popperRef-->
<!--            on:mouseenter={() => showTooltip = true}-->
<!--            on:mouseleave={() => showTooltip = false}-->
<!--    >-->
<!--        <slot></slot>-->
<!--    </div>-->
<!--    <details use:popperContent={popperOptions}-->
<!--         data-tooltip-placement="right"-->
<!--         class={`bg-gray-900 text-sm font-medium black z-10 py-2 px-3 rounded-lg shadow-sm text-white transition-opacity tooltip duration-300`}-->
<!--         class:invisible={!showTooltip}-->
<!--         class:opacity-0={!showTooltip}-->
<!--         class:visible={showTooltip}-->
<!--         class:opacity-100={showTooltip}-->
<!--    >-->
<!--        {content}-->
<!--        <div-->
<!--                class={`tooltip-arrow`}-->
<!--                class:invisible={!showTooltip}-->
<!--                class:opacity-0={!showTooltip}-->
<!--                class:visible={showTooltip}-->
<!--                class:opacity-100={showTooltip}-->
<!--                class:tooltip-arrow-right={placement === "right"}-->
<!--                class:tooltip-arrow-left={placement === "left"}-->
<!--                class:tooltip-arrow-top={placement === "top"}-->
<!--                class:tooltip-arrow-bottom={placement === "bottom"}-->
<!--                data-popper-arrow></div>-->
<!--    </details>-->
<!--{:else}-->
<!--    <slot></slot>-->
<!--{/if}-->

<!--<style>-->
<!--    /* https://github.com/themesberg/flowbite/blob/649c725b918c1665d399f410569db9de77012434/plugin.js */-->
<!--    .tooltip-arrow, .tooltip-arrow:before {-->
<!--        position: absolute;-->
<!--        width: 8px;-->
<!--        height: 8px;-->
<!--        background: inherit;-->
<!--    }-->

<!--    .tooltip-arrow {-->
<!--        visibility: hidden;-->
<!--    }-->

<!--    .tooltip-arrow:before {-->
<!--        content: '';-->
<!--        visibility: visible;-->
<!--        transform: rotate(45deg);-->
<!--    }-->

<!--    .tooltip-arrow-top {-->
<!--        bottom: -8px;-->
<!--    }-->

<!--    .tooltip-arrow-bottom {-->
<!--        top: -8px;-->
<!--    }-->

<!--    .tooltip-arrow-left {-->
<!--        right: -8px;-->
<!--    }-->

<!--    .tooltip-arrow-right {-->
<!--        left: -8px;-->
<!--    }-->
<!--</style>-->