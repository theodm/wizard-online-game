
export function f() {


}
// import {createPopper} from '@popperjs/core';
// // @ts-ignore
// import TooltipElement from "./TooltipElement.svelte";
//
// export function TTooltip (element: HTMLElement) {
//     function getOrCreateTooltipElement(
//         showTooltip: boolean,
//         placement: "right" | "left" | "top" | "bottom",
//         content: string
//     ) {
//         let tooltipComponent = new TooltipElement({
//             props: {
//                 showTooltip: showTooltip,
//                 placement: placement,
//                 content: content
//             },
//             target: document.body,
//         });
//
//         return tooltipComponent
//     }
//
//     const tooltipComponent = getOrCreateTooltipElement(
//         true,
//         "right",
//         "Test"
//     );
//
//     createPopper(element, document.getElementById("global-tooltip")!, {
//         modifiers: [
//             {
//                 name: 'offset',
//                 options: {
//                     offset: [0, 8],
//                 },
//             },
//         ],
//     });
//
//     function mouseEnter() {
//
//     }
//
//     function mouseLeave() {
//
//     }
//
//     element.addEventListener("onmouseenter", mouseEnter)
//     element.addEventListener("onmouseleave", mouseLeave)
// }