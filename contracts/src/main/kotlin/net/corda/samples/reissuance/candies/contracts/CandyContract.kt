package net.corda.samples.reissuance.candies.contracts

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

        requireThat {
            "No inputs of type Candy are allowed" using candyInputs.isEmpty()
            "At least one output of type Candy is expected" using candyOutputs.isNotEmpty()
        }
    }

    interface Commands : CommandData {
        class Buy : Commands
    }
}
