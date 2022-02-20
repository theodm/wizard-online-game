
export function validateUserName(name: string) {
    if (name.trim().length == 0) {
        return {
            validates: false,
            message: "Hey, vergessen Sie nicht Ihren Namen auswählen."
        }
    }

    if (name.trim().length <= 2) {
        return {
            validates: false,
            message: "Hey, etwas länger sollte der Name schon sein."
        }
    }

    return {
        validates: true,
        message: ""
    }
}