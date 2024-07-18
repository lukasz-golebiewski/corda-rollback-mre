package com.example.scheduled

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.transactions.SignedTransaction

@InitiatedBy(MainFlow::class)
class ReceiveMainFlow(val flowSession: FlowSession): FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        logger.info("${this::class.simpleName} has started.")

        subFlow(ReceiveFinalityFlow(flowSession))

        logger.info("${this::class.simpleName} has  finished.")
    }
    /*
    open val signTransactionFlow = object : SignTransactionFlow(flowSession) {
        @Suspendable
        override fun checkTransaction(stx: SignedTransaction) {
        }
    }

    @Suspendable
    override fun call(): SignedTransaction {
        val txId = subFlow(signTransactionFlow).id

        val signedTransaction = subFlow(
            ReceiveFinalityFlow(
                otherSideSession = flowSession,
                expectedTxId = txId
            )
        )
        return signedTransaction

    }
    *
     */
}