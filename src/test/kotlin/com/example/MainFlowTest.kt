package com.example

import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.queryBy
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestClock
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals

class MainFlowTest {

    @Test
    fun `When exception is thrown, tx created by the subFlow should be visible`() {
        val mockNetworkParameters = MockNetworkParameters(
            cordappsForAllNodes = listOf()
        ).withNetworkParameters(testNetworkParameters(minimumPlatformVersion = 6))

        val packageName = "com.example"
        val mockNetwork = MockNetwork(listOf(packageName), mockNetworkParameters)
        ( mockNetwork.defaultNotaryNode .services.clock as TestClock )

        val cordaName = CordaX500Name("Corda", "London", "GB")
        val node = mockNetwork.createNode(cordaName)


        try {
            mockNetwork.startNodes()

            // start flow
            assertThrows<ExecutionException> {
                val cordaFuture = node.startFlow(MainFlow())
                mockNetwork.runNetwork()
                cordaFuture.get()
            }

            val notificationRefs = node.services.vaultService.queryBy<NotificationState>().states
            assertEquals(1, notificationRefs.size)

        } finally {
            mockNetwork.stopNodes()
        }
    }

}
