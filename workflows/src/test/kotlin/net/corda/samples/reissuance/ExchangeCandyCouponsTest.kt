package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class ExchangeCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Split 1 coupon into 2 groups`() {
        issueCandyCoupons(aliceParty, 5)
        val aliceTokensBeforeExchange = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, aliceTokensBeforeExchange.map { it.ref }, listOf(1, 4))
        val aliceTokensAfterExchange = listAvailableCandyCoupons(aliceNode)
        assertThat(aliceTokensAfterExchange, hasSize(`is`(2)))
        val aliceTokenQuantities = aliceTokensAfterExchange.map { it.state.data.amount.quantity.toInt() }
        assertThat(aliceTokenQuantities, hasItems(1, 4))
        assertThat(aliceTokensAfterExchange[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceTokensAfterExchange[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(aliceTokensAfterExchange[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceTokensAfterExchange[1].state.data.holder, `is`(aliceParty as AbstractParty))
    }

    @Test
    fun `Split 2 coupons into 2 other groups`() {
        issueCandyCoupons(aliceParty, 1)
        issueCandyCoupons(aliceParty, 4)
        val aliceTokensBeforeExchange = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, aliceTokensBeforeExchange.map { it.ref }, listOf(2, 3))
        val aliceTokensAfterExchange = listAvailableCandyCoupons(aliceNode)
        assertThat(aliceTokensAfterExchange, hasSize(`is`(2)))
        val aliceTokenQuantities = aliceTokensAfterExchange.map { it.state.data.amount.quantity.toInt() }
        assertThat(aliceTokenQuantities, hasItems(2, 3))
        assertThat(aliceTokensAfterExchange[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceTokensAfterExchange[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(aliceTokensAfterExchange[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceTokensAfterExchange[1].state.data.holder, `is`(aliceParty as AbstractParty))
    }

}
