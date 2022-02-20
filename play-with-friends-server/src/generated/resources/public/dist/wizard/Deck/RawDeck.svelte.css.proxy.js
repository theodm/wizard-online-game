// [snowpack] add styles to the page (skip if no document exists)
if (typeof document !== 'undefined') {
  const code = ".wizard.svelte-5t4xwh{--card-width:3em}@media(min-width: 640px){.wizard.svelte-5t4xwh{--card-width:3em}}@media(min-width: 768px){.wizard.svelte-5t4xwh{--card-width:4em}}@media(min-width: 1024px){.wizard.svelte-5t4xwh{--card-width:4em}}@media(min-width: 1280px){.wizard.svelte-5t4xwh{--card-width:4em}}@media(min-width: 1536px){.wizard.svelte-5t4xwh{--card-width:4em}}";

  const styleEl = document.createElement("style");
  const codeEl = document.createTextNode(code);
  styleEl.type = 'text/css';
  styleEl.appendChild(codeEl);
  document.head.appendChild(styleEl);
}