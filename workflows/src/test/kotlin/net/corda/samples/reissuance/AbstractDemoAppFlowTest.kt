package net.corda.samples.reissuance

import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.dr.ledgergraph.services.LedgerGraphService
import com.r3.corda.lib.reissuance.flows.*
import com.r3.corda.lib.reissuance.states.ReIssuanceLock
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.getOrThrow
import net.corda.samples.reissuance.candies.flows.*
import net.corda.samples.reissuance.candies.flows.wrappedReIssuanceFlows.*
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.DUMMY_NOTARY_NAME
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.internal.*
import org.junit.After
import org.junit.Before


abstract class AbstractDemoAppFlowTest {

    lateinit var mockNet: InternalMockNetwork

    lateinit var notaryNode: TestStartedNode
    lateinit var bankNode: TestStartedNode
    lateinit var aliceNode: TestStartedNode
    lateinit var bobNode: TestStartedNode
    lateinit var charlieNode: TestStartedNode

    lateinit var notaryParty: Party
    lateinit var bankParty: Party
    lateinit var aliceParty: Party
    lateinit var bobParty: Party
    lateinit var charlieParty: Party

    lateinit var bankLegalName: CordaX500Name
    lateinit var aliceLegalName: CordaX500Name
    lateinit var bobLegalName: CordaX500Name
    lateinit var charlieLegalName: CordaX500Name

    lateinit var issuedTokenType: IssuedTokenType

    val demoAppTokenType = TokenType("CandyCoupon", 0)

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
                findCordapp("com.r3.dr.ledgergraph"),
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

        bankLegalName = CordaX500Name(organisation = "ISSUER", locality = "London", country = "GB")
        bankNode = mockNet.createNode(InternalMockNodeParameters(legalName = bankLegalName))
        bankParty = bankNode.info.singleIdentity()

        aliceLegalName = CordaX500Name(organisation = "ALICE", locality = "London", country = "GB")
        aliceNode = mockNet.createNode(InternalMockNodeParameters(legalName = aliceLegalName))
        aliceParty = aliceNode.info.singleIdentity()

        bobLegalName = CordaX500Name(organisation = "BOB", locality = "London", country = "GB")
        bobNode = mockNet.createNode(InternalMockNodeParameters(legalName = bobLegalName))
        bobParty = bobNode.info.singleIdentity()

        charlieLegalName = CordaX500Name(organisation = "CHARLIE", locality = "London", country = "GB")
        charlieNode = mockNet.createNode(InternalMockNodeParameters(legalName = charlieLegalName))
        charlieParty = charlieNode.info.singleIdentity()

        issuedTokenType = IssuedTokenType(bankParty, demoAppTokenType)

        aliceNode.services.cordaService(LedgerGraphService::class.java).waitForInitialization()

    }

    @After
    fun tearDown() {
        mockNet.stopNodes()
    }

    fun getHolderTokensQuantity(
        node: TestStartedNode
    ): Long {
        val availableTokens = listAvailableTokens(node)
        return getHolderTokensQuantity(availableTokens)
    }

    private fun getHolderTokensQuantity(
        availableTokens: List<StateAndRef<FungibleToken>>
    ): Long {
        return availableTokens.map { it.state.data.amount.quantity }.sum()
    }

    fun issueDemoAppTokens(
        holder: Party,
        tokenAmount: Long
    ): SecureHash {
        return runFlow(
            bankNode,
            IssueCandyCoupons(holder, tokenAmount)
        )
    }

    fun moveDemoAppTokens(
        node: TestStartedNode,
        tokenRefs: List<StateRef>,
        newHolder: Party
    ): SecureHash {
        return runFlow(
            node,
            GiveCandyCoupons(tokenRefs.map { it.toString() }, newHolder)
        )
    }

    fun redeemDemoAppTokens(
        node: TestStartedNode,
        tokenRefs: List<StateRef>
    ): SecureHash {
        return runFlow(
            node,
            ThrowAwayCandyCoupons(tokenRefs.map { it.toString() })
        )
    }

    fun listAvailableTokens(
        node: TestStartedNode,
        encumbered: Boolean? = null
    ): List<StateAndRef<FungibleToken>> {
        return runFlow(
            node,
            ListAvailableCandyCoupons(node.info.singleIdentity(), encumbered)
        )
    }

    fun createDemoAppTokenReIssuanceRequestAndShareRequiredTransactions(
        node: TestStartedNode,
        statesToReIssue: List<StateAndRef<FungibleToken>>,
        bank: AbstractParty
    ) {
        runFlow(
            node,
            RequestDemoAppTokensReIssuanceAndShareRequiredTransactions(bank, statesToReIssue.map { it.ref.toString() })
        )
    }

    fun reIssueRequestedStates(
        node: TestStartedNode,
        reIssuanceRequest: StateAndRef<ReIssuanceRequest>
    ) {
        runFlow(
            node,
            ReIssueDemoAppTokens(reIssuanceRequest.ref.toString())
        )
    }

    fun rejectReIssuanceRequested(
        node: TestStartedNode,
        reIssuanceRequest: StateAndRef<ReIssuanceRequest>
    ) {
        runFlow(
            node,
            RejectDemoAppTokensReIssuanceRequest(reIssuanceRequest.ref.toString())
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

    fun unlockReIssuedState(
        node: TestStartedNode,
        attachmentSecureHashes: List<SecureHash>,
        reIssuedStateAndRefs: List<StateAndRef<FungibleToken>>,
        lockStateAndRef: StateAndRef<ReIssuanceLock<FungibleToken>>
    ) {
        runFlow(
            node,
            UnlockReIssuedDemoAppStates(reIssuedStateAndRefs.map { it.ref.toString() }, lockStateAndRef.ref.toString(),
                attachmentSecureHashes)
        )
    }

    fun deleteReIssuedStatesAndLock(
        node: TestStartedNode,
        reIssuanceLock: StateAndRef<ReIssuanceLock<FungibleToken>>,
        reIssuedStates: List<StateAndRef<FungibleToken>>
        ) {
        runFlow(
            node,
            DeleteReIssuedDemoAppStatesAndLock(reIssuedStates.map { it.ref.toString() }, reIssuanceLock.ref.toString())
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
