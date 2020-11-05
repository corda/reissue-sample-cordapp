package net.corda.samples.reissuance

import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.reissuance.flows.*
import com.r3.corda.lib.reissuance.states.ReissuanceLock
import com.r3.corda.lib.reissuance.states.ReissuanceRequest
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.getOrThrow
import net.corda.samples.reissuance.candies.flows.*
import net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows.*
import net.corda.samples.reissuance.candies.states.Candy
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.DUMMY_NOTARY_NAME
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.internal.*
import org.junit.After
import org.junit.Before


abstract class AbstractCandyFlowTest {

    lateinit var mockNet: InternalMockNetwork

    lateinit var notaryNode: TestStartedNode
    lateinit var candyShopNode: TestStartedNode
    lateinit var aliceNode: TestStartedNode
    lateinit var bobNode: TestStartedNode

    lateinit var notaryParty: Party
    lateinit var candyShopParty: Party
    lateinit var aliceParty: Party
    lateinit var bobParty: Party

    lateinit var candyShopLegalName: CordaX500Name
    lateinit var aliceLegalName: CordaX500Name
    lateinit var bobLegalName: CordaX500Name

    val candyCouponTokenType = TokenType("CandyCoupon", 0)
    lateinit var issuedCandyCouponTokenType: IssuedTokenType

    @Before
    fun setup() {
        mockNet = InternalMockNetwork(
            cordappsForAllNodes = listOf(
                findCordapp("com.r3.corda.lib.tokens.contracts"),
                findCordapp("com.r3.corda.lib.tokens.workflows"),
                findCordapp("com.r3.corda.lib.tokens.money"),
                findCordapp("com.r3.corda.lib.tokens.selection"),
                findCordapp("com.r3.corda.lib.accounts.contracts"),
                findCordapp("com.r3.corda.lib.accounts.workflows"),
                findCordapp("com.r3.corda.lib.ci.workflows"),
                findCordapp("com.r3.corda.lib.reissuance.flows"),
                findCordapp("com.r3.corda.lib.reissuance.contracts"),
                findCordapp("net.corda.samples.reissuance.candies.contracts"),
                findCordapp("net.corda.samples.reissuance.candies.flows")
            ),
            notarySpecs = listOf(MockNetworkNotarySpec(DUMMY_NOTARY_NAME, false)),
            initialNetworkParameters = testNetworkParameters(
                minimumPlatformVersion = 4
            )
        )

        notaryNode = mockNet.notaryNodes.first()
        notaryParty = notaryNode.info.singleIdentity()

        candyShopLegalName = CordaX500Name(organisation = "ISSUER", locality = "London", country = "GB")
        candyShopNode = mockNet.createNode(InternalMockNodeParameters(legalName = candyShopLegalName))
        candyShopParty = candyShopNode.info.singleIdentity()

        aliceLegalName = CordaX500Name(organisation = "ALICE", locality = "London", country = "GB")
        aliceNode = mockNet.createNode(InternalMockNodeParameters(legalName = aliceLegalName))
        aliceParty = aliceNode.info.singleIdentity()

        bobLegalName = CordaX500Name(organisation = "BOB", locality = "London", country = "GB")
        bobNode = mockNet.createNode(InternalMockNodeParameters(legalName = bobLegalName))
        bobParty = bobNode.info.singleIdentity()

        issuedCandyCouponTokenType = IssuedTokenType(candyShopParty, candyCouponTokenType)
    }

    @After
    fun tearDown() {
        mockNet.stopNodes()
    }

    fun issueCandyCoupons(
        holder: Party,
        tokenAmount: Int
    ): SecureHash {
        return runFlow(
            candyShopNode,
            IssueCandyCoupons(holder, tokenAmount)
        )
    }

    fun exchangeCandyCoupons(
        node: TestStartedNode,
        candyCouponRefs: List<StateRef>,
        newCouponCandies: List<Int>
    ): SecureHash {
        return runFlow(
            node,
            ExchangeCandyCoupons(candyCouponRefs.map { it.toString() }, newCouponCandies)
        )
    }

