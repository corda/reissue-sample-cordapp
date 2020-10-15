package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class TearUpCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Issue and tear up one coupon`() {
        issueCandyCoupons(aliceParty, 5)
        val couponRefsBeforeRedeem = listAvailableCandyCoupons(aliceNode)
        tearUpCandyCoupons(aliceNode, couponRefsBeforeRedeem.map { it.ref })
        val couponRefsAfterRedeem = listAvailableCandyCoupons(aliceNode)
        assertThat(couponRefsAfterRedeem, empty())
    }

    @Test
    fun `Issue and tear up many coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val couponRefsBeforeRedeem = listAvailableCandyCoupons(aliceNode)
        tearUpCandyCoupons(aliceNode, couponRefsBeforeRedeem.map{ it.ref })
        val couponRefsAfterRedeem = listAvailableCandyCoupons(aliceNode)
        assertThat(couponRefsAfterRedeem, empty())
    }

    @Test
    fun `Issue many and tear up some of the coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val couponRefsBeforeRedeem = listAvailableCandyCoupons(aliceNode)
        tearUpCandyCoupons(aliceNode, listOf(couponRefsBeforeRedeem[0].ref))
        val couponRefsAfterRedeem = listAvailableCandyCoupons(aliceNode)
        assertThat(couponRefsAfterRedeem, hasSize(`is`(1)))
        assertThat(couponRefsAfterRedeem[0].state.data.amount.quantity.toInt(), `is`(3))
        assertThat(couponRefsAfterRedeem[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(couponRefsAfterRedeem[0].state.data.holder, `is`(aliceParty as AbstractParty))
    }
}
