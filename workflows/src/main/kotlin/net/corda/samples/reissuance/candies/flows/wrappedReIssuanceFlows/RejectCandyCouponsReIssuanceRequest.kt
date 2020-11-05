package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.RejectReissuanceRequest
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling RejectReissuanceRequest.
// RejectReissuanceRequest can be used directly to reject any re-issuance request.
// RejectCandyCouponsReissuanceRequest has been created to make it easier to use node shell.

@StartableByRPC
class RejectCandyCouponsReissuanceRequest(
    private val ReissuanceRequestRefString: String
): FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val ReissuanceRequestRef = parseStateReference(ReissuanceRequestRefString)
        val ReissuanceRequestStateAndRef = serviceHub.vaultService.queryBy<ReissuanceRequest>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(ReissuanceRequestRef))
        ).states[0]
        return subFlow(RejectReissuanceRequest<FungibleToken>(
            ReissuanceRequestStateAndRef
        ))
    }

}
