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
import net.corda.samples.reissuance.candies.contracts.CandyContract
import net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows.parseStateReference
import net.corda.samples.reissuance.candies.states.Candy

@InitiatingFlow
@StartableByRPC
class BuyCandiesUsingCoupons(
    private val couponRefsStrings: List<String>
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val couponRefs = couponRefsStrings.map { parseStateReference(it) }
        val couponsToUse = subFlow(ListCandyCoupons(ourIdentity, couponRefs = couponRefs))
        val candyShop = couponsToUse[0].state.data.issuer
        val couponCandies = couponsToUse.sumBy { it.state.data.amount.quantity.toInt() }

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        addTokensToRedeem(transactionBuilder, couponsToUse, null)
        (1..couponCandies).forEach {
            transactionBuilder.addOutputState(Candy(ourIdentity))
        }
        transactionBuilder.addCommand(CandyContract.Commands.Buy(), listOf(ourIdentity.owningKey, candyShop.owningKey))

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


@InitiatedBy(BuyCandiesUsingCoupons::class)
class BuyCandiesResponder(
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
