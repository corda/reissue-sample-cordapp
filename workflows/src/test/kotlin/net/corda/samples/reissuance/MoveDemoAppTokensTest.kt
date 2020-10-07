package net.corda.samples.reissuance

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class MoveDemoAppTokensTest: AbstractDemoAppFlowTest() {

    @Test
    fun `Move a given quantity of tokens`() {
        issueDemoAppTokens(aliceParty, 5L)
        moveDemoAppTokens(aliceNode, bobParty, 1L)
        assertThat(getHolderTokensQuantity(aliceNode), `is`(4L))
        assertThat(getHolderTokensQuantity(bobNode), `is`(1L))
    }

    @Test
    fun `Move a given quantity of token from many fungible states`() {
        issueDemoAppTokens(aliceParty, 10L)
        issueDemoAppTokens(aliceParty, 10L)
        moveDemoAppTokens(aliceNode, bobParty, 15L)
        assertThat(getHolderTokensQuantity(aliceNode), `is`(5L))
        assertThat(getHolderTokensQuantity(bobNode), `is`(15L))
    }

    @Test
    fun `Split 1 token into 2 groups`() {
        issueDemoAppTokens(aliceParty, 5L)
        moveDemoAppTokens(aliceNode, aliceParty, 1L)
        val aliceTokens = listAvailableTokens(aliceNode)
        assertThat(aliceTokens, hasSize(`is`(2)))
        val aliceTokenQuantities = aliceTokens.map { it.state.data.amount.quantity }
        assertThat(aliceTokenQuantities, hasItem(1L))
        assertThat(aliceTokenQuantities, hasItem(4L))
    }

    @Test
    fun `Split many tokens into 2 groups`() {
        issueDemoAppTokens(aliceParty, 1L)
        issueDemoAppTokens(aliceParty, 1L)
        issueDemoAppTokens(aliceParty, 1L)
        issueDemoAppTokens(aliceParty, 1L)
        issueDemoAppTokens(aliceParty, 1L)
        moveDemoAppTokens(aliceNode, aliceParty, 2L)
        val aliceTokens = listAvailableTokens(aliceNode)
        assertThat(aliceTokens, hasSize(`is`(2)))
        val aliceTokenQuantities = aliceTokens.map { it.state.data.amount.quantity }
        assertThat(aliceTokenQuantities, hasItem(2L))
        assertThat(aliceTokenQuantities, hasItem(3L))
    }

}
