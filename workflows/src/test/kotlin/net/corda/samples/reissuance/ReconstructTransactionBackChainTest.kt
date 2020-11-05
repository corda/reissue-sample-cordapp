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
        val transactionIds = mutableListOf<SecureHash>()
        transactionIds.add(issueCandyCoupons(aliceParty, 10))
        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }, bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref }, aliceParty))

        val aliceCandyCouponsToReissue = listAvailableCandyCoupons(aliceNode)
        transactionIds.add(createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCouponsToReissue,
            candyShopParty
        ))
        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        transactionIds.add(reissueRequestedStates(candyShopNode, reissuanceRequest))
        val exitCouponTransactionId = tearUpCandyCoupons(aliceNode, aliceCandyCouponsToReissue.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reissuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]
        transactionIds.add(unlockReissuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reissuedStateAndRefs,
            reissuanceLock
        ))

        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }, bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref }, aliceParty))

        val transactionBackChain = reconstructTransactionBackChain(aliceNode, transactionIds.last())
        assertThat(transactionBackChain, hasSize(`is`(transactionIds.size)))
        assertThat(transactionBackChain, hasItems(*transactionIds.toTypedArray()))
    }

    @Test
    fun `Reconstructed transaction back-chain is correct for not linearly updated state`() {
        val transactionIds = mutableListOf<SecureHash>()
        transactionIds.add(issueCandyCoupons(aliceParty, 10))
        transactionIds.add(exchangeCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref },
            listOf(2, 3, 5)))
        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).subList(0, 2).map { it.ref },
            bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 1).map { it.ref },
            aliceParty))
        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref },
            bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).subList(0, 2).map { it.ref },
            aliceParty))
        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref },
            bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref },
            aliceParty))

        val aliceCandyCouponsToReissue = listAvailableCandyCoupons(aliceNode)
        transactionIds.add(createCandyCouponReissuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceCandyCouponsToReissue,
            candyShopParty
        ))
        val reissuanceRequest = candyShopNode.services.vaultService.queryBy<ReissuanceRequest>().states[0]
        transactionIds.add(reissueRequestedStates(candyShopNode, reissuanceRequest))
        val exitCouponTransactionId = tearUpCandyCoupons(aliceNode, aliceCandyCouponsToReissue.map { it.ref })
        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, exitCouponTransactionId)
        val reissuedStateAndRefs = listAvailableCandyCoupons(aliceNode, true)
        val reissuanceLock = aliceNode.services.vaultService.queryBy<ReissuanceLock<FungibleToken>>().states[0]
        transactionIds.add(unlockReissuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reissuedStateAndRefs,
            reissuanceLock
        ))

        transactionIds.add(giveCandyCoupons(aliceNode, listAvailableCandyCoupons(aliceNode).map { it.ref }
            .subList(0, 2), bobParty))
        transactionIds.add(giveCandyCoupons(bobNode, listAvailableCandyCoupons(bobNode).map { it.ref }, aliceParty))

        val transactionBackChain = reconstructTransactionBackChain(aliceNode, transactionIds.last())
        assertThat(transactionBackChain, hasSize(`is`(transactionIds.size)))
        assertThat(transactionBackChain, hasItems(*transactionIds.toTypedArray()))
    }

}
