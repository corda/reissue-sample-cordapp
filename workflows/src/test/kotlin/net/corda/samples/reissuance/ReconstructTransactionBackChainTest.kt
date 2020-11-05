package net.corda.samples.reissuance

import com.r3.corda.lib.reissuance.states.ReissuanceLock
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.crypto.SecureHash
import net.corda.core.node.services.queryBy
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class ReconstructTransactionBackChainTest: AbstractCandyFlowTest() {

    @Test
    fun `Reconstructed transaction back-chain is correct if reissuance didn't happen`() {
        val transactionIds = mutableListOf<SecureHash>()
        transactionIds.add(issueCandyCoupons(aliceParty, 10))
        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }, bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref }, aliceParty))

        val transactionBackChain = reconstructTransactionBackChain(aliceNode, transactionIds.last())
        assertThat(transactionBackChain, hasSize(`is`(transactionIds.size)))
        assertThat(transactionBackChain, hasItems(*transactionIds.toTypedArray()))
    }

    @Test
    fun `Reconstructed transaction back-chain is correct for linearly updated state`() {
        val originalStateTransactionIds = mutableListOf<SecureHash>()
        originalStateTransactionIds.add(issueCandyCoupons(aliceParty, 10))
        originalStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }, bobParty))
        originalStateTransactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref }, aliceParty))

        val reissuedStateTransactionIds = mutableListOf<SecureHash>()
        val aliceCandyCouponsToReissue = listAvailableCandyCoupons(aliceNode)
        reissuedStateTransactionIds.add(createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCouponsToReissue,
            candyShopParty
        ))
        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        reissuedStateTransactionIds.add(reissueRequestedStates(candyShopNode, reissuanceRequest))
        val exitCouponTransactionId = tearUpCandyCoupons(aliceNode, aliceCandyCouponsToReissue.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reissuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]
        reissuedStateTransactionIds.add(unlockReissuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reissuedStateAndRefs,
            reissuanceLock
        ))

        reissuedStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }, charlieParty))
        reissuedStateTransactionIds.add(giveCandyCoupons(charlieNode, listAvailableCandyCoupons(charlieNode).map { it.ref }, aliceParty))

        // alice, as a requester, can access the states before reissuance
        val aliceTransactionBackChain = reconstructTransactionBackChain(aliceNode, reissuedStateTransactionIds.last())
        assertThat(aliceTransactionBackChain, hasSize(`is`(originalStateTransactionIds.size + reissuedStateTransactionIds.size)))
        assertThat(aliceTransactionBackChain, hasItems(*(originalStateTransactionIds + reissuedStateTransactionIds).toTypedArray()))

        // bob, even though he is a participant of the states before reissuance, can't access the states after reissuance
        val bobTransactionBackChain = reconstructTransactionBackChain(bobNode, reissuedStateTransactionIds.last())
        assertThat(bobTransactionBackChain, `is`(empty()))

        // charlie can't access to the original state
        val charlieTransactionBackChain = reconstructTransactionBackChain(charlieNode, reissuedStateTransactionIds.last())
        assertThat(charlieTransactionBackChain, hasSize(`is`(reissuedStateTransactionIds.size)))
        assertThat(charlieTransactionBackChain, hasItems(*reissuedStateTransactionIds.toTypedArray()))
    }

    @Test
    fun `Reconstructed transaction back-chain is correct for not linearly updated state`() {
        val originalStateTransactionIds = mutableListOf<SecureHash>()
        originalStateTransactionIds.add(issueCandyCoupons(aliceParty, 10))
        originalStateTransactionIds.add(exchangeCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref },
            listOf(2, 3, 5)))
        originalStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(0, 2).map { it.ref },
            bobParty))
        originalStateTransactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 1).map { it.ref },
            aliceParty))
        originalStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref },
            bobParty))
        originalStateTransactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 2).map { it.ref },
            aliceParty))
        originalStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref },
            bobParty))
        originalStateTransactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref },
            aliceParty))

        val reissuedStateTransactionIds = mutableListOf<SecureHash>()
        val aliceCandyCouponsToReissue = listAvailableCandyCoupons(aliceNode)
        reissuedStateTransactionIds.add(createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCouponsToReissue,
            candyShopParty
        ))
        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        reissuedStateTransactionIds.add(reissueRequestedStates(candyShopNode, reissuanceRequest))
        val exitCouponTransactionId = tearUpCandyCoupons(aliceNode, aliceCandyCouponsToReissue.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reissuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]
        reissuedStateTransactionIds.add(unlockReissuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reissuedStateAndRefs,
            reissuanceLock
        ))

        reissuedStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }
            .subList(0, 2), bobParty))
        reissuedStateTransactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref }, aliceParty))

        reissuedStateTransactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }, charlieParty))
        reissuedStateTransactionIds.add(giveCandyCoupons(charlieNode, listAvailableCandyCoupons(charlieNode).map { it.ref }, aliceParty))

        // alice, as a requester, can access the states before reissuance
        val aliceTransactionBackChain = reconstructTransactionBackChain(aliceNode, reissuedStateTransactionIds.last())
        assertThat(aliceTransactionBackChain, hasSize(`is`(originalStateTransactionIds.size + reissuedStateTransactionIds.size)))
        assertThat(aliceTransactionBackChain, hasItems(*(originalStateTransactionIds + reissuedStateTransactionIds).toTypedArray()))

        // bob, even though he is a participant of the states before reissuance, can't access the states after reissuance
        val bobTransactionBackChain = reconstructTransactionBackChain(bobNode, reissuedStateTransactionIds.last())
        assertThat(bobTransactionBackChain, `is`(empty()))

        // charlie can't access to the original state
        val charlieTransactionBackChain = reconstructTransactionBackChain(charlieNode, reissuedStateTransactionIds.last())
        assertThat(charlieTransactionBackChain, hasSize(`is`(reissuedStateTransactionIds.size)))
        assertThat(charlieTransactionBackChain, hasItems(*reissuedStateTransactionIds.toTypedArray()))
    }

}
