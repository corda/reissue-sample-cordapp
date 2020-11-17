package net.corda.samples.reissuance.candies.flows.reissuanceResponders

import com.r3.corda.lib.reissuance.flows.ReissueStates
import com.r3.corda.lib.reissuance.flows.ReissueStatesResponder
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.requireThat
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.transactions.SignedTransaction

@InitiatedBy(ReissueStates::class)
class ReissueCandyCouponResponder(
    otherSession: FlowSession
) : ReissueStatesResponder(otherSession) {

    override fun checkConstraints(stx: SignedTransaction) {
        requireThat {
            otherOutputs.forEach {
                val token = it.data as? FungibleToken
                "Output $it is of type FungibleToken" using (token != null)
                token!!
                "Output token $token is of type CandyCoupon" using (token.tokenType == TokenType("CandyCoupon", 0))
            }
        }
    }
}
