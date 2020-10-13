package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class ThrowAwayCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Issue and throw away one coupon`() {
        issueCandyCoupons(aliceParty, 5)
        val couponRefsBeforeRedeem = listAvailableCandyCoupons(aliceNode)
        throwAwayCandyCoupons(aliceNode, couponRefsBeforeRedeem.map { it.ref })
        val couponRefsAfterRedeem = listAvailableCandyCoupons(aliceNode)
        assertThat(couponRefsAfterRedeem, empty())
    }

    @Test
    fun `Issue and throw away many coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val couponRefsBeforeRedeem = listAvailableCandyCoupons(aliceNode)
        throwAwayCandyCoupons(aliceNode, couponRefsBeforeRedeem.map{ it.ref })
        val couponRefsAfterRedeem = listAvailableCandyCoupons(aliceNode)
        assertThat(couponRefsAfterRedeem, empty())
    }

    @Test
    fun `Issue many and throw away some of the coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val couponRefsBeforeRedeem = listAvailableCandyCoupons(aliceNode)
        throwAwayCandyCoupons(aliceNode, listOf(couponRefsBeforeRedeem[0].ref))
        val couponRefsAfterRedeem = listAvailableCandyCoupons(aliceNode)
        assertThat(couponRefsAfterRedeem, hasSize(`is`(1)))
        assertThat(couponRefsAfterRedeem[0].state.data.amount.quantity.toInt(), `is`(3))
        assertThat(couponRefsAfterRedeem[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(couponRefsAfterRedeem[0].state.data.holder, `is`(aliceParty as AbstractParty))
    }
}
