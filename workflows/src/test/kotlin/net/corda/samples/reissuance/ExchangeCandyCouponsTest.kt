package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class ExchangeCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Split 1 coupon into 2 groups`() {
        issueCandyCoupons(aliceParty, 5)
        val aliceCouponsBeforeExchange = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, aliceCouponsBeforeExchange.map { it.ref }, listOf(1, 4))
        val aliceCouponsAfterExchange = listAvailableCandyCoupons(aliceNode)
        assertThat(aliceCouponsAfterExchange, hasSize(`is`(2)))
        val aliceTokenQuantities = aliceCouponsAfterExchange.map { it.state.data.amount.quantity.toInt() }
        assertThat(aliceTokenQuantities, hasItems(1, 4))
        assertThat(aliceCouponsAfterExchange[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceCouponsAfterExchange[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(aliceCouponsAfterExchange[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceCouponsAfterExchange[1].state.data.holder, `is`(aliceParty as AbstractParty))
    }

    @Test
    fun `Split 2 coupons into 2 other groups`() {
        issueCandyCoupons(aliceParty, 1)
        issueCandyCoupons(aliceParty, 4)
        val aliceCouponsBeforeExchange = listAvailableCandyCoupons(aliceNode)
        exchangeCandyCoupons(aliceNode, aliceCouponsBeforeExchange.map { it.ref }, listOf(2, 3))
        val aliceCouponsAfterExchange = listAvailableCandyCoupons(aliceNode)
        assertThat(aliceCouponsAfterExchange, hasSize(`is`(2)))
        val aliceTokenQuantities = aliceCouponsAfterExchange.map { it.state.data.amount.quantity.toInt() }
        assertThat(aliceTokenQuantities, hasItems(2, 3))
        assertThat(aliceCouponsAfterExchange[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceCouponsAfterExchange[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(aliceCouponsAfterExchange[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceCouponsAfterExchange[1].state.data.holder, `is`(aliceParty as AbstractParty))
    }

}
