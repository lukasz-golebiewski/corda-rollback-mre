package com.example

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.TimeWindow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.NotaryError
import net.corda.core.flows.NotaryException
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

@InitiatingFlow
@StartableByRPC
open class MainFlow() : FlowLogic<Unit>() {

    @Suspendable
    override fun call(): Unit {
        logger.info("${this::class.simpleName} has started.")
        val notificationState = NotificationState(owner = ourIdentity, payload = "Hello, World!")
        subFlow(SubFlow(notificationState))
        throw FlowException("This is a test exception.")
        logger.info("${this::class.simpleName} has finished.")
    }
}
