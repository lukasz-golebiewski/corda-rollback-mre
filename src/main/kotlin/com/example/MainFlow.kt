package com.example

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC

@InitiatingFlow
@StartableByRPC
open class MainFlow() : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        logger.info("${this::class.simpleName} has started.")
        val notificationState = NotificationState(owner = ourIdentity, payload = "Hello, World!")
        subFlow(SubFlow(notificationState))
        throw FlowException("This is a test exception.")
        //logger.info("${this::class.simpleName} has finished.")
    }
}
