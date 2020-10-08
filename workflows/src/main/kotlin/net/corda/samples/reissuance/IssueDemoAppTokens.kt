package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.contracts.utilities.amount
import com.r3.corda.lib.tokens.workflows.flows.issue.addIssueTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.IssueTokensHandler
import com.r3.corda.lib.tokens.workflows.internal.flows.finality.ObserverAwareFinalityFlow
import com.r3.corda.lib.tokens.workflows.utilities.addTokenTypeJar
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class IssueDemoAppTokens(
    private val tokenHolderParty: Party,
    private val tokenAmount: Long
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val demoAppTokenType = TokenType("DemoAppToken", 0)
        val issuerParty: Party = ourIdentity
        val issuedDemoAppTokenType = IssuedTokenType(issuerParty, demoAppTokenType)
        val demoAppTokenAmount = amount(tokenAmount, issuedDemoAppTokenType)
        val demoAppToken = FungibleToken(demoAppTokenAmount, tokenHolderParty)

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        addIssueTokens(transactionBuilder, demoAppToken)
        addTokenTypeJar(demoAppToken, transactionBuilder)

        val holderSession = initiateFlow(tokenHolderParty)
        return subFlow(
            ObserverAwareFinalityFlow(
                transactionBuilder = transactionBuilder,
                allSessions = listOf(holderSession)
            )
        ).id
    }
}

@InitiatedBy(IssueDemoAppTokens::class)
class IssueDemoAppTokensResponder(
    private val otherSession: FlowSession
) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(IssueTokensHandler(otherSession))
    }
}
