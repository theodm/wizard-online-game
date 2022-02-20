

export function createError(
    e: any
) {
    console.error(e)

    if (e.errorData && e.errorData.message) {
        return {
            timestamp: new Date(),
            message: e.errorData.message
        }
    }

    return {
        timestamp: new Date(),
        message: "Fehler: " + e
    }
}