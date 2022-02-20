<script lang="typescript">
    import {fade} from 'svelte/transition';
    import {clickOutside} from '../../utils/clickOutside';

    export let onMouseEnter: (() => void) | undefined;
    export let onMouseLeave: (() => void) | undefined;
    export let onClick: (() => void) | undefined;
    export let onClickOutside: (() => void) | undefined;

    export let cardColor: "Red" | "Green" | "Blue" | "Yellow" | "Null" | "Joker";
    export let textInTheMiddle: string;
    export let textOnTheSides: string;
    export let cssClass: string = "";

    export let overlay: boolean;

    function getHTMLColor(cardColor: "Red" | "Green" | "Blue" | "Yellow" | "Null" | "Joker"): string {
        switch (cardColor) {
            case "Blue":
                return "#60A5FA";
            case "Green":
                return "#6EE7B7";
            case "Yellow":
                return "#FCD34D";
            case "Red":
                return "#F87171";
            case "Null":
            case "Joker":
                return "#FFFFFF";
        }

        throw new Error("Die übergebene Farbe " + cardColor + " ist nicht gültig.");
    }

    function getTextColor(cardColor: "Red" | "Green" | "Blue" | "Yellow" | "Null" | "Joker"): string {
        switch (cardColor) {
            case "Blue":
            case "Green":
            case "Yellow":
            case "Red":
                return "#FFFFFF";
            case "Null":
            case "Joker":
                return "#000000";
        }

        throw new Error("Die übergebene Farbe " + cardColor + " ist nicht gültig.");
    }

    $: color = getHTMLColor(cardColor);
    $: textColor = getTextColor(cardColor);
</script>

<!--<style>-->
<!--    svg {-->
<!--        /*width: var(&#45;&#45;card-width);*/-->
<!--        /*height: calc(var(&#45;&#45;card-width) * (22 / 14));*/-->
<!--    }-->
<!--</style>-->
<style global>
    .small {
        font: 10px Consolas;
    }

    .big {
        font: 15px Consolas;
    }

    .card {
        stroke: black;
    }
</style>
<svg transition:fade|local={{duration: 100}} use:clickOutside on:click_outside={onClickOutside} class={"transition-all " + cssClass} viewBox="0 0 56 88" xmlns="http://www.w3.org/2000/svg" on:click={onClick} on:mouseenter={onMouseEnter} on:mouseleave={onMouseLeave} {...$$restProps}>
    <defs>
        <pattern id="muster2" x="8" y="8" width="20" height="20" patternUnits="userSpaceOnUse">
            <circle cx="10" cy="10" r="9" fill="#3983ab"/>
        </pattern>
    </defs>
    <rect class="card" x="1" y="1" width="54" height="86" fill={color} rx="5" ry="5"/>
    <text x="4" y="12" class="small" fill={textColor}>{textOnTheSides}</text>
    <text x="52" y="12" class="small" text-anchor="end" fill={textColor}>{textOnTheSides}</text>
    <text x="27" y="46" class="big" text-anchor="middle" fill={textColor}>{textInTheMiddle}</text>
    <text x="4" y="83" class="small" fill={textColor}>{textOnTheSides}</text>
    <text x="52" y="83" class="small" text-anchor="end" fill={textColor}>{textOnTheSides}</text>
    <rect class="card transition-all duration-1200" style={`opacity: ${overlay ? "50" : "0"}%`} x="1" y="1" width="54" height="86" fill="#000000" rx="5" ry="5"/>

</svg>
