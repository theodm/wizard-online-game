package de.theodm.pwf.routing.model.wizard

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.theodm.wizard.TrumpColor
import de.theodm.wizard.card.*
import io.javalin.plugin.openapi.dsl.oneOf

data class RsWizardCard(
    val type: String,
    val cardID: String,
    val cmpTrumpColor: TrumpColor?,
    val color: CardColor? = null,
    val number: Int? = null
) {
    fun toWizardCard(): WizardCard {
        when (type) {
            "RsJoker" -> return Joker(this.cardID)
            "RsNull" -> return Null(this.cardID)
            "RsNumberColorCard" -> return NumberColorCard(color!!, number!!)
        }

        throw IllegalStateException("invalid type: $type")
    }
}


fun WizardCard.toRsWizardCard(): RsWizardCard {
    return when (this) {
        is Joker -> RsWizardCard(
            type = "RsJoker",
            cardID = this.id(),
            cmpTrumpColor = this.trumpColor()
        )
        is Null -> RsWizardCard(
            cardID = this.id(),
            type = "RsNull",
            cmpTrumpColor = this.trumpColor()
        )
        is NumberColorCard -> RsWizardCard(
            type = "RsNumberColorCard",
            cardID = this.id(),
            cmpTrumpColor = this.trumpColor(),
            color = this.color,
            number = this.number
        )
    }
}
