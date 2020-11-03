package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class IssueCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Initially there are no coupons in holder's vault`() {
        val availableCandyCoupons = listAvailableCandyCoupons(aliceNode)
        assertThat(availableCandyCoupons, empty())
    }

    @Test
    fun `Issued coupons are in holder's vault`() {
        val candies = 5
        issueCandyCoupons(aliceParty, candies)
        val availableCandyCoupons = listAvailableCandyCoupons(aliceNode)
        assertThat(availableCandyCoupons, hasSize(`is`(1)))
        assertThat(availableCandyCoupons[0].state.data.amount.quantity.toInt(), `is`(candies))
        assertThat(availableCandyCoupons[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(availableCandyCoupons[0].state.data.holder, `is`(aliceParty as AbstractParty))
    }
}
