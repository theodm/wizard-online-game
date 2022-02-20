
export function registerResizeChange(element: HTMLElement, onResize: (rect: DOMRectReadOnly) => void) {
    // https://stackoverflow.com/questions/40251082/an-event-or-observer-for-changes-to-getboundingclientrect
    onResize(element.getBoundingClientRect())
    const resizeObserver = new ResizeObserver(entries => {
        for (let entry of entries) {
            onResize(entry.contentRect);
        }
    });

    resizeObserver.observe(element)
}