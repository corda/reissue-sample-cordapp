package net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.RequestReissuanceAndShareRequiredTransactions
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// Note: There is no need to generate a separate flow calling RequestReissuanceAndShareRequiredTransactions.
// RequestReissuanceAndShareRequiredTransactions can be used directly to request state re-issuance and share required
// transactions (proving that states to be re-issued are valid) with the issuer.
// RequestCandyCouponReissuanceAndShareRequiredTransactions has been created to make it easier to use node shell.

@StartableByRPC
class RequestCandyCouponReissuanceAndShareRequiredTransactions(
    private val issuer: AbstractParty,
    private val stateRefStringsToReissue: List<String>
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val candyCouponTokenType = TokenType("CandyCoupon", 0)
        val issuedTokenType = IssuedTokenType(issuer as Party, candyCouponTokenType)

        return subFlow(RequestReissuanceAndShareRequiredTransactions<FungibleToken>(
            issuer,
            stateRefStringsToReissue.map { parseStateReference(it) },
            IssueTokenCommand(issuedTokenType, stateRefStringsToReissue.indices.toList())
        ))
    }

}
