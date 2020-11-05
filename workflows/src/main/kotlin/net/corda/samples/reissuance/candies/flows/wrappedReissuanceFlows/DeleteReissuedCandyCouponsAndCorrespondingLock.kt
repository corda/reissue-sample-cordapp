package net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows

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

// Note: There is no need to generate a separate flow calling DeleteReissuedStatesAndLock.
// DeleteReissuedStatesAndLock can be used directly to exit re-issued states and lock from the ledger.
// DeleteReissuedCandyCouponsAndCorrespondingLock has been created to make it easier to use node shell.

@StartableByRPC
class DeleteReissuedCandyCouponsAndCorrespondingLock(
    private val reissuedStatesRefStrings: List<String>,
    private val reissuanceLockRefString: String
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

        val reissuedStates = reissuanceLockStateAndRef.state.data.originalStates

        return subFlow(DeleteReissuedStatesAndLock(
            reissuanceLockStateAndRef,
            reissuedStatesStateAndRefs,
            RedeemTokenCommand(issuedTokenType, reissuedStates.indices.toList(), listOf())
        ))
    }

}
