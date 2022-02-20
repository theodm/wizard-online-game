import { expect } from "chai";
import { ApiClient } from "./ApiClient";

// const client = new ApiClient("http://127.0.0.1:8088");
//
// describe("API Access", () => {
//   it("create user", async () => {
//     const result = await client.createUser("Theo");
//
//     expect(result.userKey).to.be.lengthOf(36);
//     expect(result.userPublicID).to.be.lengthOf(36);
//   });
//
//   it("change user name", async () => {
//     const result = await client.createUser("Theo");
//
//     expect(result.userKey).to.be.lengthOf(36);
//     expect(result.userPublicID).to.be.lengthOf(36);
//
//     const result2 = await client.changeUserName(
//       "Test",
//       "Custom " + result.userKey
//     );
//   });
// });
