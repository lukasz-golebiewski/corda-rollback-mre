package com.example.scheduled

import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.SchedulableState
import net.corda.core.contracts.ScheduledActivity
import net.corda.core.contracts.StateRef
import net.corda.core.flows.FlowLogicRefFactory
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.time.Instant

@BelongsToContract(ScheduledContract::class)
data class ScheduledState(val requestor: Party, private val recipient: Party, val startAt: Instant, val counter: Int = 0) : SchedulableState {

    override val participants: List<AbstractParty> = listOf(requestor, recipient)

    override fun nextScheduledActivity(
        thisStateRef: StateRef,
        flowLogicRefFactory: FlowLogicRefFactory
    ): ScheduledActivity? =
        ScheduledActivity(
            flowLogicRefFactory.create("com.example.scheduled.ScheduledFlow"),
            startAt
        )

    fun increment() = copy(counter = counter + 1)
}