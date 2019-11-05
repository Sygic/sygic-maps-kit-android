/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.uikit.viewmodels.common.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.sygic.maps.uikit.views.common.extensions.asMutable

open class KnightRiderServer {

    val commandFeed: LiveData<String> = MutableLiveData()
    var httpCallback: ((String) -> String) = { "No http callback set" }

    private val httpServer = AsyncHttpServer()
    private val webSocketServer = AsyncHttpServer()
    private val webSocketList = LinkedHashSet<WebSocket>()

    private var isHttpServerRunning = false
    private var isWebSocketServerRunning = false

    fun writeToWebSocket(data: String) = webSocketList.forEach { it.send(data) }

    fun startWebSocketServer(port: Int) {
        if (isWebSocketServerRunning) {
            return
        }

        webSocketServer.websocket(".*") { webSocket, _ ->
            with(webSocket) {
                setStringCallback { commandFeed.asMutable().postValue(it) }
                setClosedCallback { webSocketList.remove(this).also { send("Bye") } }
                webSocketList.add(this)

                send(
                    "Knight Rider. A shadowy flight into the dangerous world of a man " +
                            "who does not exist. Michael Knight: a young loner on a crusade to champion the " +
                            "cause of the innocent, the helpless, the powerless, in a world of criminals who " +
                            "operate above the law."
                )
                send("https://www.youtube.com/watch?v=oNyXYPhnUIs")
            }
        }
        webSocketServer.listen(port)
        isWebSocketServerRunning = true
    }

    fun startHttpServer(port: Int) {
        if (isHttpServerRunning) {
            return
        }

        httpServer.get(".*") { request, response -> response.send(httpCallback(request.path)) }
        httpServer.listen(port)
        isHttpServerRunning = true
    }

    fun stopHttpServer() {
        httpServer.stop()
        isHttpServerRunning = false
    }

    fun stopWebSocketServer() {
        webSocketServer.stop()
        isWebSocketServerRunning = false
    }
}