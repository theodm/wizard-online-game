import './GameLayout.svelte.css.proxy.js';
/* src\wizard\layout\GameLayout.svelte generated by Svelte v3.44.3 */
import {
	SvelteComponent,
	append,
	attr,
	create_slot,
	detach,
	element,
	get_all_dirty_from_scope,
	get_slot_changes,
	init,
	insert,
	safe_not_equal,
	space,
	transition_in,
	transition_out,
	update_slot_base
} from "../../../_snowpack/pkg/svelte/internal.js";

const get_playerHand_slot_changes = dirty => ({});
const get_playerHand_slot_context = ctx => ({});
const get_stichField_slot_changes = dirty => ({});
const get_stichField_slot_context = ctx => ({});
const get_titleBar_slot_changes = dirty => ({});
const get_titleBar_slot_context = ctx => ({});

// (12:2) {#if false}
function create_if_block(ctx) {
	let current;
	const default_slot_template = /*#slots*/ ctx[1].default;
	const default_slot = create_slot(default_slot_template, ctx, /*$$scope*/ ctx[0], null);

	return {
		c() {
			if (default_slot) default_slot.c();
		},
		m(target, anchor) {
			if (default_slot) {
				default_slot.m(target, anchor);
			}

			current = true;
		},
		p(ctx, dirty) {
			if (default_slot) {
				if (default_slot.p && (!current || dirty & /*$$scope*/ 1)) {
					update_slot_base(
						default_slot,
						default_slot_template,
						ctx,
						/*$$scope*/ ctx[0],
						!current
						? get_all_dirty_from_scope(/*$$scope*/ ctx[0])
						: get_slot_changes(default_slot_template, /*$$scope*/ ctx[0], dirty, null),
						null
					);
				}
			}
		},
		i(local) {
			if (current) return;
			transition_in(default_slot, local);
			current = true;
		},
		o(local) {
			transition_out(default_slot, local);
			current = false;
		},
		d(detaching) {
			if (default_slot) default_slot.d(detaching);
		}
	};
}

function create_fragment(ctx) {
	let div6;
	let t0;
	let div5;
	let div0;
	let t1;
	let div1;
	let t2;
	let div4;
	let div3;
	let div2;
	let current;
	let if_block = false && create_if_block(ctx);
	const titleBar_slot_template = /*#slots*/ ctx[1].titleBar;
	const titleBar_slot = create_slot(titleBar_slot_template, ctx, /*$$scope*/ ctx[0], get_titleBar_slot_context);
	const stichField_slot_template = /*#slots*/ ctx[1].stichField;
	const stichField_slot = create_slot(stichField_slot_template, ctx, /*$$scope*/ ctx[0], get_stichField_slot_context);
	const playerHand_slot_template = /*#slots*/ ctx[1].playerHand;
	const playerHand_slot = create_slot(playerHand_slot_template, ctx, /*$$scope*/ ctx[0], get_playerHand_slot_context);

	return {
		c() {
			div6 = element("div");
			if (if_block) if_block.c();
			t0 = space();
			div5 = element("div");
			div0 = element("div");
			if (titleBar_slot) titleBar_slot.c();
			t1 = space();
			div1 = element("div");
			if (stichField_slot) stichField_slot.c();
			t2 = space();
			div4 = element("div");
			div3 = element("div");
			div2 = element("div");
			if (playerHand_slot) playerHand_slot.c();
			attr(div0, "class", "col-span-2 row-span-1");
			attr(div1, "class", "col-span-2 row-span-1");
			attr(div2, "class", "flex items-center self-end justify-center w-full");
			attr(div3, "class", "flex");
			attr(div4, "class", "col-span-2 row-span-1 bg-red-700");
			attr(div5, "class", "grid w-full h-full gr svelte-1jfh3hk");
			attr(div6, "class", "relative w-full h-screen");
		},
		m(target, anchor) {
			insert(target, div6, anchor);
			if (if_block) if_block.m(div6, null);
			append(div6, t0);
			append(div6, div5);
			append(div5, div0);

			if (titleBar_slot) {
				titleBar_slot.m(div0, null);
			}

			append(div5, t1);
			append(div5, div1);

			if (stichField_slot) {
				stichField_slot.m(div1, null);
			}

			append(div5, t2);
			append(div5, div4);
			append(div4, div3);
			append(div3, div2);

			if (playerHand_slot) {
				playerHand_slot.m(div2, null);
			}

			current = true;
		},
		p(ctx, [dirty]) {
			if (false) if_block.p(ctx, dirty);

			if (titleBar_slot) {
				if (titleBar_slot.p && (!current || dirty & /*$$scope*/ 1)) {
					update_slot_base(
						titleBar_slot,
						titleBar_slot_template,
						ctx,
						/*$$scope*/ ctx[0],
						!current
						? get_all_dirty_from_scope(/*$$scope*/ ctx[0])
						: get_slot_changes(titleBar_slot_template, /*$$scope*/ ctx[0], dirty, get_titleBar_slot_changes),
						get_titleBar_slot_context
					);
				}
			}

			if (stichField_slot) {
				if (stichField_slot.p && (!current || dirty & /*$$scope*/ 1)) {
					update_slot_base(
						stichField_slot,
						stichField_slot_template,
						ctx,
						/*$$scope*/ ctx[0],
						!current
						? get_all_dirty_from_scope(/*$$scope*/ ctx[0])
						: get_slot_changes(stichField_slot_template, /*$$scope*/ ctx[0], dirty, get_stichField_slot_changes),
						get_stichField_slot_context
					);
				}
			}

			if (playerHand_slot) {
				if (playerHand_slot.p && (!current || dirty & /*$$scope*/ 1)) {
					update_slot_base(
						playerHand_slot,
						playerHand_slot_template,
						ctx,
						/*$$scope*/ ctx[0],
						!current
						? get_all_dirty_from_scope(/*$$scope*/ ctx[0])
						: get_slot_changes(playerHand_slot_template, /*$$scope*/ ctx[0], dirty, get_playerHand_slot_changes),
						get_playerHand_slot_context
					);
				}
			}
		},
		i(local) {
			if (current) return;
			transition_in(if_block);
			transition_in(titleBar_slot, local);
			transition_in(stichField_slot, local);
			transition_in(playerHand_slot, local);
			current = true;
		},
		o(local) {
			transition_out(if_block);
			transition_out(titleBar_slot, local);
			transition_out(stichField_slot, local);
			transition_out(playerHand_slot, local);
			current = false;
		},
		d(detaching) {
			if (detaching) detach(div6);
			if (if_block) if_block.d();
			if (titleBar_slot) titleBar_slot.d(detaching);
			if (stichField_slot) stichField_slot.d(detaching);
			if (playerHand_slot) playerHand_slot.d(detaching);
		}
	};
}

function instance($$self, $$props, $$invalidate) {
	let { $$slots: slots = {}, $$scope } = $$props;
	"use strict";

	$$self.$$set = $$props => {
		if ('$$scope' in $$props) $$invalidate(0, $$scope = $$props.$$scope);
	};

	return [$$scope, slots];
}

class GameLayout extends SvelteComponent {
	constructor(options) {
		super();
		init(this, options, instance, create_fragment, safe_not_equal, {});
	}
}

export default GameLayout;