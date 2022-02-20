module.exports = {
    "stories": [
        "../src/**/*.stories.mdx",
        "../src/**/*.stories.@(js|jsx|ts|tsx|svelte)"
    ],
    "addons": [
        "@storybook/addon-links",
        "@storybook/addon-essentials",
        "@storybook/addon-svelte-csf"
    ],
    "framework": "@storybook/svelte",
    "staticDirs": ['../public'],
    "svelteOptions": {
        "preprocess": require("../svelte.config.js").preprocess
    }
}