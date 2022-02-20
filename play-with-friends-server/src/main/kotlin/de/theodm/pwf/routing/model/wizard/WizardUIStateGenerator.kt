package de.theodm.pwf.routing.model.wizard

import com.fasterxml.jackson.databind.ObjectMapper
import de.theodm.pwf.routing.lobby.LobbyParticipant
import de.theodm.wizard.Deck
import de.theodm.wizard.TrumpColor
import de.theodm.wizard.WizardGameState
import de.theodm.wizard.card.CardColor
import de.theodm.wizard.card.Joker
import de.theodm.wizard.card.NumberColorCard
import de.theodm.wizard.card.WizardCard
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

private val wpSebastian = LobbyParticipant("Sebastian123", "Sebastian")
private val wpTabea = LobbyParticipant("Tabea123", "Tabea")
private val wpDomi = LobbyParticipant("Domi123", "Domi")
private val wpNiklas = LobbyParticipant("Niklas123", "Niklas")
private val wpJenny = LobbyParticipant("Jenny123", "Jenny")
private val wpSteiner = LobbyParticipant("Steiner123", "Steiner123")

fun printGameState(
    fileName: String,
    wizardGameState: WizardGameState
) {
    val json = ObjectMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(wizardGameState.getRsWizardGameStateForPlayer(wpSebastian))

    Files.writeString(Paths.get("./client/src/wizard/GameDemoFiles").resolve(fileName), json, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
}

fun main() {
    var wizardGameState3PlayersLastRound = WizardGameState.initialWithDeckAndRound(
        hostPlayer = wpSebastian,
        players = listOf(
            wpSebastian,
            wpTabea,
            wpDomi,
        ),
        deck = Deck.from(
            *(1..60).map { Joker("J1") }.toTypedArray()
        ),
        numberOfCards = 20
    )

    printGameState("wizardGameState3PlayersLastRound.json", wizardGameState3PlayersLastRound)

    var wizardGameStateFull = WizardGameState.initialWithDeckAndRound(
        hostPlayer = wpSebastian,
        players = listOf(
            wpSebastian,
            wpTabea,
            wpDomi,
            wpNiklas,
            wpJenny,
            wpSteiner
        ),
        deck = Deck.shuffled().let { it.copy(it.origin + Joker("J1")) },
        numberOfCards = 10
    )

    printGameState("6playerslastround.json", wizardGameStateFull)

    var wizardGameState = WizardGameState.initialWithDeck(
        hostPlayer = wpSebastian,
        players = listOf(
            wpSebastian,
            wpTabea,
            wpDomi
        ),
        deck = Deck.from(
            Joker("J1"),
            NumberColorCard(CardColor.Blue, 1),
            NumberColorCard(CardColor.Blue, 2),
            NumberColorCard(CardColor.Blue, 3),
        )
    )
    printGameState("1.json", wizardGameState)

    wizardGameState = wizardGameState.selectTrump(wpSebastian, TrumpColor.Blue)
    printGameState("2.json", wizardGameState)

    wizardGameState = wizardGameState.placeBet(wpSebastian, 1)
    printGameState("3.json", wizardGameState)

    wizardGameState = wizardGameState.placeBet(wpTabea, 0)
    printGameState("4.json", wizardGameState)

    wizardGameState = wizardGameState.placeBet(wpDomi, 1)
    printGameState("5.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpSebastian, NumberColorCard(CardColor.Blue, 3))
    printGameState("6.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpTabea, NumberColorCard(CardColor.Blue, 2))
    printGameState("7.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpDomi, NumberColorCard(CardColor.Blue, 1))
    printGameState("8.json", wizardGameState)

    wizardGameState = wizardGameState.finishAndStartNewRoundWithDeck(
        player = wpSebastian,
        deck = Deck.from(
            Joker("J1"),
            NumberColorCard(CardColor.Blue, 2),
            NumberColorCard(CardColor.Yellow, 2),
            NumberColorCard(CardColor.Yellow, 3),
            NumberColorCard(CardColor.Blue, 3),
            NumberColorCard(CardColor.Blue, 1),
            NumberColorCard(CardColor.Yellow, 1),
        )
    )
    printGameState("9.json", wizardGameState)

    wizardGameState = wizardGameState.selectTrump(wpTabea, TrumpColor.Red)
    printGameState("10.json", wizardGameState)

    wizardGameState = wizardGameState.placeBet(wpTabea, 1)
    printGameState("11.json", wizardGameState)

    wizardGameState = wizardGameState.placeBet(wpDomi, 1)
    printGameState("12.json", wizardGameState)

    wizardGameState = wizardGameState.placeBet(wpSebastian, 1)
    printGameState("13.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpTabea, NumberColorCard(CardColor.Blue, 3))
    printGameState("14.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpDomi, NumberColorCard(CardColor.Blue, 2))
    printGameState("15.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpSebastian, NumberColorCard(CardColor.Blue, 1))
    printGameState("16.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpTabea, NumberColorCard(CardColor.Yellow, 3))
    printGameState("17.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpDomi, NumberColorCard(CardColor.Yellow, 2))
    printGameState("18.json", wizardGameState)

    wizardGameState = wizardGameState.placeCard(wpSebastian, NumberColorCard(CardColor.Yellow, 1))
    printGameState("19.json", wizardGameState)




}