package net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC

@StartableByRPC
class GetTransactionBackChain(
    private val transactionId: SecureHash
): FlowLogic<Set<SecureHash>>() {

    @Suspendable
    override fun call(): Set<SecureHash> {
        val visitedTransactions = mutableSetOf<SecureHash>()
        val transactionsToVisitQueue = mutableSetOf<SecureHash>(transactionId)
        val backChain = getTransactionBackChain(transactionId, visitedTransactions, transactionsToVisitQueue)
        return backChain
    }

    private fun getTransactionBackChain(
        transactionId: SecureHash,
        visitedTransactions: MutableSet<SecureHash>,
        transactionsToVisitQueue: MutableSet<SecureHash>
    ): Set<SecureHash> {
        val signedTransaction = serviceHub.validatedTransactions.getTransaction(transactionId)
            ?: throw BackChainException("Cannot find transaction with id $transactionId")

        transactionsToVisitQueue.remove(transactionId)
        visitedTransactions.add(transactionId)

        transactionsToVisitQueue.addAll(signedTransaction.inputs.map { it.txhash })

        if(transactionsToVisitQueue.isEmpty())
            return visitedTransactions
        return getTransactionBackChain(transactionsToVisitQueue.elementAt(0), visitedTransactions,
            transactionsToVisitQueue)
    }

    class BackChainException(
        message: String, cause: Throwable? = null
    ) : FlowException(message, cause)

}
