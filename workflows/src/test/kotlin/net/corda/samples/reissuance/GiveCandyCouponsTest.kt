package net.corda.samples.reissuance

import net.corda.core.identity.AbstractParty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class GiveCandyCouponsTest: AbstractCandyFlowTest() {

    @Test
    fun `Issue and give one coupon`() {
        issueCandyCoupons(aliceParty, 5)
        val aliceTokensBeforeGive = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, aliceTokensBeforeGive.map { it.ref }, bobParty)
        val aliceTokensAfterGive = listAvailableCandyCoupons(aliceNode)
        val bobTokensAfterGive = listAvailableCandyCoupons(bobNode)

        assertThat(aliceTokensAfterGive, empty())
        assertThat(bobTokensAfterGive, hasSize(`is`(1)))
        assertThat(bobTokensAfterGive[0].state.data.amount.quantity.toInt(), `is`(5))
        assertThat(bobTokensAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobTokensAfterGive[0].state.data.holder, `is`(bobParty as AbstractParty))
    }

    @Test
    fun `Issue and give many coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val aliceTokensBeforeGive = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, aliceTokensBeforeGive.map { it.ref }, bobParty)
        val aliceTokensAfterGive = listAvailableCandyCoupons(aliceNode)
        val bobTokensAfterGive = listAvailableCandyCoupons(bobNode)

        assertThat(aliceTokensAfterGive, empty())
        assertThat(bobTokensAfterGive, hasSize(`is`(2)))
        assertThat(bobTokensAfterGive.map { it.state.data.amount.quantity.toInt() }, hasItems(2, 3))
        assertThat(bobTokensAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobTokensAfterGive[0].state.data.holder, `is`(bobParty as AbstractParty))
        assertThat(bobTokensAfterGive[1].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobTokensAfterGive[1].state.data.holder, `is`(bobParty as AbstractParty))
    }

    @Test
    fun `Issue many and give some of the coupons`() {
        issueCandyCoupons(aliceParty, 2)
        issueCandyCoupons(aliceParty, 3)
        val aliceTokensBeforeGive = listAvailableCandyCoupons(aliceNode)
        giveCandyCoupons(aliceNode, listOf(aliceTokensBeforeGive[0].ref), bobParty)
        val aliceTokensAfterGive = listAvailableCandyCoupons(aliceNode)
        val bobTokensAfterGive = listAvailableCandyCoupons(bobNode)

        assertThat(aliceTokensAfterGive, hasSize(`is`(1)))
        assertThat(bobTokensAfterGive, hasSize(`is`(1)))
        assertThat(aliceTokensAfterGive[0].state.data.amount.quantity.toInt(), `is`(3))
        assertThat(bobTokensAfterGive[0].state.data.amount.quantity.toInt(), `is`(2))
        assertThat(aliceTokensAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(aliceTokensAfterGive[0].state.data.holder, `is`(aliceParty as AbstractParty))
        assertThat(bobTokensAfterGive[0].state.data.issuedTokenType, `is`(issuedCandyCouponTokenType))
        assertThat(bobTokensAfterGive[0].state.data.holder, `is`(bobParty as AbstractParty))
    }

}
