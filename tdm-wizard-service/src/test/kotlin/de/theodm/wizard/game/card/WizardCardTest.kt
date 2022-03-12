package de.theodm.wizard.card

import com.google.common.truth.Truth
import de.theodm.wizard.game.card.TrumpColor
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class WizardCardTest {
    @Test
    @DisplayName("Eine Farbkarte ist höher als eine andere, wenn ihr Zahlenwert höher ist.")
    fun farbkarte() {
        Truth.assertThat(wizardCardOf("R13").isHigherThan(wizardCardOf("R12"), TrumpColor.Yellow)).isTrue()
        Truth.assertThat(wizardCardOf("R12").isHigherThan(wizardCardOf("R13"), TrumpColor.Yellow)).isFalse()
    }

    @Test
    @DisplayName("Eine Farbkarte ist höher als eine andere, wenn es die Trumpffarbe ist.")
    fun trumpffarbe() {
        Truth.assertThat(wizardCardOf("Y13").isHigherThan(wizardCardOf("R1"), TrumpColor.Yellow)).isTrue()
        Truth.assertThat(wizardCardOf("R1").isHigherThan(wizardCardOf("Y13"), TrumpColor.Yellow)).isFalse()
    }

    @Test
    @DisplayName("Eine Farbkarte ist höher, wenn sie vorher angespielt wurde.")
    fun vorherigesAusspielen() {
        Truth.assertThat(wizardCardOf("R1").isHigherThan(wizardCardOf("Y13"), TrumpColor.Blue)).isFalse()
        Truth.assertThat(wizardCardOf("Y13").isHigherThan(wizardCardOf("R1"), TrumpColor.Blue)).isFalse()
    }

    @Test
    @DisplayName("Ein Narr ist immer kleiner als eine Farb- oder Trumpfkarte.")
    fun narr() {
        Truth.assertThat(wizardCardOf("R1").isHigherThan(wizardCardOf("N1"), TrumpColor.Blue)).isTrue()
        Truth.assertThat(wizardCardOf("R1").isHigherThan(wizardCardOf("N1"), TrumpColor.Red)).isTrue()
        Truth.assertThat(wizardCardOf("J1").isHigherThan(wizardCardOf("N1"), TrumpColor.Red)).isTrue()
        Truth.assertThat(wizardCardOf("N1").isHigherThan(wizardCardOf("R1"), TrumpColor.Blue)).isFalse()
        Truth.assertThat(wizardCardOf("N1").isHigherThan(wizardCardOf("R1"), TrumpColor.Red)).isFalse()
        Truth.assertThat(wizardCardOf("N1").isHigherThan(wizardCardOf("J1"), TrumpColor.Red)).isFalse()
    }

    @Test
    @DisplayName("Ein Zauberer ist immer größer als eine Farb- oder Trumpfkarte.")
    fun joker() {
        Truth.assertThat(wizardCardOf("J1").isHigherThan(wizardCardOf("R1"), TrumpColor.Blue)).isTrue()
        Truth.assertThat(wizardCardOf("J1").isHigherThan(wizardCardOf("R1"), TrumpColor.Red)).isTrue()
        Truth.assertThat(wizardCardOf("J1").isHigherThan(wizardCardOf("N1"), TrumpColor.Red)).isTrue()
        Truth.assertThat(wizardCardOf("R1").isHigherThan(wizardCardOf("J1"), TrumpColor.Blue)).isFalse()
        Truth.assertThat(wizardCardOf("R1").isHigherThan(wizardCardOf("J1"), TrumpColor.Red)).isFalse()
        Truth.assertThat(wizardCardOf("N1").isHigherThan(wizardCardOf("J1"), TrumpColor.Red)).isFalse()
    }

    @Test
    @DisplayName("Ein nachfolgender Narr ist kleiner als ein vorhergehender.")
    fun narr2() {
        Truth.assertThat(wizardCardOf("N1").isHigherThan(wizardCardOf("N2"), TrumpColor.Green)).isFalse()
    }

    @Test
    @DisplayName("Ein nachfolgender Joker ist kleiner als ein vorhergehender.")
    fun joker2() {
        Truth.assertThat(wizardCardOf("J1").isHigherThan(wizardCardOf("J2"), TrumpColor.Green)).isFalse()
    }






}