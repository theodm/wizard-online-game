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

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface CreateUserResponse
 */
export interface CreateUserResponse {
    /**
     * 
     * @type {string}
     * @memberof CreateUserResponse
     */
    userPublicID: string;
    /**
     * 
     * @type {string}
     * @memberof CreateUserResponse
     */
    userKey: string;
}

export function CreateUserResponseFromJSON(json: any): CreateUserResponse {
    return CreateUserResponseFromJSONTyped(json, false);
}

export function CreateUserResponseFromJSONTyped(json: any, ignoreDiscriminator: boolean): CreateUserResponse {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'userPublicID': json['userPublicID'],
        'userKey': json['userKey'],
    };
}

export function CreateUserResponseToJSON(value?: CreateUserResponse | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'userPublicID': value.userPublicID,
        'userKey': value.userKey,
    };
}

