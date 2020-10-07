package net.corda.samples.reissuance

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class IssueDemoAppTokensTest: AbstractDemoAppFlowTest() {

    @Test
    fun `Initially there are no tokens in holder's vault`() {
        assertThat(getHolderTokensQuantity(aliceNode), `is`(0L))
    }

    @Test
    fun `Issued tokens are in holder's vault`() {
        val tokenAmount = 5L
        issueDemoAppTokens(aliceParty, tokenAmount)
        assertThat(getHolderTokensQuantity(aliceNode), `is`(tokenAmount))
    }
}
