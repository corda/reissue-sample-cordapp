package net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.UnlockReissuedStates
import com.r3.corda.lib.reissuance.states.ReissuanceLock
import com.r3.corda.lib.tokens.contracts.commands.MoveTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling UnlockReissuedStates.
// UnlockReissuedStates can be used directly to unlock re-issued states and deactivate re-issuance lock.
// UnlockReissuedCandyCoupons has been created to make it easier to use node shell.

@StartableByRPC
class UnlockReissuedCandyCoupons(
    private val reissuedStatesRefStrings: List<String>,
    private val reissuanceLockRefString: String,
    private val deletedStateTransactionHashes: List<SecureHash>
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val reissuanceLockRef = parseStateReference(reissuanceLockRefString)
        val reissuanceLockStateAndRef = serviceHub.vaultService.queryBy<ReissuanceLock<FungibleToken>>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(reissuanceLockRef))
        ).states[0]

        val reissuedStatesRefs = reissuedStatesRefStrings.map { parseStateReference(it) }
        val reissuedStatesStateAndRefs = serviceHub.vaultService.queryBy<FungibleToken>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = reissuedStatesRefs)
        ).states

        val issuer = reissuanceLockStateAndRef.state.data.issuer
        val candyCouponTokenType = TokenType("CandyCoupon", 0)
        val issuedTokenType = IssuedTokenType(issuer as Party, candyCouponTokenType)

        val stateRefsToReissue = reissuanceLockStateAndRef.state.data.originalStates
        val tokenIndices = stateRefsToReissue.indices.toList()

        return subFlow(UnlockReissuedStates(
            reissuedStatesStateAndRefs,
            reissuanceLockStateAndRef,
            deletedStateTransactionHashes,
            MoveTokenCommand(issuedTokenType, tokenIndices, tokenIndices)
        ))
    }

}
