package net.corda.samples.reissuance

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.reissuance.flows.DeleteReIssuedStatesAndLock
import com.r3.corda.lib.reissuance.flows.UnlockReIssuedStates
import com.r3.corda.lib.reissuance.states.ReIssuanceLock
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import com.r3.corda.lib.tokens.contracts.commands.MoveTokenCommand
import com.r3.corda.lib.tokens.contracts.commands.RedeemTokenCommand
import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.StateAndRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party

// Note: There is no need to generate a separate flow calling DeleteReIssuedStatesAndLock.
// The flow has been created to make it easier to use node shell.

@InitiatingFlow
@StartableByRPC
class DeleteReIssuedDemoAppStatesAndLock(
    private val reIssuedStateAndRefs: List<StateAndRef<FungibleToken>>,
    private val reIssuanceLock: StateAndRef<ReIssuanceLock<FungibleToken>>
): FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val issuer = reIssuanceLock.state.data.issuer
        val demoAppTokenType = TokenType("DemoAppToken", 0)
        val issuedTokenType = IssuedTokenType(issuer as Party, demoAppTokenType)

        val reIssuedStates = reIssuanceLock.state.data.originalStates

        subFlow(DeleteReIssuedStatesAndLock(
            reIssuanceLock,
            reIssuedStateAndRefs,
            RedeemTokenCommand(issuedTokenType, reIssuedStates.indices.toList(), listOf())
        ))
    }

}
