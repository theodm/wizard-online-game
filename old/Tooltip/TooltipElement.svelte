<!--<script lang="ts">-->
<!--    export let showTooltip: boolean;-->
<!--    export let placement: "right" | "left" | "top" | "bottom";-->
<!--    export let content: string;-->

<!--</script>-->
<!--<div id="global-tooltip"-->
<!--     class={`bg-gray-900 text-sm font-medium black z-10 py-2 px-3 rounded-lg shadow-sm text-white transition-opacity tooltip duration-300`}-->
<!--         class:invisible={!showTooltip}-->
<!--         class:opacity-0={!showTooltip}-->
<!--         class:visible={showTooltip}-->
<!--         class:opacity-100={showTooltip}-->
<!--&gt;-->
<!--    {content}-->
<!--    <div-->
<!--            class={`tooltip-arrow`}-->
<!--            class:invisible={!showTooltip}-->
<!--            class:opacity-0={!showTooltip}-->
<!--            class:visible={showTooltip}-->
<!--            class:opacity-100={showTooltip}-->
<!--            class:tooltip-arrow-right={placement === "right"}-->
<!--            class:tooltip-arrow-left={placement === "left"}-->
<!--            class:tooltip-arrow-top={placement === "top"}-->
<!--            class:tooltip-arrow-bottom={placement === "bottom"}-->
<!--            data-popper-arrow></div>-->
<!--</div>-->

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