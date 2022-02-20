package de.theodm.pwf.routing.model.wizard

import de.theodm.wizard.card.WizardCard
import de.theodm.wizard.stich.Stich

data class RsCardInHand(
    val wizardCard: RsWizardCard,
    val cmpAllowedToPlay: Boolean
)

fun WizardCard.toRsCardInHand(handOfPlayer: List<WizardCard>, currentStich: Stich): RsCardInHand {
    return RsCardInHand(
        wizardCard = this.toRsWizardCard(),
        cmpAllowedToPlay = currentStich.isAllowedToPlayCard(handOfPlayer, this)
    )
}