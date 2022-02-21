module.exports = {
    content: [
        "./src/**/*.{html,js,ts,svelte}",
        "./node_modules/flowbite/**/*.js"
    ],
    theme: {
        extend: {
            fontSize: {
                '2xs': ['.50rem', '0.75rem']
            },
        },
    },
    plugins: [
        require("@tailwindcss/forms"),
        require('daisyui'),
        require('tailwindcss-debug-screens'),
        require('flowbite/plugin')
    ],
    daisyui: {
        styled: true,
        themes: true,
        base: false,
        utils: true
    }
}
