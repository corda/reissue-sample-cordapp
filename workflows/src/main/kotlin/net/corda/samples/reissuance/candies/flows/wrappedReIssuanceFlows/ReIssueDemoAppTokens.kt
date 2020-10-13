package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.ReIssueStates
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling ReIssueStates.
// The flow has been created to make it easier to use node shell.

@StartableByRPC
class ReIssueDemoAppTokens(
    private val reIssuanceRequestRefString: String
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val rejectReIssuanceRequestRef = parseStateReference(reIssuanceRequestRefString)
        val rejectReIssuanceRequestStateAndRef = serviceHub.vaultService.queryBy<ReIssuanceRequest>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(rejectReIssuanceRequestRef))
        ).states[0]
        return subFlow(ReIssueStates<FungibleToken>(
            rejectReIssuanceRequestStateAndRef,
            issuerIsRequiredExitCommandSigner = true
        ))
    }

}
