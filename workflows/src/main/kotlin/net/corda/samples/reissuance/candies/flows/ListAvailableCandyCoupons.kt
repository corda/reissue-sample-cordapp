package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.internal.schemas.PersistentFungibleToken
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder

@StartableByRPC
class ListAvailableCandyCoupons(
    private val holderParty: Party,
    private val encumbered: Boolean? = false
) : FlowLogic<List<StateAndRef<FungibleToken>>>() {

    @Suspendable
    override fun call(): List<StateAndRef<FungibleToken>> {
        val demoAppTokenType = TokenType("CandyCoupon", 0)
        val tokenTypeCriteria = QueryCriteria.VaultCustomQueryCriteria(
            builder { PersistentFungibleToken::tokenIdentifier.equal(demoAppTokenType.tokenIdentifier) })
        val tokenHolderCriteria = QueryCriteria.VaultCustomQueryCriteria(
            builder { PersistentFungibleToken::holder.equal(holderParty) })
        val criteria = tokenTypeCriteria.and(tokenHolderCriteria)
        val availableTokens = serviceHub.vaultService.queryBy<FungibleToken>(criteria).states
        if(encumbered == null)
            return availableTokens
        if(encumbered)
            return availableTokens.filter { it.state.encumbrance != null }
        return availableTokens.filter { it.state.encumbrance == null }
    }
}
