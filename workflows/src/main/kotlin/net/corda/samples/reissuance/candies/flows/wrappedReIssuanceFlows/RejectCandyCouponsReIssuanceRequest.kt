package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.RejectReIssuanceRequest
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling RejectReIssuanceRequest.
// The flow has been created to make it easier to use node shell.

@StartableByRPC
class RejectCandyCouponsReIssuanceRequest(
    private val reIssuanceRequestRefString: String
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val reIssuanceRequestRef = parseStateReference(reIssuanceRequestRefString)
        val reIssuanceRequestStateAndRef = serviceHub.vaultService.queryBy<ReIssuanceRequest>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(reIssuanceRequestRef))
        ).states[0]
        return subFlow(RejectReIssuanceRequest<FungibleToken>(
            reIssuanceRequestStateAndRef
        ))
    }

}
