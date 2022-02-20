

export function assertNotFalsy(value: unknown, message?: string): asserts value {
    if (!value) {
        throw new Error(message ? message : "value must be defined");
    }
}