package com.example

import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party

@BelongsToContract(NotificationContract::class)
class NotificationState(val owner: Party, val payload: String) : ContractState{

    override val participants: List<Party> get() = listOf(owner)

}