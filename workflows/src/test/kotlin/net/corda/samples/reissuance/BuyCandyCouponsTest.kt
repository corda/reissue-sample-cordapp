package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class BuyCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Issue one coupon and use it to buy candies`() {
        issueCandyCoupons(aliceParty, 5)
        val issuedCouponRefs = listAvailableCandyCoupons(aliceNode)
        buyCandiesUsingCoupons(aliceNode, issuedCouponRefs.map { it.ref })
        val couponRefsAfterBuyingCandies = listAvailableCandyCoupons(aliceNode)
        val boughtCandies = listAvailableCandies(aliceNode)
        val boughtCandiesOwners = boughtCandies.map { it.state.data.owner }

        assertThat(couponRefsAfterBuyingCandies, empty())

        assertThat(boughtCandies, hasSize(`is`(5)))
        assertThat(boughtCandiesOwners.toSet(), hasSize(`is`(1)))
        assertThat(boughtCandiesOwners[0], `is`(aliceParty as AbstractParty))
    }

    @Test
    fun `Issue many coupons and use them to buy candies`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val issuedCouponRefs = listAvailableCandyCoupons(aliceNode)
        buyCandiesUsingCoupons(aliceNode, issuedCouponRefs.map { it.ref })
        val couponRefsAfterBuyingCandies = listAvailableCandyCoupons(aliceNode)
        val boughtCandies = listAvailableCandies(aliceNode)
        val boughtCandiesOwners = boughtCandies.map { it.state.data.owner }

        assertThat(couponRefsAfterBuyingCandies, empty())

        assertThat(boughtCandies, hasSize(`is`(5)))
        assertThat(boughtCandiesOwners.toSet(), hasSize(`is`(1)))
        assertThat(boughtCandiesOwners[0], `is`(aliceParty as AbstractParty))
    }

    @Test
    fun `Issue many coupons and use it to buy candies separately`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val issuedCoupons = listAvailableCandyCoupons(aliceNode)
        buyCandiesUsingCoupons(aliceNode, listOf(issuedCoupons[0].ref))
        val couponRefsAfterBuyingCandies = listAvailableCandyCoupons(aliceNode)
        val boughtCandies = listAvailableCandies(aliceNode)
        val boughtCandiesOwners = boughtCandies.map { it.state.data.owner }

        assertThat(couponRefsAfterBuyingCandies, hasSize(`is`(1)))
        assertThat(couponRefsAfterBuyingCandies[0].state.data.amount.quantity.toInt(), `is`(3))
        assertThat(couponRefsAfterBuyingCandies[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(couponRefsAfterBuyingCandies[0].state.data.holder, `is`(aliceParty as AbstractParty))

        assertThat(boughtCandies, hasSize(`is`(2)))
        assertThat(boughtCandiesOwners.toSet(), hasSize(`is`(1)))
        assertThat(boughtCandiesOwners[0], `is`(aliceParty as AbstractParty))
    }
}
