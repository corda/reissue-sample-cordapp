package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.internal.schemas.PersistentFungibleToken
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder

@StartableByRPC
class ListCandyCoupons(
    private val holderParty: Party,
    private val encumbered: Boolean? = false,
    private val couponRefs: List<StateRef>? = null
) : FlowLogic<List<StateAndRef<FungibleToken>>>() {

    @Suspendable
    override fun call(): List<StateAndRef<FungibleToken>> {
        val tokenTypeCriteria = QueryCriteria.VaultCustomQueryCriteria(
            builder { PersistentFungibleToken::tokenIdentifier.equal(TokenType("CandyCoupon", 0).tokenIdentifier) })
        val tokenHolderCriteria = QueryCriteria.VaultCustomQueryCriteria(
            builder { PersistentFungibleToken::holder.equal(holderParty) })
        val criteria = if(couponRefs == null)
            tokenTypeCriteria.and(tokenHolderCriteria)
        else {
            val referenceCriteria = QueryCriteria.VaultQueryCriteria(stateRefs = couponRefs)
            tokenTypeCriteria.and(tokenHolderCriteria).and(referenceCriteria)
        }
        val availableCoupons = serviceHub.vaultService.queryBy<FungibleToken>(criteria).states
        if(encumbered == null)
            return availableCoupons
        if(encumbered)
            return availableCoupons.filter { it.state.encumbrance != null }
        return availableCoupons.filter { it.state.encumbrance == null }
    }
}
