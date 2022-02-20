import type { RsWizardCard } from "../../generated/client";

export interface CardHolderDef {
  playerName: string;
  isActive: boolean;
  playerColorClass: string;

  numberCard: number | undefined;
  colorCard: "Red" | "Yellow" | "Green" | "Blue" | undefined;
  card: RsWizardCard | undefined;

  middleText: string;
  numOfStiche: string | undefined;
  numOfBet: string | undefined;
};
