package com.example.scheduled

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

class ScheduledContract : Contract {

    sealed class Commands : CommandData {
        object Create : Commands()
        object Update : Commands()
    }

    override fun verify(tx: LedgerTransaction) {
    }
}