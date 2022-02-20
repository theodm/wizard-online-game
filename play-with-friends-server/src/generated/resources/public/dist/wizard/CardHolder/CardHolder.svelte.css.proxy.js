// [snowpack] add styles to the page (skip if no document exists)
if (typeof document !== 'undefined') {
  const code = ".wizard.svelte-4jljhg{--card-width:6em;--card-height:calc(var(--card-width) * (22 / 14));--hand-gap-between-cards:calc(var(--card-width) * (6 / 14));--hand-move-up-offset:5em;font-family:Consolas}@media(min-width: 640px){.wizard.svelte-4jljhg{--card-width:6em}}@media(min-width: 768px){.wizard.svelte-4jljhg{--card-width:8em}}@media(min-width: 1024px){.wizard.svelte-4jljhg{--card-width:10em}}@media(min-width: 1280px){.wizard.svelte-4jljhg{--card-width:11em}}@media(min-width: 1536px){.wizard.svelte-4jljhg{--card-width:12em}}";

  const styleEl = document.createElement("style");
  const codeEl = document.createTextNode(code);
  styleEl.type = 'text/css';
  styleEl.appendChild(codeEl);
  document.head.appendChild(styleEl);
}