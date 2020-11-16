package net.corda.samples.reissuance.candies.flows.reissuanceResponders

import com.r3.corda.lib.reissuance.flows.DeleteReissuedStatesAndLock
import com.r3.corda.lib.reissuance.flows.DeleteReissuedStatesAndLockResponder
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.requireThat
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.transactions.SignedTransaction

@InitiatedBy(DeleteReissuedStatesAndLock::class)
class DeleteReissuedStatesAndLock(
    otherSession: FlowSession
) : DeleteReissuedStatesAndLockResponder(otherSession) {

    override fun checkConstraints(stx: SignedTransaction) {
        requireThat {
            otherInputs.forEach {
                val token = it.state.data as? FungibleToken
                "Input $it is of type FungibleToken" using (token != null)
                token!!
                "Input token $token is of type CandyCoupon" using (token.tokenType == TokenType("CandyCoupon", 0))
            }
        }
    }
}
