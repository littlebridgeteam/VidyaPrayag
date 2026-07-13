package com.littlebridge.enrollplus.core

import io.ktor.client.HttpClient
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

object HttpClientRegistry {
    private val log = LoggerFactory.getLogger("HttpClientRegistry")
    private val clients = CopyOnWriteArrayList<HttpClient>()

    fun register(client: HttpClient) {
        clients.add(client)
    }

    fun closeAll() {
        for (client in clients) {
            runCatching { client.close() }
                .onFailure { log.warn("Failed to close HttpClient: {}", it.message) }
        }
        clients.clear()
        log.info("All registered HttpClients closed")
    }
}
