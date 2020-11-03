package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.samples.reissuance.candies.states.Candy

@StartableByRPC
class ListCandies: FlowLogic<List<StateAndRef<Candy>>>() {

    @Suspendable
    override fun call(): List<StateAndRef<Candy>> {
        return serviceHub.vaultService.queryBy<Candy>().states
    }
}
