package com.example

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.transactions.TransactionBuilder


@InitiatingFlow
open class SubFlow(val notificationState: NotificationState) : FlowLogic<Unit>() {

    @Suspendable
    override fun call(): Unit {
        logger.info("${this::class.simpleName} has started.")
        val initialSigners = setOf(ourIdentity.owningKey)

        val txBuilder = TransactionBuilder(notary = getNotary())
            .addOutputState(notificationState)

        txBuilder.verify(serviceHub)
        val tx = serviceHub.signInitialTransaction(txBuilder, initialSigners)

        subFlow(FinalityFlow(tx, emptyList()))
        logger.info("${this::class.simpleName} has finished.")
    }

    fun getNotary() = serviceHub.networkMapCache.notaryIdentities
        .firstOrNull() ?: throw FlowException("No available notary.")
}