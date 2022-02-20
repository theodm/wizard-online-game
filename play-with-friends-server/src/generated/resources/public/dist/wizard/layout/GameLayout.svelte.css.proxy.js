// [snowpack] add styles to the page (skip if no document exists)
if (typeof document !== 'undefined') {
  const code = ".gr.svelte-1jfh3hk{grid-template-columns:65% 35%;grid-template-rows:-webkit-min-content auto -webkit-min-content;grid-template-rows:min-content auto min-content}";

  const styleEl = document.createElement("style");
  const codeEl = document.createTextNode(code);
  styleEl.type = 'text/css';
  styleEl.appendChild(codeEl);
  document.head.appendChild(styleEl);
}