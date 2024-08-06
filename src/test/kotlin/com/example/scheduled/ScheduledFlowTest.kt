package com.example.scheduled

import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.queryBy
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestClock
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals

class ScheduledFlowTest {

    @Test
    fun `State update by a scheduled flow should be visible`() {
        val mockNetworkParameters = MockNetworkParameters(
            cordappsForAllNodes = listOf()
        ).withNetworkParameters(testNetworkParameters(minimumPlatformVersion = 6))

        val packageName = "com.example.scheduled"
        val mockNetwork = MockNetwork(listOf(packageName), mockNetworkParameters)
        ( mockNetwork.defaultNotaryNode .services.clock as TestClock)

        val cordaName = CordaX500Name("Corda", "London", "GB")
        val cordaName2 = CordaX500Name("Corda2", "London", "GB")
        val node1 = mockNetwork.createNode(cordaName)
        val node2 = mockNetwork.createNode(cordaName2)


        try {
            mockNetwork.startNodes()

            val triggerAt = java.time.Instant.now().plusSeconds(100)

            // start flow

            val cordaFuture = node1.startFlow(MainFlow(node2.info.legalIdentities.first(), triggerAt))
            mockNetwork.runNetwork()
            cordaFuture.get()

            scheduleFlow(mockNetwork, triggerAt, node1, node2)
            // triggerManually(node1, mockNetwork)

            mockNetwork.waitQuiescent()

            val stateRefs1 = node1.services.vaultService.queryBy<ScheduledState>().states
            assertEquals(1, stateRefs1.size)
            assertEquals(1, stateRefs1.first().state.data.counter)

            val stateRefs2 = node2.services.vaultService.queryBy<ScheduledState>().states
            assertEquals(1, stateRefs2.size)
            assertEquals(1, stateRefs2.first().state.data.counter)

        } finally {
            mockNetwork.stopNodes()
        }
    }

    private fun triggerManually(node1: StartedMockNode, mockNetwork: MockNetwork) {
        val cordaFuture2 = node1.startFlow(ScheduledFlow())
        mockNetwork.runNetwork()
        cordaFuture2.get()
    }

    private fun scheduleFlow(
        mockNetwork: MockNetwork,
        triggerAt: Instant,
        node1: StartedMockNode,
        node2: StartedMockNode
    ) {
        (mockNetwork.defaultNotaryNode
            .services.clock as TestClock
                ).setTo(triggerAt)

        (listOf(node1, node2)).forEach { cordaNode ->
            (cordaNode.services.clock as TestClock).setTo(triggerAt)
        }

        mockNetwork.runNetwork()
    }
}