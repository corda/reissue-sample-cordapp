package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.ReissueStates
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling ReIssueStates.
// ReIssueStates can be used directly to re-issue states and generate corresponding re-issuance lock.
// ReIssueCandyCoupons has been created to make it easier to use node shell.

@StartableByRPC
class ReIssueCandyCoupons(
    private val reIssuanceRequestRefString: String
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val rejectReIssuanceRequestRef = parseStateReference(reIssuanceRequestRefString)
        val rejectReIssuanceRequestStateAndRef = serviceHub.vaultService.queryBy<ReissuanceRequest>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(rejectReIssuanceRequestRef))
        ).states[0]
        return subFlow(ReissueStates<FungibleToken>(
            rejectReIssuanceRequestStateAndRef,
            issuerIsRequiredExitCommandSigner = true
        ))
    }

}
