package net.corda.samples.reissuance

import com.r3.corda.lib.tokens.contracts.commands.RedeemTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.reissuance.states.ReIssuanceLock
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import net.corda.core.node.services.queryBy
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class UnlockReIssuedStatesTest: AbstractDemoAppFlowTest() {

    @Test
    fun `Tokens are re-issued and back-chain is pruned`() {
        issueDemoAppTokens(aliceParty, 12L)
        moveDemoAppTokens(aliceNode, bobParty, 11L)
        moveDemoAppTokens(bobNode, charlieParty, 10L)
        moveDemoAppTokens(charlieNode, aliceParty, 9L)
        moveDemoAppTokens(aliceNode, bobParty, 8L)
        moveDemoAppTokens(bobNode, charlieParty, 7L)
        moveDemoAppTokens(charlieNode, aliceParty, 6L)

        val aliceTokens = listAvailableTokens(aliceNode, encumbered = false)

        // Alice should own exactly 2 token inputs
        val firstTokenBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceTokens[0].ref.txhash)
        val secondTokenBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceTokens[1].ref.txhash)

        assertThat(firstTokenBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(5)))
        assertThat(secondTokenBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(7)))
        assertThat(secondTokenBeforeReIssuanceBackChainTransactionIds, hasItems(
            *firstTokenBeforeReIssuanceBackChainTransactionIds.toTypedArray()))

        createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceTokens,
            bankParty
        )
        val reIssuanceRequest = bankNode.services.vaultService.queryBy<ReIssuanceRequest>().states[0]

        reIssueRequestedStates(bankNode, reIssuanceRequest, true)

        val redeemDemoAppTokensTransactionId = redeemDemoAppTokens(aliceNode, tokenRefs = aliceTokens.map { it.ref })

        val attachmentSecureHash = uploadDeletedStateAttachment(aliceNode, redeemDemoAppTokensTransactionId)

        val reIssuedStateAndRefs = listAvailableTokens(aliceNode, true)
        val reIssuanceLock = aliceNode.services.vaultService.queryBy<ReIssuanceLock<FungibleToken>>().states[0]

        unlockReIssuedState(
            aliceNode,
            listOf(attachmentSecureHash),
            reIssuedStateAndRefs,
            reIssuanceLock
        )

        val aliceReIssuedTokens = listAvailableTokens(aliceNode, encumbered = false)
        assertThat(aliceReIssuedTokens.map { it.state.data }, `is`(aliceTokens.map { it.state.data }))

        val firstTokenAfterReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceReIssuedTokens[0].ref.txhash)
        val secondTokenAfterReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceReIssuedTokens[1].ref.txhash)

        assertThat(firstTokenAfterReIssuanceBackChainTransactionIds, hasSize(`is`(3)))
        assertThat(firstTokenAfterReIssuanceBackChainTransactionIds, `is`(
            secondTokenAfterReIssuanceBackChainTransactionIds))
        assertThat(firstTokenAfterReIssuanceBackChainTransactionIds,
            everyItem(not(`is`(`in`(firstTokenBeforeReIssuanceBackChainTransactionIds)))))
        assertThat(secondTokenAfterReIssuanceBackChainTransactionIds,
            everyItem(not(`is`(`in`(secondTokenBeforeReIssuanceBackChainTransactionIds)))))
    }

    @Test
    fun `Re-issuance request is rejected`() {
        issueDemoAppTokens(aliceParty, 12L)
        moveDemoAppTokens(aliceNode, bobParty, 11L)
        moveDemoAppTokens(bobNode, charlieParty, 10L)
        moveDemoAppTokens(charlieNode, aliceParty, 9L)
        moveDemoAppTokens(aliceNode, bobParty, 8L)
        moveDemoAppTokens(bobNode, charlieParty, 7L)
        moveDemoAppTokens(charlieNode, aliceParty, 6L)

        val aliceTokens = listAvailableTokens(aliceNode, encumbered = false)

        // Alice should own exactly 2 token inputs
        val firstTokenBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceTokens[0].ref.txhash)
        val secondTokenBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceTokens[1].ref.txhash)

        assertThat(firstTokenBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(5)))
        assertThat(secondTokenBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(7)))
        assertThat(secondTokenBeforeReIssuanceBackChainTransactionIds, hasItems(
            *firstTokenBeforeReIssuanceBackChainTransactionIds.toTypedArray()))

        createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceTokens,
            bankParty
        )
        val reIssuanceRequest = bankNode.services.vaultService.queryBy<ReIssuanceRequest>().states[0]

        rejectReIssuanceRequested(bankNode, reIssuanceRequest)

        val aliceTokensAfterReIssuanceRejection = listAvailableTokens(aliceNode, encumbered = false)
        assertThat(aliceTokensAfterReIssuanceRejection, `is`(aliceTokens))
    }

    @Test
    fun `Original tokens are spent after re-issuance and re-issued tokens are deleted`() {
        issueDemoAppTokens(aliceParty, 12L)
        moveDemoAppTokens(aliceNode, bobParty, 11L)
        moveDemoAppTokens(bobNode, charlieParty, 10L)
        moveDemoAppTokens(charlieNode, aliceParty, 9L)
        moveDemoAppTokens(aliceNode, bobParty, 8L)
        moveDemoAppTokens(bobNode, charlieParty, 7L)
        moveDemoAppTokens(charlieNode, aliceParty, 6L)

        val aliceTokens = listAvailableTokens(aliceNode, encumbered = null)

        // Alice should own exactly 2 token inputs
        val firstTokenBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceTokens[0].ref.txhash)
        val secondTokenBeforeReIssuanceBackChainTransactionIds = getTransactionBackChain(aliceNode,
            aliceTokens[1].ref.txhash)

        assertThat(firstTokenBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(5)))
        assertThat(secondTokenBeforeReIssuanceBackChainTransactionIds, hasSize(`is`(7)))
        assertThat(secondTokenBeforeReIssuanceBackChainTransactionIds, hasItems(
            *firstTokenBeforeReIssuanceBackChainTransactionIds.toTypedArray()))

        createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
            aliceNode,
            aliceTokens,
            bankParty
        )
        val reIssuanceRequest = bankNode.services.vaultService.queryBy<ReIssuanceRequest>().states[0]

        reIssueRequestedStates(bankNode, reIssuanceRequest, true)

        moveDemoAppTokens(aliceNode, bobParty, 1L)
        val unencumberedAliceTokensAfterMove = listAvailableTokens(aliceNode, encumbered = false)

        val reIssuedStates = listAvailableTokens(aliceNode, encumbered = true)
        val reIssuanceLock = aliceNode.services.vaultService.queryBy<ReIssuanceLock<FungibleToken>>().states[0]
        deleteReIssuedStatesAndLock(
            aliceNode,
            reIssuanceLock,
            reIssuedStates
        )

        val aliceTokensAfterDeletion = listAvailableTokens(aliceNode, encumbered = null)
        assertThat(aliceTokensAfterDeletion, `is`(unencumberedAliceTokensAfterMove))
    }
}
