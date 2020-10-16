package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class GiveCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Issue and give one coupon`() {
        issueCandyCoupons(aliceParty, 5)
        val aliceCouponsBeforeGiveness = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, aliceCouponsBeforeGiveness.map { it.ref }, bobParty)
        val aliceCouponsAfterGiveness = listAvailableCandyCoupons(aliceNode)
        val bobCouponsAfterGiveness = listAvailableCandyCoupons(bobNode)

        assertThat(aliceCouponsAfterGiveness, empty())
        assertThat(bobCouponsAfterGiveness, hasSize(`is`(1)))
        assertThat(bobCouponsAfterGiveness[0].state.data.amount.quantity.toInt(), `is`(5))
        assertThat(bobCouponsAfterGiveness[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGiveness[0].state.data.holder, `is`(bobParty as AbstractParty))
    }

    @Test
    fun `Issue and give many coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val aliceCouponsBeforeGiveness = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, aliceCouponsBeforeGiveness.map { it.ref }, bobParty)
        val aliceCouponsAfterGiveness = listAvailableCandyCoupons(aliceNode)
        val bobCouponsAfterGiveness = listAvailableCandyCoupons(bobNode)

        assertThat(aliceCouponsAfterGiveness, empty())
        assertThat(bobCouponsAfterGiveness, hasSize(`is`(2)))
        assertThat(bobCouponsAfterGiveness.map { it.state.data.amount.quantity.toInt() }, hasItems(2, 3))
        assertThat(bobCouponsAfterGiveness[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGiveness[0].state.data.holder, `is`(bobParty as AbstractParty))
        assertThat(bobCouponsAfterGiveness[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGiveness[1].state.data.holder, `is`(bobParty as AbstractParty))
    }

    @Test
    fun `Issue many and give some of the coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val aliceCouponsBeforeGiveness = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, listOf(aliceCouponsBeforeGiveness[0].ref), bobParty)
        val aliceCouponsAfterGiveness = listAvailableCandyCoupons(aliceNode)
        val bobCouponsAfterGiveness = listAvailableCandyCoupons(bobNode)

        assertThat(aliceCouponsAfterGiveness, hasSize(`is`(1)))
        assertThat(bobCouponsAfterGiveness, hasSize(`is`(1)))
        assertThat(aliceCouponsAfterGiveness[0].state.data.amount.quantity.toInt(), `is`(3))
        assertThat(bobCouponsAfterGiveness[0].state.data.amount.quantity.toInt(), `is`(2))
        assertThat(aliceCouponsAfterGiveness[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceCouponsAfterGiveness[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(bobCouponsAfterGiveness[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobCouponsAfterGiveness[0].state.data.holder, `is`(bobParty as AbstractParty))
    }

}
