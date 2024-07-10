package com.example

import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

class NotificationContract : Contract {

    override fun verify(tx: LedgerTransaction) {
    }

}
