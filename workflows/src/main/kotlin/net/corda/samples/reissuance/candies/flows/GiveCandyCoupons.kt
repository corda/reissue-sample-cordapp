package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.commands.MoveTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.TransactionBuilder
import net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows.parseStateReference

@InitiatingFlow
@StartableByRPC
class GiveCandyCoupons(
    private val couponRefsStrings: List<String>,
    private val newHolderParty: Party
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val couponRefs = couponRefsStrings.map { parseStateReference(it) }
        val couponsToGive = subFlow(ListAvailableCandyCoupons(ourIdentity, couponRefs = couponRefs))

        val issuedCandyCouponTokenType = couponsToGive[0].state.data.issuedTokenType
        val newCoupons = couponsToGive.map { FungibleToken(it.state.data.amount, newHolderParty) }

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        couponsToGive.forEach { transactionBuilder.addInputState(it) }
        newCoupons.forEach { transactionBuilder.addOutputState(it) }
        transactionBuilder.addCommand(MoveTokenCommand(
            issuedCandyCouponTokenType,
            inputs = couponsToGive.indices.toList(),
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

@InitiatedBy(GiveCandyCoupons::class)
class GiveCandyCouponsResponder(
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
