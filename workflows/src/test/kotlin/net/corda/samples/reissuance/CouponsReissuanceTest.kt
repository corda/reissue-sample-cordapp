package net.corda.samples.reissuance

import com.r3.corda.lib.reissuance.states.ReissuanceLock
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.contracts.TransactionVerificationException
import net.corda.core.node.services.queryBy
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class CouponsReissuanceTest: AbstractCandyFlowTest() {

    @Test
    fun `Coupons are re-issued and back-chain is pruned`() {
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
        val firstCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReissuanceBackChainTransactionIds.toTypedArray()))

        createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        reissueRequestedStates(candyShopNode, reissuanceRequest)

        val exitCouponTransactionId = tearUpCandyCoupons(aliceNode, aliceCandyCoupons.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reissuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]

        unlockReissuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reissuedStateAndRefs,
            reissuanceLock
        )

        val aliceReissuedCoupons = listAvailableCandyCoupons(aliceNode, encumbered = false)
        assertThat(aliceReissuedCoupons.map { it.state.data }, `is`(aliceCandyCoupons.map { it.state.data }))

        val firstCouponAfterReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceReissuedCoupons[0].ref.txhash)
        val secondCouponAfterReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceReissuedCoupons[1].ref.txhash)

        assertThat(firstCouponAfterReissuanceBackChainTransactionIds, hasSize(`is`(3)))
        assertThat(firstCouponAfterReissuanceBackChainTransactionIds, `is`(
            secondCouponAfterReissuanceBackChainTransactionIds))
        assertThat(firstCouponAfterReissuanceBackChainTransactionIds,
            everyItem(not(`is`(`in`(firstCouponBeforeReissuanceBackChainTransactionIds)))))
        assertThat(secondCouponAfterReissuanceBackChainTransactionIds,
            everyItem(not(`is`(`in`(secondCouponBeforeReissuanceBackChainTransactionIds)))))
    }

    @Test(expected = TransactionVerificationException::class)
    fun `Re-issued coupons can't be unlocked when using BuyCandiesUsingCoupons`() {
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
        val firstCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReissuanceBackChainTransactionIds.toTypedArray()))

        createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        reissueRequestedStates(candyShopNode, reissuanceRequest)

        val exitCouponTransactionId = buyCandiesUsingCoupons(aliceNode, aliceCandyCoupons.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reissuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]

        unlockReissuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reissuedStateAndRefs,
            reissuanceLock
        )
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
        val firstCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReissuanceBackChainTransactionIds.toTypedArray()))

        createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        rejectReissuanceRequested(candyShopNode, reissuanceRequest)

        val aliceCouponsAfterReissuanceRejection = listAvailableCandyCoupons(aliceNode, encumbered = false)
        assertThat(aliceCouponsAfterReissuanceRejection, `is`(aliceCandyCoupons))
    }

    @Test
    fun `Original coupons are exchanged (spent) after re-issuance and re-issued tokens are deleted`() {
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
        val firstCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[0].ref.txhash)
        val secondCouponBeforeReissuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceCandyCoupons[1].ref.txhash)

        assertThat(firstCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(4)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasSize(`is`(8)))
        assertThat(secondCouponBeforeReissuanceBackChainTransactionIds, hasItems(
            *firstCouponBeforeReissuanceBackChainTransactionIds.toTypedArray()))

        createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCoupons,
            candyShopParty
        )

        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        reissueRequestedStates(candyShopNode, reissuanceRequest)

        exchangeCandyCoupons(aliceNode, listOf(aliceCandyCoupons[0].ref), listOf(2, 2))
        val unencumberedaliceCouponsAfterMove = listAvailableCandyCoupons(aliceNode, encumbered = false)

        val reissuedStates = listAvailableCandyCoupons(aliceNode, encumbered = true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]
        deleteReissuedStatesAndLock(
            aliceNode,
            reissuanceLock,
            reissuedStates
        )

        val aliceCouponsAfterDeletion = listAvailableCandyCoupons(aliceNode, encumbered = null)
        assertThat(aliceCouponsAfterDeletion, `is`(unencumberedaliceCouponsAfterMove))
    }
}
