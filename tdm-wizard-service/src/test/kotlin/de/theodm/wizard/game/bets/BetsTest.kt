package de.theodm.wizard.game.bets

import com.google.common.truth.Truth
import de.theodm.wizard.game.TestWizardPlayer
import de.theodm.wizard.game.WizardGameSettings
import de.theodm.wizard.game.players.Players
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class BetsTest {
    private val p1 = TestWizardPlayer("Theo")
    private val p2 = TestWizardPlayer("Tabea")
    private val p3 = TestWizardPlayer("Domi")

    val gameSettingsNormal = WizardGameSettings(hiddenBets = false)
    val gameSettingsHidden = WizardGameSettings(hiddenBets = true)

    @DisplayName("Initialzustand: Kein Spieler hat einen Tipp abgegeben.")
    @Test
    fun initial() {
        val bets = Bets.create(Players(p1, p2, p3))

        Truth.assertThat(bets[p1]).isEqualTo(null)
        Truth.assertThat(bets[p2]).isEqualTo(null)
        Truth.assertThat(bets[p3]).isEqualTo(null)
    }

    @DisplayName("Die Methode betsRemaining gibt an, ob es noch Spieler gibt, die einen Tipp abgeben müssen.")
    @Test
    fun betsRemaining() {
        val bets = Bets(mapOf(
            p1 to 1,
            p2 to 1,
            p3 to 1
        ))

        Truth.assertThat(bets.betsRemaining()).isFalse()

        val bets2 = Bets(mapOf(
            p1 to null,
            p2 to 1,
            p3 to 1
        ))

        Truth.assertThat(bets2.betsRemaining()).isTrue()
    }

    @DisplayName("Im normalen Spiel darf der letzte Spieler den Tipp nicht abgeben, der das Spiel dazu bringt, aufzugehen.")
    @Test
    fun allowedBets() {
        val bets = Bets(mapOf(
            p1 to 1,
            p2 to 1,
            p3 to null
        ))

        Truth.assertThat(bets.allowedBets(gameSettingsNormal, p3, 3)).doesNotContain(1)
        Truth.assertThat(bets.allowedBets(gameSettingsNormal, p3, 3)).containsExactly(0, 2, 3)

        Assertions.assertThrows(IllegalStateException::class.java) {
            bets.setBetForPlayer(gameSettingsNormal, p3, 1, 3)
        }

        Assertions.assertDoesNotThrow {
            bets.setBetForPlayer(gameSettingsNormal, p3, 0, 3)
            bets.setBetForPlayer(gameSettingsNormal, p3, 2, 3)
            bets.setBetForPlayer(gameSettingsNormal, p3, 3, 3)
        }
    }

    @DisplayName("Im Spiel mit verdeckten Wetten darf der letzte Spieler einen beliebigen Tipp abgeben.")
    @Test
    fun allowedBets3() {
        val bets = Bets(mapOf(
            p1 to 1,
            p2 to 1,
            p3 to null
        ))

        Truth.assertThat(bets.allowedBets(gameSettingsHidden, p3, 3)).containsExactly(0, 1, 2, 3)

        Assertions.assertDoesNotThrow {
            bets.setBetForPlayer(gameSettingsHidden, p3, 0, 3)
            bets.setBetForPlayer(gameSettingsHidden, p3, 1, 3)
            bets.setBetForPlayer(gameSettingsHidden, p3, 2, 3)
            bets.setBetForPlayer(gameSettingsHidden, p3, 3, 3)
        }
    }

    @Test
    @DisplayName("Im normalen Spiel darf der vorletzte Spieler jeden Tipp abgeben.")
    fun allowedBets2() {
        val bets = Bets(mapOf(
            p1 to 1,
            p2 to null,
            p3 to null
        ))

        Truth.assertThat(bets.allowedBets(gameSettingsNormal, p2, 3)).containsExactly(0, 1, 2, 3)

        Assertions.assertDoesNotThrow {
            bets.setBetForPlayer(gameSettingsNormal, p2, 0, 3)
            bets.setBetForPlayer(gameSettingsNormal, p2, 1, 3)
            bets.setBetForPlayer(gameSettingsNormal, p2, 2, 3)
            bets.setBetForPlayer(gameSettingsNormal, p2, 3, 3)
        }
    }

    @Test
    @DisplayName("Der Aufruf von setBetForPlayer aktualisiert die Datenstruktur.")
    fun setBetForPlayer() {
        val bets = Bets(mapOf(
            p1 to 1,
            p2 to null,
            p3 to null
        ))

        Truth.assertThat(bets.setBetForPlayer(gameSettingsNormal, p2, 1, 3)).isEqualTo(
            Bets(mapOf(
            p1 to 1,
            p2 to 1,
            p3 to null
        ))
        )
    }




}