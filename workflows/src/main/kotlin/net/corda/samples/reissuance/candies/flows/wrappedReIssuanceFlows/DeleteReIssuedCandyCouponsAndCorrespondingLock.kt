package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.DeleteReissuedStatesAndLock
import com.r3.corda.lib.reissuance.states.ReissuanceLock
import com.r3.corda.lib.tokens.contracts.commands.RedeemTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling DeleteReIssuedStatesAndLock.
// DeleteReIssuedStatesAndLock can be used directly to exit re-issued states and lock from the ledger.
// DeleteReIssuedCandyCouponsAndCorrespondingLock has been created to make it easier to use node shell.

@StartableByRPC
class DeleteReIssuedCandyCouponsAndCorrespondingLock(
    private val reIssuedStatesRefStrings: List<String>,
    private val reIssuanceLockRefString: String
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val reIssuanceLockRef = parseStateReference(reIssuanceLockRefString)
        val reIssuanceLockStateAndRef = serviceHub.vaultService.queryBy<ReissuanceLock<FungibleToken>>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(reIssuanceLockRef))
        ).states[0]

        val reIssuedStatesRefs = reIssuedStatesRefStrings.map { parseStateReference(it) }
        val reIssuedStatesStateAndRefs = serviceHub.vaultService.queryBy<FungibleToken>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = reIssuedStatesRefs)
        ).states

        val issuer = reIssuanceLockStateAndRef.state.data.issuer
        val candyCouponTokenType = TokenType("CandyCoupon", 0)
        val issuedTokenType = IssuedTokenType(issuer as Party, candyCouponTokenType)

        val reIssuedStates = reIssuanceLockStateAndRef.state.data.originalStates

        return subFlow(DeleteReissuedStatesAndLock(
            reIssuanceLockStateAndRef,
            reIssuedStatesStateAndRefs,
            RedeemTokenCommand(issuedTokenType, reIssuedStates.indices.toList(), listOf())
        ))
    }

}
