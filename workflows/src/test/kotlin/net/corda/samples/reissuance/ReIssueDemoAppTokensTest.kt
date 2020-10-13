package net.corda.samples.reissuance

import com.r3.corda.lib.reissuance.states.ReIssuanceLock
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.node.services.queryBy
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class UnlockReIssuedStatesTest: AbstractCandyFlowTest() {

    @Test
    fun `Tokens are re-issued and back-chain is pruned`() {
        issueCandyCoupons(aliceParty, 12)
        val issuedCoupons = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, issuedCoupons.map { it.ref }, listOf(4, 4, 2, 2))
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(1, 4).map{ it.ref }, aliceParty)
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(1, 3).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map{ it.ref }, aliceParty)
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(1, 4).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 1).map{ it.ref }, aliceParty)

        val aliceCandyCoupons = listAvailableCandyCoupons(aliceNode, encumbered = false)

        // Alice should own exactly 2 coupons
        val firstCouponBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReIssuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReIssuanceBackChainTransactionIds.toTypedArray()))

        createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reIssuanceRequest = candyShopNode.services.vaultService.queryBy<ReIssuanceRequest>().states[0]
        reIssueRequestedStates(candyShopNode, reIssuanceRequest)

        val exitCouponTransactionId = throwAwayCandyCoupons(aliceNode, aliceCandyCoupons.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reIssuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reIssuanceLock = aliceNode.services.vaultService.queryBy<ReIssuanceLock<FungibleToken>>().states[0]

        unlockReIssuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reIssuedStateAndRefs,
            reIssuanceLock
        )

        val aliceReIssuedCoupons = listAvailableCandyCoupons(aliceNode, encumbered = false)
        assertThat(aliceReIssuedCoupons.map { it.state.data }, `is`(aliceCandyCoupons.map { it.state.data }))

        val firstCouponAfterReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceReIssuedCoupons[0].ref.txhash)
        val secondCouponAfterReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceReIssuedCoupons[1].ref.txhash)

        assertThat(firstCouponAfterReIssuanceBackChainTransactionIds, hasSize(`is`(3)))
        assertThat(firstCouponAfterReIssuanceBackChainTransactionIds, `is`(
            secondCouponAfterReIssuanceBackChainTransactionIds))
        assertThat(firstCouponAfterReIssuanceBackChainTransactionIds,
            everyItem(not(`is`(`in`(firstCouponBeforeReIssuanceBackChainTransactionIds)))))
        assertThat(secondCouponAfterReIssuanceBackChainTransactionIds,
            everyItem(not(`is`(`in`(secondCouponBeforeReIssuanceBackChainTransactionIds)))))
    }

    @Test
    fun `Re-issuance request is rejected`() {
        issueCandyCoupons(aliceParty, 12)
        val issuedCoupons = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, issuedCoupons.map { it.ref }, listOf(4, 4, 2, 2))
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(1, 4).map{ it.ref }, aliceParty)
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(1, 3).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map{ it.ref }, aliceParty)
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(1, 4).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 1).map{ it.ref }, aliceParty)

        val aliceCandyCoupons = listAvailableCandyCoupons(aliceNode, encumbered = false)

        // Alice should own exactly 2 coupons
        val firstCouponBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReIssuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReIssuanceBackChainTransactionIds.toTypedArray()))

        createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reIssuanceRequest = candyShopNode.services.vaultService.queryBy<ReIssuanceRequest>().states[0]
        rejectReIssuanceRequested(candyShopNode, reIssuanceRequest)

        val aliceTokensAfterReIssuanceRejection = listAvailableCandyCoupons(aliceNode, encumbered = false)
        assertThat(aliceTokensAfterReIssuanceRejection, `is`(aliceCandyCoupons))
    }

    @Test
    fun `Original tokens are spent after re-issuance and re-issued tokens are deleted`() {
        issueCandyCoupons(aliceParty, 12)
        val issuedCoupons = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, issuedCoupons.map { it.ref }, listOf(4, 4, 2, 2))
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(1, 4).map{ it.ref }, aliceParty)
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(1, 3).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map{ it.ref }, aliceParty)
        giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(1, 4).map{ it.ref }, bobParty)
        giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 1).map{ it.ref }, aliceParty)

        val aliceCandyCoupons = listAvailableCandyCoupons(aliceNode, encumbered = false)

        // Alice should own exactly 2 coupons
        val firstCouponBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReIssuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReIssuanceBackChainTransactionIds.toTypedArray()))

        createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reIssuanceRequest = candyShopNode.services.vaultService.queryBy<ReIssuanceRequest>().states[0]
        reIssueRequestedStates(candyShopNode, reIssuanceRequest)

        exchangeCandyCoupons(aliceNode, listOf(aliceCandyCoupons[0].ref), listOf(2, 2))
        val unencumberedAliceTokensAfterMove = listAvailableCandyCoupons(aliceNode, encumbered = false)

        val reIssuedStates = listAvailableCandyCoupons(aliceNode, encumbered = true)
        val reIssuanceLock = aliceNode.services.vaultService.queryBy<ReIssuanceLock<FungibleToken>>().states[0]
        deleteReIssuedStatesAndLock(
            aliceNode,
            reIssuanceLock,
            reIssuedStates
        )

        val aliceTokensAfterDeletion = listAvailableCandyCoupons(aliceNode, encumbered = null)
        assertThat(aliceTokensAfterDeletion, `is`(unencumberedAliceTokensAfterMove))
    }
}
