package net.corda.samples.reissuance.candies.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.states.ReissuanceLock
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.selection.memory.services.VaultWatcherService
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.messaging.DataFeed
import net.corda.core.node.services.Vault
import net.corda.core.node.services.trackBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.serialization.CordaSerializable
import net.corda.core.toFuture
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.minutes
import net.corda.core.utilities.unwrap
import net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows.ReissueCandyCoupons
import net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows.RequestCandyCouponReissuanceAndShareRequiredTransactions
import net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows.UnlockReissuedCandyCoupons
import net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows.parseStateReference
import rx.Observable

private inline fun <T> measureTimeMillis(block: () -> T): Pair<Long, T> {
    val start = System.currentTimeMillis()
    val result = block()
    return Pair(System.currentTimeMillis() - start, result)
}

@CordaSerializable
private class IssueCandyCouponsInfo(
    val couponHolderParty: Party,
    val couponCandies: Int
)

@InitiatingFlow
@StartableByRPC
class RequestIssueCandyCoupons(
    private val candyShopParty: Party,
    private val couponHolderParty: Party,
    private val couponCandies: Int
) : FlowLogic<SecureHash>() {
    companion object {
        private val TIMEOUT = 1.minutes
    }

    @Suspendable
    override fun call(): SecureHash {
        val session = initiateFlow(candyShopParty)

        val txId = session.sendAndReceive<SecureHash>(
            IssueCandyCouponsInfo(
                couponHolderParty,
                couponCandies
            )
        ).unwrap { data -> data }

        return serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
    }
}

@InitiatedBy(RequestIssueCandyCoupons::class)
class RequestIssueCandyCouponsResponder(private val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val issueCandyCouponsInfo = counterpartySession.receive<IssueCandyCouponsInfo>().unwrap { data -> data }

        val txId = subFlow(
            IssueCandyCoupons(
                issueCandyCouponsInfo.couponHolderParty,
                issueCandyCouponsInfo.couponCandies
            )
        )

        counterpartySession.send(txId)
    }
}

@InitiatingFlow
@StartableByRPC
class RequestReissueCandyCouponsAcceptance(
    private val candyShopParty: Party,
    private val reissuanceRequestRefString: String
) : FlowLogic<SecureHash>() {
    companion object {
        private val TIMEOUT = 1.minutes
    }

    @Suspendable
    override fun call(): SecureHash {
        val session = initiateFlow(candyShopParty)

        val txId = session.sendAndReceive<SecureHash>(reissuanceRequestRefString).unwrap { data -> data }

        return serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
    }
}

@InitiatedBy(RequestReissueCandyCouponsAcceptance::class)
class RequestReissueCandyCouponsAcceptanceResponder(private val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    companion object {
        private val TIMEOUT = 1.minutes
    }

    @Suspendable
    override fun call() {
        val reissuanceRequestRefString = counterpartySession.receive<String>().unwrap { data -> data }

        val tx = serviceHub.validatedTransactions.trackTransaction(
            parseStateReference(reissuanceRequestRefString).txhash
        ).getOrThrow(TIMEOUT)

        var tracker: DataFeed<Vault.Page<FungibleToken>, Vault.Update<FungibleToken>>? = serviceHub.vaultService.trackBy<FungibleToken>(
            criteria = QueryCriteria.VaultQueryCriteria(stateRefs = tx.tx.outputsOfType<ReissuanceRequest>().single().stateRefsToReissue)
        )

        if (tracker != null) {
            if (tracker.snapshot.states.isEmpty())
                tracker.updates.toFuture().getOrThrow(TIMEOUT)

            tracker = null
        }

        val txId = subFlow(
            ReissueCandyCoupons(
                reissuanceRequestRefString
            )
        )

        counterpartySession.send(txId)
    }
}

