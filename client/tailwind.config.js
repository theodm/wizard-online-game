module.exports = {
    content: [
        "./src/**/*.{html,js,ts,svelte}"
    ],
    theme: {
        extend: {
            fontSize: {
                '2xs': ['.50rem', '0.75rem']
            },
            // screens: {
            //     'portrait': {'raw': '(orientation: portrait)'},
            //     'landscape': {'raw': '(orientation: landscape)'},
            // }
        },
    },
    plugins: [
        require("@tailwindcss/forms"),
        require('daisyui'),
        require('tailwindcss-debug-screens'),
    ],
    daisyui: {
        styled: true,
        themes: true,
        base: false,
        utils: true
    }
}
