import { c as commonjsGlobal } from './common/_commonjsHelpers-6d0ad781.js';

// https://github.com/maxogden/websocket-stream/blob/48dc3ddf943e5ada668c31ccd94e9186f02fafbd/ws-fallback.js

var ws = null;

if (typeof WebSocket !== 'undefined') {
  ws = WebSocket;
} else if (typeof MozWebSocket !== 'undefined') {
  ws = MozWebSocket;
} else if (typeof commonjsGlobal !== 'undefined') {
  ws = commonjsGlobal.WebSocket || commonjsGlobal.MozWebSocket;
} else if (typeof window !== 'undefined') {
  ws = window.WebSocket || window.MozWebSocket;
} else if (typeof self !== 'undefined') {
  ws = self.WebSocket || self.MozWebSocket;
}

var browser = ws;

export default browser;
