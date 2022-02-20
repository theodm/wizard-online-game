export function assertNotFalsy(value, message) {
  if (!value) {
    throw new Error(message ? message : "value must be defined");
  }
}
