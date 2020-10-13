package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.UnlockReIssuedStates
import com.r3.corda.lib.reissuance.states.ReIssuanceLock
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

// Note: There is no need to generate a separate flow calling UnlockReIssuedStates.
// The flow has been created to make it easier to use node shell.

@StartableByRPC
class UnlockReIssuedCandyCoupons(
    private val reIssuedStatesRefStrings: List<String>,
    private val reIssuanceLockRefString: String,
    private val deletedStateTransactionHashes: List<SecureHash>
): FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val reIssuanceLockRef = parseStateReference(reIssuanceLockRefString)
        val reIssuanceLockStateAndRef = serviceHub.vaultService.queryBy<ReIssuanceLock<FungibleToken>>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(reIssuanceLockRef))
        ).states[0]

        val reIssuedStatesRefs = reIssuedStatesRefStrings.map { parseStateReference(it) }
        val reIssuedStatesStateAndRefs = serviceHub.vaultService.queryBy<FungibleToken>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = reIssuedStatesRefs)
        ).states

        val issuer = reIssuanceLockStateAndRef.state.data.issuer
        val candyCouponTokenType = TokenType("CandyCoupon", 0)
        val issuedTokenType = IssuedTokenType(issuer as Party, candyCouponTokenType)

        val stateRefsToReIssue = reIssuanceLockStateAndRef.state.data.originalStates
        val tokenIndices = stateRefsToReIssue.indices.toList()

        subFlow(UnlockReIssuedStates(
            reIssuedStatesStateAndRefs,
            reIssuanceLockStateAndRef,
            deletedStateTransactionHashes,
            MoveTokenCommand(issuedTokenType, tokenIndices, tokenIndices)
        ))
    }

}
