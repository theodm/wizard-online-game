<script lang="typescript">
    import {fade} from 'svelte/transition';

    export let middleText = "";
    export let cssClass = "";

    export let strokeColor = "#000000";

</script>
<svg
        transition:fade|local={{duration: 100}}
        class={"transition-all " + cssClass}
        viewBox="0 0 56 88"
        xmlns="http://www.w3.org/2000/svg"
        {...$$restProps}>
    <defs>
    </defs>
    <style>
        .small {
            font: 10px Consolas;
        }

        .big {
            font: 15px Consolas;
        }

        .card-dotted {
            stroke: #000000;
            stroke-width: 1;

            /*stroke-dasharray: 5;*/
        }
    </style>
    <rect
            stroke={strokeColor}
            class="card-dotted"
            x="1"
            y="1"
            width="54"
            height="86"
            fill="white"
            rx="5"
            ry="5"/>
    <text x="27" y="46" class="big" text-anchor="middle" fill="black">{middleText}</text>
</svg>
