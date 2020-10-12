package net.corda.samples.reissuance.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.RejectReIssuanceRequest
import com.r3.corda.lib.reissuance.flows.RequestReIssuanceAndShareRequiredTransactions
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria

// Note: There is no need to generate a separate flow calling RejectReIssuanceRequest.
// The flow has been created to make it easier to use node shell.

@StartableByRPC
class RejectDemoAppTokensReIssuanceRequest(
    private val reIssuanceRequestRefString: String
): FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val reIssuanceRequestRef = parseStateReference(reIssuanceRequestRefString)
        val reIssuanceRequestStateAndRef = serviceHub.vaultService.queryBy<ReIssuanceRequest>(
            criteria= QueryCriteria.VaultQueryCriteria(stateRefs = listOf(reIssuanceRequestRef))
        ).states[0]
        subFlow(RejectReIssuanceRequest<FungibleToken>(
            reIssuanceRequestStateAndRef
        ))
    }

}
