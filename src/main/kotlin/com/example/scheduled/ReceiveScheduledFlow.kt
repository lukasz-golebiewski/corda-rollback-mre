package com.example.scheduled

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.ReceiveFinalityFlow

@InitiatedBy(ScheduledFlow::class)
class ReceiveScheduledFlow (private val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        logger.info("${this::class.simpleName} has started.")

        subFlow(ReceiveFinalityFlow(counterpartySession))

        logger.info("${this::class.simpleName} has  finished.")
    }
}