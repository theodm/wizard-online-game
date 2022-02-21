

export function generateItems(numberOfItems: number) {
    const result: { label: string; key: string }[] = [];
    for (let i = 0; i < numberOfItems; i++) {
        result.push({
            label: "Eintrag " + (i+1) + "",
            key: "Eintrag_" + (i+1) + "_Key"
        })
    }
    console.log(numberOfItems)

    return result;
}