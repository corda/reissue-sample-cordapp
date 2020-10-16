package net.corda.samples.reissuance.candies.flows

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
class IssueCandyCoupons(
    private val couponHolderParty: Party,
    private val couponCandies: Int
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val candyCouponTokenType = TokenType("CandyCoupon", 0)
        val candyShopParty: Party = ourIdentity
        val issuedCandyCouponTokenType = IssuedTokenType(candyShopParty, candyCouponTokenType)
        val candyCouponToken = FungibleToken(amount(couponCandies, issuedCandyCouponTokenType), couponHolderParty)

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        addIssueTokens(transactionBuilder, candyCouponToken)
        addTokenTypeJar(candyCouponToken, transactionBuilder)

        val holderSession = initiateFlow(couponHolderParty)
        return subFlow(
            ObserverAwareFinalityFlow(
                transactionBuilder = transactionBuilder,
                allSessions = listOf(holderSession)
            )
        ).id
    }
}

@InitiatedBy(IssueCandyCoupons::class)
class IssueCandyCouponsResponder(
    private val otherSession: FlowSession
) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(IssueTokensHandler(otherSession))
    }
}
