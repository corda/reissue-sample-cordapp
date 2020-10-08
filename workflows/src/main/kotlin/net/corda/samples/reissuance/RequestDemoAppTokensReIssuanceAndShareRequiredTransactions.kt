package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.RequestReIssuanceAndShareRequiredTransactions
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

// Note: There is no need to generate a separate flow calling RequestReIssuanceAndShareRequiredTransactions.
// The flow has been created to make it easier to use node shell.

@InitiatingFlow
@StartableByRPC
class RequestDemoAppTokensReIssuanceAndShareRequiredTransactions(
    private val issuer: AbstractParty,
    private val stateRefsToReIssue: List<StateAndRef<FungibleToken>>
): FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val demoAppTokenType = TokenType("DemoAppToken", 0)
        val issuedTokenType = IssuedTokenType(issuer as Party, demoAppTokenType)

        subFlow(RequestReIssuanceAndShareRequiredTransactions(
            issuer,
            stateRefsToReIssue,
            IssueTokenCommand(issuedTokenType, stateRefsToReIssue.indices.toList())
        ))
    }

}