@StartableByRPC
class MeasureTransactionResolutionTime(
    private val candyShopParty: Party,
    private val couponCandies: Int,
    private val chainLength: Int,
    private val counterParty: Party,
    private val chainSnipping: Boolean
): FlowLogic<SecureHash>() {
    companion object {
        private val TIMEOUT = 1.minutes
    }

    @Suspendable
    override fun call(): SecureHash {
        val (issueTime, couponsTxId) = measureTimeMillis {
            val txId = subFlow(
                RequestIssueCandyCoupons(
                    candyShopParty,
                    ourIdentity,
                    couponCandies
                )
            )

            serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
        }
        logger.info("[Timings] Issue coupons time: $issueTime ms")

        val (exchangeTime, exchangeTxId) = measureTimeMillis {
            var txId = couponsTxId

            repeat(chainLength) { index ->
                logger.info("[Timings] Iteration ${index + 1} of $chainLength")

                val tx = serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT)

                txId = subFlow(
                    ExchangeCandyCoupons(
                        tx.tx.outRefsOfType<FungibleToken>().map { it.ref.toString() },
                        listOf(couponCandies)
                    )
                )
            }

            serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
        }
        logger.info("[Timings] Exchange coupons time: $exchangeTime ms")

        val (reissueTime, lastTxId) = if (chainSnipping) {
            val (requestReissuanceTime, requestReissuanceTxId) = measureTimeMillis {
                val tx = serviceHub.validatedTransactions.getTransaction(exchangeTxId)!!

                val txId = subFlow(
                    RequestCandyCouponReissuanceAndShareRequiredTransactions(
                        candyShopParty,
                        tx.tx.outRefsOfType<FungibleToken>().map { it.ref.toString() }
                    )
                )

                serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
            }
            logger.info("[Timings] Request coupons re-issuance time: $requestReissuanceTime ms")

            val (reissueAcceptanceTime, reissueAcceptanceTxId) = measureTimeMillis {
                val tx = serviceHub.validatedTransactions.getTransaction(requestReissuanceTxId)!!

                val txId = subFlow(
                    RequestReissueCandyCouponsAcceptance(
                        candyShopParty,
                        tx.tx.outRefsOfType<ReissuanceRequest>().single().ref.toString()
                    )
                )

                serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
            }
            logger.info("[Timings] Re-issue coupons time: $reissueAcceptanceTime ms")

            val (tearUpTime, tearUpTxId) = measureTimeMillis {
                val tx = serviceHub.validatedTransactions.getTransaction(exchangeTxId)!!

                val txId = subFlow(
                    TearUpCandyCoupons(
                        tx.tx.outRefsOfType<FungibleToken>().map { it.ref.toString() }
                    )
                )

                serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
            }
            logger.info("[Timings] Tear up coupons time: $tearUpTime ms")

            val (unlockTime, unlockTxId) = measureTimeMillis {
                val tx = serviceHub.validatedTransactions.getTransaction(reissueAcceptanceTxId)!!

                val txId = subFlow(
                    UnlockReissuedCandyCoupons(
                        tx.tx.outRefsOfType<FungibleToken>().map { it.ref.toString() },
                        tx.tx.outRefsOfType<ReissuanceLock<FungibleToken>>().single().ref.toString(),
                        listOf(tearUpTxId)
                    )
                )

                serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
            }
            logger.info("[Timings] Unlock re-issued coupons time: $unlockTime ms")

            val reissuanceTime = requestReissuanceTime + reissueAcceptanceTime + tearUpTime + unlockTime
            logger.info("[Timings] Re-issuance summary time: $reissuanceTime ms")

            Pair(reissuanceTime, unlockTxId)
        } else {
            Pair(0L, exchangeTxId)
        }

        val (giveTime, giveTxId) = measureTimeMillis {
            val tx = serviceHub.validatedTransactions.getTransaction(lastTxId)!!

            val txId = subFlow(
                GiveCandyCoupons(
                    tx.tx.outRefsOfType<FungibleToken>().map { it.ref.toString() },
                    counterParty
                )
            )

            serviceHub.validatedTransactions.trackTransaction(txId).getOrThrow(TIMEOUT).id
        }
        logger.info("[Timings] Give coupons time: $giveTime ms")

        val summaryTime = issueTime + exchangeTime + reissueTime + giveTime
        logger.info("[Timings] Summary time: $summaryTime ms")

        return giveTxId
    }
}