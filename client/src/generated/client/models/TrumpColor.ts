/* tslint:disable */
/* eslint-disable */
/**
 * Play with friends
 * Play with friends
 *
 * The version of the OpenAPI document: 1.0
 * Contact: theo.dm94@gmail.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/**
 * 
 * @export
 * @enum {string}
 */
export enum TrumpColor {
    Red = 'Red',
    Yellow = 'Yellow',
    Green = 'Green',
    Blue = 'Blue',
    None = 'None'
}

export function TrumpColorFromJSON(json: any): TrumpColor {
    return TrumpColorFromJSONTyped(json, false);
}

export function TrumpColorFromJSONTyped(json: any, ignoreDiscriminator: boolean): TrumpColor {
    return json as TrumpColor;
}

export function TrumpColorToJSON(value?: TrumpColor | null): any {
    return value as any;
}
