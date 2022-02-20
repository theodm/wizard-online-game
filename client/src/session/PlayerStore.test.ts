import { expect } from "chai";
import { PlayerStore } from "./PlayerStore";

export function test() {

}
//
// describe("PlayerStore", () => {
//   it("create new session", async () => {
//     const playerStore = PlayerStore.loadFromWebStorage(
//       async (privateKey, newUserName) => {},
//       async (userName) => {
//         return {
//           userKey: "user_key",
//           userPublicID: "user_public_id",
//         };
//       }
//     );
//
//     const result = await playerStore.getOrCreateUserForName("Theo");
//
//     expect(result.privateKey).to.eq("user_key");
//     expect(result.publicKey).to.eq("user_public_id");
//     expect(result.userName).to.eq("Theo");
//
//     expect(playerStore.getPrivateKey()).to.eq("user_key");
//     expect(playerStore.getUserName()).to.eq("Theo");
//
//     localStorage.clear();
//   });
//
//   it("restore from session", async () => {
//     await PlayerStore.loadFromWebStorage(
//       async (privateKey, newUserName) => {},
//       async (userName) => {
//         return {
//           userKey: "user_key_2",
//           userPublicID: "user_public_id_2",
//         };
//       }
//     ).getOrCreateUserForName("Test");
//
//     const playerStore = PlayerStore.loadFromWebStorage(
//       async (privateKey, newUserName) => {},
//       async (userName) => {
//         return {
//           userKey: "user_key_2",
//           userPublicID: "user_public_id_2",
//         };
//       }
//     );
//
//     expect(playerStore.getPrivateKey()).to.eq("user_key_2");
//     expect(playerStore.getUserName()).to.eq("Test");
//
//     localStorage.clear();
//   });
// });
