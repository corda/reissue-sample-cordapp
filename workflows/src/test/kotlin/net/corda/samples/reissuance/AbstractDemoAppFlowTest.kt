package net.corda.samples.reissuance

import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.dr.ledgergraph.services.LedgerGraphService
import com.r3.corda.lib.reissuance.flows.*
import com.r3.corda.lib.reissuance.states.ReIssuanceLock
import com.r3.corda.lib.reissuance.states.ReIssuanceRequest
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.core.DUMMY_NOTARY_NAME
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.internal.*
import org.junit.After
import org.junit.Before
import java.util.*


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

    val demoAppTokenType = TokenType("DemoAppToken", 0)

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
                findCordapp("net.corda.samples.reissuance")
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

    fun getHolderTokensQuantity(
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
            IssueDemoAppTokens(demoAppTokenType, holder, tokenAmount)
        )
    }

    fun moveDemoAppTokens(
        node: TestStartedNode,
        newHolder: Party,
        tokenAmount: Long
    ): SecureHash {
        return runFlow(
            node,
            MoveDemoAppTokens(demoAppTokenType, bankParty, newHolder, tokenAmount)
        )
    }

    fun redeemDemoAppTokens(
        node: TestStartedNode,
        encumbered: Boolean? = false,
        tokenAmount: Long? = null,
        tokenRefs: List<StateRef> = listOf()
    ): SecureHash {
        return runFlow(
            node,
            RedeemDemoAppTokens(demoAppTokenType, bankParty, encumbered, tokenAmount, tokenRefs)
        )
    }

    fun listAvailableTokens(
        node: TestStartedNode,
        encumbered: Boolean? = null
    ): List<StateAndRef<FungibleToken>> {
        return runFlow(
            node,
            ListAvailableTokens(demoAppTokenType, node.info.singleIdentity(), encumbered)
        )
    }

    inline fun <reified T : ContractState> getStateAndRefs(
        node: TestStartedNode,
        encumbered: Boolean? = null,
        accountUUID: UUID? = null
    ): List<StateAndRef<T>> {
        val states = if(accountUUID == null)
            node.services.vaultService.queryBy<T>().states
        else
            node.services.vaultService.queryBy<T>(
                criteria = QueryCriteria.VaultQueryCriteria().withExternalIds(listOf(accountUUID))
            ).states
        if(encumbered == null)
            return states
        return filterStates(states, encumbered)
    }

    inline fun <reified T : ContractState> filterStates(
        states: List<StateAndRef<T>>,
        encumbered: Boolean
    ): List<StateAndRef<T>> {
        if(encumbered)
            return states.filter { it.state.encumbrance != null }
        return states.filter { it.state.encumbrance == null }
    }

    fun <T> createReIssuanceRequestAndShareRequiredTransactions(
        node: TestStartedNode,
        statesToReIssue: List<StateAndRef<T>>,
        command: CommandData,
        bank: AbstractParty,
        commandSigners: List<AbstractParty> = listOf(),
        requester: AbstractParty? = null
    ) where T: ContractState {
        runFlow(
            node,
            RequestReIssuanceAndShareRequiredTransactions<T>(bank, statesToReIssue, command, commandSigners,
                requester)
        )
    }

    fun unlockReIssuedState(
        node: TestStartedNode,
        attachmentSecureHashes: List<SecureHash>,
        command: CommandData,
        commandSigners: List<AbstractParty>? = null,
        reIssuedStateAndRefs: List<StateAndRef<FungibleToken>> = getStateAndRefs<FungibleToken>(node, true),
        lockStateAndRef: StateAndRef<ReIssuanceLock<FungibleToken>> = getStateAndRefs<ReIssuanceLock<FungibleToken>>(node, encumbered = true)[0]
    ) {
        val signers: List<AbstractParty> = commandSigners ?: listOf(lockStateAndRef.state.data.requester)
        runFlow(
            node,
            UnlockReIssuedStates(reIssuedStateAndRefs, lockStateAndRef, attachmentSecureHashes, command, signers)
        )
    }

    fun reIssueRequestedStates(
        node: TestStartedNode,
        reIssuanceRequest: StateAndRef<ReIssuanceRequest>,
        bankIsRequiredExitCommandSigner: Boolean
        ) {
        runFlow(
            node,
            ReIssueStates<FungibleToken>(reIssuanceRequest, bankIsRequiredExitCommandSigner)
        )
    }

    fun rejectReIssuanceRequested(
        node: TestStartedNode,
        reIssuanceRequest: StateAndRef<ReIssuanceRequest>
    ) {
        runFlow(
            node,
            RejectReIssuanceRequest<FungibleToken>(reIssuanceRequest)
        )
    }

    fun uploadDeletedStateAttachment(
        node: TestStartedNode,
        deleteStateTransactionId: SecureHash
    ): SecureHash {
        val party = node.info.singleIdentity()

        mockNet.runNetwork()
        val transactionByteArray = runFlow(
            node,
            GenerateTransactionByteArray(deleteStateTransactionId)
        )

        return node.services.attachments.importAttachment(transactionByteArray.inputStream(), party.toString(), null)
    }

    fun deleteReIssuedStatesAndLock(
        node: TestStartedNode,
        reIssuanceLock: StateAndRef<ReIssuanceLock<FungibleToken>>,
        reIssuedStates: List<StateAndRef<FungibleToken>>,
        command: CommandData,
        commandSigners: List<AbstractParty>? = null
        ) {
        val signers: List<AbstractParty> = commandSigners ?: listOf(reIssuanceLock.state.data.requester,
            reIssuanceLock.state.data.issuer)
        runFlow(
            node,
            DeleteReIssuedStatesAndLock(reIssuanceLock, reIssuedStates, command, signers)
        )
    }


    fun getSignedTransactions(
        node: TestStartedNode
    ): List<SignedTransaction> {
        return node.services.validatedTransactions.track().snapshot
    }

    fun getLedgerTransactions(
        node: TestStartedNode
    ): List<LedgerTransaction> {
        return getSignedTransactions(node).map {
            it.toLedgerTransaction(node.services)
        }
    }

    fun getTransactionBackChain(
        node: TestStartedNode,
        txId: SecureHash
    ): Set<SecureHash> {
        val ledgerGraphService = node.services.cordaService(LedgerGraphService::class.java)
        return ledgerGraphService.getBackchain(setOf(txId))
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
