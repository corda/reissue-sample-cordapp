package net.corda.samples.reissuance.candies.flows


import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.GetTransactionBackChain
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.contextLogger

@StartableByRPC
class ReconstructTransactionBackChain(
    private val transactionId: SecureHash
): FlowLogic<Set<SecureHash>>() {

    @Suspendable
    override fun call(): Set<SecureHash> {
        val endTransactionsToVisit = mutableSetOf<SecureHash>(transactionId)
        val backChain = mutableSetOf<SecureHash>()
        reconstructTransactionBackChain(endTransactionsToVisit, backChain)
        return backChain
    }

    private fun reconstructTransactionBackChain(
        endTransactionsToVisit: MutableSet<SecureHash>,
        backChain: MutableSet<SecureHash>
    ) {
        if(endTransactionsToVisit.isEmpty()) return

        val endTransaction = endTransactionsToVisit.elementAt(0)
        endTransactionsToVisit.remove(endTransaction)

        try {
            val transactionBackChain = subFlow(GetTransactionBackChain(endTransaction))
            backChain.addAll(transactionBackChain)

            // if transaction doesn't have any inputs and has an output of type ReissuanceRequest, the request contains
            // references of original states which can be used to reconstruct transaction back-chain
            val issuanceTransactionsSecureHashes = findIssuanceTransactions(transactionBackChain)
            issuanceTransactionsSecureHashes.forEach {
                val issuanceSignedTransaction = serviceHub.validatedTransactions.getTransaction(it)!!
                val issuanceLedgerTransaction = issuanceSignedTransaction.toLedgerTransaction(serviceHub)
                val reissuanceRequest = issuanceLedgerTransaction.outputs.find { it.data is ReissuanceRequest }
                if(reissuanceRequest != null)
                    endTransactionsToVisit.addAll((reissuanceRequest.data as ReissuanceRequest).stateRefsToReissue
                        .map { it.txhash })
            }
        }
        // BackChainException is throw when the transaction is not available in the vault - the party doesn't have
        // access the the state before reissuance
        catch (e: GetTransactionBackChain.BackChainException) {
            logger.info("Back-chain of transaction $endTransaction is not available")
        }

        reconstructTransactionBackChain(endTransactionsToVisit, backChain)
    }

    private fun findIssuanceTransactions(
        transactionBackChain: Set<SecureHash>
    ): Iterable<SecureHash> {
        return transactionBackChain.filter {
            serviceHub.validatedTransactions.getTransaction(it)!!.inputs.isEmpty()
        }
    }

}
