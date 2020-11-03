package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.flows.redeem.addTokensToRedeem
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.contracts.requireThat
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows.parseStateReference

@InitiatingFlow
@StartableByRPC
class TearUpCandyCoupons(
    private val couponRefsStrings: List<String> = listOf()
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val couponRefs = couponRefsStrings.map { parseStateReference(it) }
        val couponsToTearUp = subFlow(ListCandyCoupons(ourIdentity, couponRefs = couponRefs))
        val candyShop = couponsToTearUp[0].state.data.issuer

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        addTokensToRedeem(transactionBuilder, couponsToTearUp, null)

        transactionBuilder.verify(serviceHub)
        val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)

        val sessions = listOf(initiateFlow(candyShop))
        val fullySignedTransaction = subFlow(CollectSignaturesFlow(signedTransaction, sessions))

        return subFlow(
            FinalityFlow(
                transaction = fullySignedTransaction,
                sessions = sessions
            )
        ).id
    }

}


@InitiatedBy(TearUpCandyCoupons::class)
class TearUpCandyCouponsResponder(
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
