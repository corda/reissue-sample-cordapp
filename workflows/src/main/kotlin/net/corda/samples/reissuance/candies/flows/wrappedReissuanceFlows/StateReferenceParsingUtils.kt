package net.corda.samples.reissuance.candies.flows.wrappedReissuanceFlows

import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash

fun parseStateReference(stateRefStr: String): StateRef {
    val (secureHashStr, indexStr) = stateRefStr.dropLast(1).split("(")
    return StateRef(SecureHash.parse(secureHashStr), Integer.parseInt(indexStr))
}