export const BASE_PATH = "http://localhost".replace(/\/+$/, "");
const isBlob = (value) => typeof Blob !== "undefined" && value instanceof Blob;
export class BaseAPI {
  constructor(configuration = new Configuration()) {
    this.configuration = configuration;
    this.fetchApi = async (url, init) => {
      let fetchParams = {url, init};
      for (const middleware of this.middleware) {
        if (middleware.pre) {
          fetchParams = await middleware.pre({
            fetch: this.fetchApi,
            ...fetchParams
          }) || fetchParams;
        }
      }
      let response = await (this.configuration.fetchApi || fetch)(fetchParams.url, fetchParams.init);
      for (const middleware of this.middleware) {
        if (middleware.post) {
          response = await middleware.post({
            fetch: this.fetchApi,
            url: fetchParams.url,
            init: fetchParams.init,
            response: response.clone()
          }) || response;
        }
      }
      return response;
    };
    this.middleware = configuration.middleware;
  }
  withMiddleware(...middlewares) {
    const next = this.clone();
    next.middleware = next.middleware.concat(...middlewares);
    return next;
  }
  withPreMiddleware(...preMiddlewares) {
    const middlewares = preMiddlewares.map((pre) => ({pre}));
    return this.withMiddleware(...middlewares);
  }
  withPostMiddleware(...postMiddlewares) {
    const middlewares = postMiddlewares.map((post) => ({post}));
    return this.withMiddleware(...middlewares);
  }
  async request(context, initOverrides) {
    const {url, init} = this.createFetchParams(context, initOverrides);
    const response = await this.fetchApi(url, init);
    if (response.status >= 200 && response.status < 300) {
      return response;
    }
    throw response;
  }
  createFetchParams(context, initOverrides) {
    let url = this.configuration.basePath + context.path;
    if (context.query !== void 0 && Object.keys(context.query).length !== 0) {
      url += "?" + this.configuration.queryParamsStringify(context.query);
    }
    const body = typeof FormData !== "undefined" && context.body instanceof FormData || context.body instanceof URLSearchParams || isBlob(context.body) ? context.body : JSON.stringify(context.body);
    const headers = Object.assign({}, this.configuration.headers, context.headers);
    const init = {
      method: context.method,
      headers,
      body,
      credentials: this.configuration.credentials,
      ...initOverrides
    };
    return {url, init};
  }
  clone() {
    const constructor = this.constructor;
    const next = new constructor(this.configuration);
    next.middleware = this.middleware.slice();
    return next;
  }
}
;
export class RequiredError extends Error {
  constructor(field, msg) {
    super(msg);
    this.field = field;
    this.name = "RequiredError";
  }
}
export const COLLECTION_FORMATS = {
  csv: ",",
  ssv: " ",
  tsv: "	",
  pipes: "|"
};
export class Configuration {
  constructor(configuration = {}) {
    this.configuration = configuration;
  }
  get basePath() {
    return this.configuration.basePath != null ? this.configuration.basePath : BASE_PATH;
  }
  get fetchApi() {
    return this.configuration.fetchApi;
  }
  get middleware() {
    return this.configuration.middleware || [];
  }
  get queryParamsStringify() {
    return this.configuration.queryParamsStringify || querystring;
  }
  get username() {
    return this.configuration.username;
  }
  get password() {
    return this.configuration.password;
  }
  get apiKey() {
    const apiKey = this.configuration.apiKey;
    if (apiKey) {
      return typeof apiKey === "function" ? apiKey : () => apiKey;
    }
    return void 0;
  }
  get accessToken() {
    const accessToken = this.configuration.accessToken;
    if (accessToken) {
      return typeof accessToken === "function" ? accessToken : async () => accessToken;
    }
    return void 0;
  }
  get headers() {
    return this.configuration.headers;
  }
  get credentials() {
    return this.configuration.credentials;
  }
}
export function exists(json, key) {
  const value = json[key];
  return value !== null && value !== void 0;
}
export function querystring(params, prefix = "") {
  return Object.keys(params).map((key) => {
    const fullKey = prefix + (prefix.length ? `[${key}]` : key);
    const value = params[key];
    if (value instanceof Array) {
      const multiValue = value.map((singleValue) => encodeURIComponent(String(singleValue))).join(`&${encodeURIComponent(fullKey)}=`);
      return `${encodeURIComponent(fullKey)}=${multiValue}`;
    }
    if (value instanceof Date) {
      return `${encodeURIComponent(fullKey)}=${encodeURIComponent(value.toISOString())}`;
    }
    if (value instanceof Object) {
      return querystring(value, fullKey);
    }
    return `${encodeURIComponent(fullKey)}=${encodeURIComponent(String(value))}`;
  }).filter((part) => part.length > 0).join("&");
}
export function mapValues(data, fn) {
  return Object.keys(data).reduce((acc, key) => ({...acc, [key]: fn(data[key])}), {});
}
export function canConsumeForm(consumes) {
  for (const consume of consumes) {
    if (consume.contentType === "multipart/form-data") {
      return true;
    }
  }
  return false;
}
export class JSONApiResponse {
  constructor(raw, transformer = (jsonValue) => jsonValue) {
    this.raw = raw;
    this.transformer = transformer;
  }
  async value() {
    return this.transformer(await this.raw.json());
  }
}
export class VoidApiResponse {
  constructor(raw) {
    this.raw = raw;
  }
  async value() {
    return void 0;
  }
}
export class BlobApiResponse {
  constructor(raw) {
    this.raw = raw;
  }
  async value() {
    return await this.raw.blob();
  }
}
export class TextApiResponse {
  constructor(raw) {
    this.raw = raw;
  }
  async value() {
    return await this.raw.text();
  }
}
