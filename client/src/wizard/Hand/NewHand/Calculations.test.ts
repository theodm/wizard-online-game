import {expect} from "chai";
import {diffLeft, indexToLaneMap, makeLanes} from "./Calculations";


describe("Lane-Splitting: Array aufteilen in mehrere gleich große Arrays.", () => {
    it("30 -> 10, 10, 10", async () => {
        const test = Array.from(Array(30).keys())

        const result = makeLanes(test, 10)

        expect(result[0]).to.deep.eq([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
        expect(result[1]).to.deep.eq([10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
        expect(result[2]).to.deep.eq([20, 21, 22, 23, 24, 25, 26, 27, 28, 29])

        const result2 = indexToLaneMap(test, 10)

        expect(result2.numberOfLanes).to.be.eq(3)
        expect(result2.newArr[0].element).to.be.eq(0)
        expect(result2.newArr[10].element).to.be.eq(10)


    });

    it("27 -> 9, 9, 9", async () => {
        const test = Array.from(Array(27).keys())

        const result = makeLanes(test, 10)

        expect(result[0]).to.deep.eq([0, 1, 2, 3, 4, 5, 6, 7, 8])
        expect(result[1]).to.deep.eq([9, 10, 11, 12, 13, 14, 15, 16, 17])
        expect(result[2]).to.deep.eq([18, 19, 20, 21, 22, 23, 24, 25, 26])
    });


    it("29 -> 10, 10, 9", async () => {
        const test = Array.from(Array(29).keys())

        const result = makeLanes(test, 10)

        expect(result[0]).to.deep.eq([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
        expect(result[1]).to.deep.eq([10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
        expect(result[2]).to.deep.eq([20, 21, 22, 23, 24, 25, 26, 27, 28])
    });


});

describe("Änderungen an Arrays filtern: ", () => {
    it("Verschiedene Testfälle", async () => {
        const arr = [];

        expect(diffLeft([1, 2], [1])).to.be.deep.eq([2])
        expect(diffLeft([], [1])).to.be.deep.eq([])
    })

    it("Besondere Vergleichsfunktion", async () => {
        const arr = [];

        expect(diffLeft(["1a", "2a"], ["1"], (it) => it[0])).to.be.deep.eq(["2a"])
        expect(diffLeft([], ["1"])).to.be.deep.eq([])
    })

})