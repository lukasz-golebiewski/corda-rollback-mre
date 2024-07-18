package com.example.scheduled

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@InitiatingFlow
class MainFlow(private val recipient: Party, private val startAt: Instant) : FlowLogic<Unit>(){

    @Suspendable
    override fun call() {
        logger.info("${this::class.simpleName} has started.")
        val notary = serviceHub.networkMapCache.notaryIdentities
            .firstOrNull() ?: throw FlowException("No available notary.")

        val scheduledState = ScheduledState(ourIdentity, recipient,startAt)

        val txBuilder = TransactionBuilder(notary).addCommand(ScheduledContract.Commands.Create, ourIdentity.owningKey).addOutputState(scheduledState)
        txBuilder.verify(serviceHub)

        val initialSigners = setOf(ourIdentity.owningKey)
        val tx = serviceHub.signInitialTransaction(txBuilder, initialSigners)

        val flowSessions =  setOf ( initiateFlow(recipient) )
        subFlow(FinalityFlow(tx, flowSessions))

        logger.info("${this::class.simpleName} has finished.")
    }
}