    fun giveCandyCoupons(
        node: TestStartedNode,
        candyCouponRefs: List<StateRef>,
        newHolder: Party
    ): SecureHash {
        return runFlow(
            node,
            GiveCandyCoupons(candyCouponRefs.map { it.toString() }, newHolder)
        )
    }

    fun tearUpCandyCoupons(
        node: TestStartedNode,
        candyCouponRefs: List<StateRef>
    ): SecureHash {
        return runFlow(
            node,
            TearUpCandyCoupons(candyCouponRefs.map { it.toString() })
        )
    }

    fun buyCandiesUsingCoupons(
        node: TestStartedNode,
        candyCouponRefs: List<StateRef>
    ): SecureHash {
        return runFlow(
            node,
            BuyCandiesUsingCoupons(candyCouponRefs.map { it.toString() })
        )
    }

    fun listAvailableCandyCoupons(
        node: TestStartedNode,
        encumbered: Boolean? = null
    ): List<StateAndRef<FungibleToken>> {
        return runFlow(
            node,
            ListCandyCoupons(node.info.singleIdentity(), encumbered)
        )
    }

    fun listAvailableCandies(
        node: TestStartedNode
    ): List<StateAndRef<Candy>> {
        return runFlow(
            node,
            ListCandies()
        )
    }

    fun createCandyCouponReissuanceRequestAndShareRequiredTransactions(
        node: TestStartedNode,
        statesToReissue: List<StateAndRef<FungibleToken>>,
        bank: AbstractParty
    ) {
        runFlow(
            node,
            RequestCandyCouponReissuanceAndShareRequiredTransactions(bank, statesToReissue.map { it.ref.toString() })
        )
    }

    fun reissueRequestedStates(
        node: TestStartedNode,
        reissuanceRequest: StateAndRef<ReissuanceRequest>
    ) {
        runFlow(
            node,
            ReissueCandyCoupons(reissuanceRequest.ref.toString())
        )
    }

    fun rejectReissuanceRequested(
        node: TestStartedNode,
        reissuanceRequest: StateAndRef<ReissuanceRequest>
    ) {
        runFlow(
            node,
            RejectCandyCouponsReissuanceRequest(reissuanceRequest.ref.toString())
        )
    }

    fun uploadDeletedStateAttachment(
        node: TestStartedNode,
        deleteStateTransactionId: SecureHash
    ): SecureHash {
        return runFlow(
            node,
            UploadTransactionAsAttachment(deleteStateTransactionId)
        )
    }

    fun unlockReissuedState(
        node: TestStartedNode,
        attachmentSecureHashes: List<SecureHash>,
        reissuedStateAndRefs: List<StateAndRef<FungibleToken>>,
        lockStateAndRef: StateAndRef<ReissuanceLock<FungibleToken>>
    ) {
        runFlow(
            node,
            UnlockReissuedCandyCoupons(reissuedStateAndRefs.map { it.ref.toString() }, lockStateAndRef.ref.toString(),
                attachmentSecureHashes)
        )
    }

    fun deleteReissuedStatesAndLock(
        node: TestStartedNode,
        reissuanceLock: StateAndRef<ReissuanceLock<FungibleToken>>,
        reissuedStates: List<StateAndRef<FungibleToken>>
        ) {
        runFlow(
            node,
            DeleteReissuedCandyCouponsAndCorrespondingLock(reissuedStates.map { it.ref.toString() }, reissuanceLock.ref.toString())
        )
    }

    fun getTransactionBackChain(
        node: TestStartedNode,
        txId: SecureHash
    ): Set<SecureHash> {
        return runFlow(
            node,
            GetTransactionBackChain(txId)
        )
    }

    fun <T> runFlow(
        node: TestStartedNode,
        flowLogic: FlowLogic<T>
    ): T {
        val flowFuture = node.services.startFlow(flowLogic).resultFuture
        mockNet.runNetwork()
        return flowFuture.getOrThrow()
    }

}
