package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class GiveCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Issue and give one coupon`() {
        issueCandyCoupons(aliceParty, 5)
        val aliceCouponsBeforeGive = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, aliceCouponsBeforeGive.map { it.ref }, bobParty)
        val aliceCouponsAfterGive = listAvailableCandyCoupons(aliceNode)
        val bobCouponsAfterGive = listAvailableCandyCoupons(bobNode)

        assertThat(aliceCouponsAfterGive, empty())
        assertThat(bobCouponsAfterGive, hasSize(`is`(1)))
        assertThat(bobCouponsAfterGive[0].state.data.amount.quantity.toInt(), `is`(5))
        assertThat(bobCouponsAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGive[0].state.data.holder, `is`(bobParty as AbstractParty))
    }

    @Test
    fun `Issue and give many coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val aliceCouponsBeforeGive = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, aliceCouponsBeforeGive.map { it.ref }, bobParty)
        val aliceCouponsAfterGive = listAvailableCandyCoupons(aliceNode)
        val bobCouponsAfterGive = listAvailableCandyCoupons(bobNode)

        assertThat(aliceCouponsAfterGive, empty())
        assertThat(bobCouponsAfterGive, hasSize(`is`(2)))
        assertThat(bobCouponsAfterGive.map { it.state.data.amount.quantity.toInt() }, hasItems(2, 3))
        assertThat(bobCouponsAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGive[0].state.data.holder, `is`(bobParty as AbstractParty))
        assertThat(bobCouponsAfterGive[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGive[1].state.data.holder, `is`(bobParty as AbstractParty))
    }

    @Test
    fun `Issue many and give some of the coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val aliceCouponsBeforeGive = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, listOf(aliceCouponsBeforeGive[0].ref), bobParty)
        val aliceCouponsAfterGive = listAvailableCandyCoupons(aliceNode)
        val bobCouponsAfterGive = listAvailableCandyCoupons(bobNode)

        assertThat(aliceCouponsAfterGive, hasSize(`is`(1)))
        assertThat(bobCouponsAfterGive, hasSize(`is`(1)))
        assertThat(aliceCouponsAfterGive[0].state.data.amount.quantity.toInt(), `is`(3))
        assertThat(bobCouponsAfterGive[0].state.data.amount.quantity.toInt(), `is`(2))
        assertThat(aliceCouponsAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceCouponsAfterGive[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(bobCouponsAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGive[0].state.data.holder, `is`(bobParty as AbstractParty))
    }

}
