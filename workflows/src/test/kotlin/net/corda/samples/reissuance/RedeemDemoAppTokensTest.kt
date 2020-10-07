package net.corda.samples.reissuance

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class RedeemDemoAppTokensTest: AbstractDemoAppFlowTest() {

    @Test
    fun `Redeem a given quantity of tokens`() {
        issueDemoAppTokens(aliceParty, 5L)
        redeemDemoAppTokens(aliceNode, tokenAmount = 1L)
        assertThat(getHolderTokensQuantity(aliceNode), `is`(4L))
    }

    @Test
    fun `Redeem a given quantity of token from many fungible states`() {
        issueDemoAppTokens(aliceParty, 10L)
        issueDemoAppTokens(aliceParty, 10L)
        issueDemoAppTokens(aliceParty, 10L)
        redeemDemoAppTokens(aliceNode, tokenAmount = 15L)
        assertThat(getHolderTokensQuantity(aliceNode), `is`(15L))
    }

    @Test
    fun `Redeem tokens by state reference`() {
        issueDemoAppTokens(aliceParty, 5L)
        issueDemoAppTokens(aliceParty, 5L)
        val tokenRefsBeforeRedeem = listAvailableTokens(aliceNode).map { it.ref }
        redeemDemoAppTokens(aliceNode, tokenRefs = listOf(tokenRefsBeforeRedeem[0]))
        val tokenRefsAfterRedeem = listAvailableTokens(aliceNode).map { it.ref }
        assertThat(tokenRefsAfterRedeem, hasSize(`is`(1)))
        assertThat(tokenRefsAfterRedeem, hasItem(tokenRefsBeforeRedeem[1]))
    }

}
