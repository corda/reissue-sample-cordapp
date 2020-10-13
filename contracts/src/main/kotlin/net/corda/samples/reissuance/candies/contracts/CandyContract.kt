package net.corda.samples.reissuance.candies.contracts

import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import net.corda.samples.reissuance.candies.states.Candy

class CandyContract: Contract {

    companion object {
        val contractId = this::class.java.enclosingClass.canonicalName
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Buy -> verifyBuyCommand(tx, command)
            else -> throw IllegalArgumentException("Command ${command.value} not supported")
        }
    }

    fun verifyBuyCommand(
        tx: LedgerTransaction,
        command: CommandWithParties<Commands>
    ) {
        val candyInputs = tx.inputsOfType<Candy>()
        val candyOutputs = tx.outputsOfType<Candy>()

        val candyCouponInputs = tx.inputsOfType<FungibleToken>().filter { it.tokenType == TokenType("CandyCoupon", 0) }
        val candyCouponOutputs = tx.outputsOfType<FungibleToken>().filter { it.tokenType == TokenType("CandyCoupon", 0) }

        requireThat {
            "No inputs of type Candy are allowed" using candyInputs.isEmpty()
            "At least one output of type Candy is expected" using candyOutputs.isNotEmpty()

            "At least one input of type CandyCoupon is expected" using candyCouponInputs.isNotEmpty()
            "No outputs of type CandyCoupon are allowed" using candyCouponOutputs.isEmpty()

            val couponCandies = candyCouponInputs.sumBy { it.amount.quantity.toInt() }
            val boughtCandies = candyOutputs.size
            "Number of coupon candies and bought candies must be equal" using (couponCandies == boughtCandies)

            "All coupons need to be issued by the same shop" using (candyCouponInputs.map { it.issuer }.toSet().size == 1)
            "All coupons need to have the same holder" using (candyCouponInputs.map { it.holder }.toSet().size == 1)
            "All candies need to have the same owner" using (candyOutputs.map { it.owner }.toSet().size == 1)
            "Coupon holders must be equal to candy owner" using (candyCouponInputs[0].holder == candyOutputs[0].owner)

            "Candy owner is required signer" using (command.signers.contains(candyOutputs[0].owner.owningKey))
            "Candy shop is required signer" using (command.signers.contains(candyCouponInputs[0].issuer.owningKey))
        }
    }

    interface Commands : CommandData {
        class Buy : Commands
    }
}
