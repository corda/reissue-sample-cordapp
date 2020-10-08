package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.flows.redeem.addTokensToRedeem
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.requireThat
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class RedeemDemoAppTokens(
    private val issuer: Party,
    private val encumbered: Boolean?,
    private val tokensNum: Long? = null,
    private val tokenRefs: List<StateRef> = listOf()
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        require((tokensNum == null).xor(tokenRefs.isEmpty())) {
            "Exactly one of tokensNum and tokenRefs parameters must be provided" }

        val holderParty = ourIdentity

        val tokensToRedeem = if(tokensNum != null) {
            // split tokens into token to redeem and optional change token
            subFlow(MoveDemoAppTokens(issuer, holderParty, tokensNum))
            val availableTokens = subFlow(ListAvailableDemoAppTokens(holderParty, encumbered))
            listOf(availableTokens.first { it.state.data.amount.quantity == tokensNum })
        } else {
            val availableTokens = subFlow(ListAvailableDemoAppTokens(holderParty, encumbered))
            availableTokens.filter { tokenRefs.contains(it.ref) }
        }

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        addTokensToRedeem(transactionBuilder, tokensToRedeem, null)

        transactionBuilder.verify(serviceHub)
        val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)

        val issuerSession = initiateFlow(issuer)
        val sessions = listOf(issuerSession)
        val fullySignedTransaction = subFlow(CollectSignaturesFlow(signedTransaction, sessions))

        return subFlow(
            FinalityFlow(
                transaction = fullySignedTransaction,
                sessions = sessions
            )
        ).id
    }

}


@InitiatedBy(RedeemDemoAppTokens::class)
class RedeemDemoAppTokensResponder(
    private val otherSession: FlowSession
) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signTransactionFlow = object : SignTransactionFlow(otherSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        }
        val transaction = subFlow(signTransactionFlow)
        subFlow(
            ReceiveFinalityFlow(
                otherSession,
                expectedTxId = transaction.id,
                statesToRecord = StatesToRecord.ALL_VISIBLE
            )
        )
    }
}
