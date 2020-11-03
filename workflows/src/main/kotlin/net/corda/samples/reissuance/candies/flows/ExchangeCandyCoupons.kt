package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.commands.MoveTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.utilities.amount
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.TransactionBuilder
import net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows.parseStateReference

@InitiatingFlow
@StartableByRPC
class ExchangeCandyCoupons(
    private val couponRefsStrings: List<String>,
    private val newCouponCandies: List<Int>
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val couponRefs = couponRefsStrings.map { parseStateReference(it) }
        val couponsToExchange = subFlow(ListCandyCoupons(ourIdentity, couponRefs = couponRefs))

        val issuedCandyCouponTokenType = couponsToExchange[0].state.data.issuedTokenType
        val newCoupons = newCouponCandies.map { FungibleToken(amount(it, issuedCandyCouponTokenType), ourIdentity) }

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        couponsToExchange.forEach { transactionBuilder.addInputState(it) }
        newCoupons.forEach { transactionBuilder.addOutputState(it) }
        transactionBuilder.addCommand(MoveTokenCommand(
            issuedCandyCouponTokenType,
            inputs = couponsToExchange.indices.toList(),
            outputs = newCoupons.indices.toList()
        ), listOf(ourIdentity.owningKey))

        transactionBuilder.verify(serviceHub)
        val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)

        return subFlow(
            FinalityFlow(
                transaction = signedTransaction,
                sessions = listOf()
            )
        ).id
    }

}


@InitiatedBy(ExchangeCandyCoupons::class)
class ExchangeCandyCouponsResponder(
    private val otherSession: FlowSession
) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(
            ReceiveFinalityFlow(
                otherSession,
                statesToRecord = StatesToRecord.ALL_VISIBLE
            )
        )
    }
}
