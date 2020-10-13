package net.corda.samples.reissuance.candies.states

import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.samples.reissuance.candies.contracts.CandyContract

@BelongsToContract(CandyContract::class)
data class Candy(
    val owner: AbstractParty
): ContractState {
    override val participants: List<AbstractParty>
        get() = listOf(owner)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Candy

        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        return owner.hashCode()
    }

}
