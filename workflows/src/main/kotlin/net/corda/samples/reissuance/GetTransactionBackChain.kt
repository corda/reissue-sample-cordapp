package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import com.r3.dr.ledgergraph.services.LedgerGraphService
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC

@StartableByRPC
class GetTransactionBackChain(
    private val transactionId: SecureHash
): FlowLogic<Set<SecureHash>>() {

    @Suspendable
    override fun call(): Set<SecureHash> {
        val ledgerGraphService = serviceHub.cordaService(LedgerGraphService::class.java)
        return ledgerGraphService.getBackchain(setOf(transactionId))
    }
}
