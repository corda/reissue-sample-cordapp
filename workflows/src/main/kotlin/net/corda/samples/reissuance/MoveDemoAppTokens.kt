package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.contracts.commands.MoveTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.workflows.utilities.getPreferredNotary
import net.corda.core.contracts.Amount
import net.corda.core.contracts.StateAndRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class MoveDemoAppTokens(
    private val issuer: Party,
    private val newTokenHolderParty: Party,
    private val tokenAmount: Long
) : FlowLogic<SecureHash>() {

    @Suspendable
    override fun call(): SecureHash {
        val demoAppTokenType = TokenType("DemoAppToken", 0)

        val holderParty: Party = ourIdentity

        val signers = listOf(
            holderParty.owningKey
        )

        val availableTokens = subFlow(ListAvailableDemoAppTokens(holderParty))

        val issuedTokenType = IssuedTokenType(issuer, demoAppTokenType)
        val (tokensToTransfer, change) = splitTokensIntoTokensToTransferAndChange(
            availableTokens, tokenAmount, issuedTokenType, holderParty, newTokenHolderParty
        )

        val transactionBuilder = TransactionBuilder(notary = getPreferredNotary(serviceHub))
        availableTokens.forEach { transactionBuilder.addInputState(it) }
        transactionBuilder.addOutputState(tokensToTransfer)
        if(change != null)
            transactionBuilder.addOutputState(change)

        transactionBuilder.addCommand(MoveTokenCommand(
            issuedTokenType,
            inputs = availableTokens.indices.toList(),
            outputs = if(change == null) listOf(0) else listOf(0, 1)
        ), signers)

        transactionBuilder.verify(serviceHub)
        val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)

        val sessions = if(newTokenHolderParty != ourIdentity) listOf(initiateFlow(newTokenHolderParty)) else listOf()
        return subFlow(
            FinalityFlow(
                transaction = signedTransaction,
                sessions = sessions
            )
        ).id
    }

    private fun splitTokensIntoTokensToTransferAndChange(
        inputs: List<StateAndRef<FungibleToken>>,
        tokensToTransfer: Long,
        issuedTokenType: IssuedTokenType,
        holderParty: Party,
        newHolderParty: Party
    ): Pair<FungibleToken, FungibleToken?> {
        val availableTokenQuantity = inputs.map { it.state.data.amount.quantity }.sum()
        require(availableTokenQuantity >= tokensToTransfer) {
            "Insufficient tokens. Required $tokensToTransfer, but have $availableTokenQuantity" }

        val tokenToTransfer = FungibleToken(Amount(tokensToTransfer, issuedTokenType), newHolderParty)

        val changeToken = if (availableTokenQuantity == tokensToTransfer) null
        else {
            val changeAmount = Amount((availableTokenQuantity - tokensToTransfer), issuedTokenType)
            FungibleToken(changeAmount, holderParty)
        }
        return Pair(tokenToTransfer, changeToken)
    }

}


@InitiatedBy(MoveDemoAppTokens::class)
class MoveDemoAppTokensResponder(
    private val otherSession: FlowSession
) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(
            ReceiveFinalityFlow(
                otherSession,
                statesToRecord = StatesToRecord.ALL_VISIBLE
            )
        )
    }
}
