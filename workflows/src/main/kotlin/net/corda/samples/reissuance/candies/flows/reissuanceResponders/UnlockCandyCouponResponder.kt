package net.corda.samples.reissuance.candies.flows.reissuanceResponders

import com.r3.corda.lib.reissuance.flows.UnlockReissuedStates
import com.r3.corda.lib.reissuance.flows.UnlockReissuedStatesResponder
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.requireThat
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.transactions.SignedTransaction

@InitiatedBy(UnlockReissuedStates::class)
class UnlockCandyCouponResponder(
    otherSession: FlowSession
) : UnlockReissuedStatesResponder(otherSession) {

    override fun checkConstraints(stx: SignedTransaction) {
        requireThat {
            otherInputs.forEach {
                val token = it.state.data as? FungibleToken
                "Input $it is of type FungibleToken" using (token != null)
                token!!
                "Input token $token is of type CandyCoupon" using (token.tokenType == TokenType("CandyCoupon", 0))
            }
            otherOutputs.forEach {
                val token = it.data as? FungibleToken
                "Output $it is of type FungibleToken" using (token != null)
                token!!
                "Output token $token is of type CandyCoupon" using (token.tokenType == TokenType("CandyCoupon", 0))
            }
        }
    }
}
