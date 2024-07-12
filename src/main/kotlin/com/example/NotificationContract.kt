package com.example

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

class NotificationContract : Contract {

    sealed class Commands : CommandData {
        object Create : Commands()
    }

    override fun verify(tx: LedgerTransaction) {
    }

}
