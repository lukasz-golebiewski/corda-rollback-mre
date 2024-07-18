package com.example.scheduled

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.SchedulableFlow
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@SchedulableFlow
class ScheduledFlow : FlowLogic<Unit>(){

    @Suspendable
    override fun call() {
        logger.info("${this::class.simpleName} has started.")

        val state = serviceHub.vaultService.queryBy(ScheduledState::class.java).states.single()

        if (!serviceHub.myInfo.isLegalIdentity(state.state.data.requestor)) {
            logger.info("Skipping execution of ${this::class.simpleName}: not a requestor.")
            return
        }

        val updatedState = state.state.data.increment()
        val txBuilder = TransactionBuilder(getNotary()).addInputState(state)
            .addCommand(ScheduledContract.Commands.Update, ourIdentity.owningKey)
            .addOutputState(updatedState)

        txBuilder.verify(serviceHub)
        val ptx = serviceHub.signInitialTransaction(txBuilder)
        val participants = state.state.data.participants
        val flowSessions = (participants - ourIdentity).map { initiateFlow(it) }
        logger.info("${this::class.simpleName} is collecting signatures.")
        val stx = subFlow(CollectSignaturesFlow(ptx, flowSessions))
        logger.info("${this::class.simpleName} is finalizing.")
        subFlow(FinalityFlow(stx, flowSessions))
        logger.info("${this::class.simpleName} has finished.")
    }

    @Suspendable
    private fun getNotary(): Party =
        serviceHub.networkMapCache.notaryIdentities
            .firstOrNull() ?: throw FlowException("No available notary.")

}