package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC

@StartableByRPC
class GetTransactionBackChain(
    private val transactionId: SecureHash
): FlowLogic<List<SecureHash>>() {

    @Suspendable
    override fun call(): List<SecureHash> {
        TODO("Not yet implemented")
    }

}