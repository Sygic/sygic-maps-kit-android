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

import com.sygic.sdk.low.LowHttp
import com.sygic.sdk.low.remote.RemoteControl

object RemoteControlManager : RemoteControl {

    private val knightRiderServer = KnightRiderServer().also {
        it.httpCallback = { request -> LowHttp.getResponseForRequest(request) }
        it.commandFeed.observeForever { command -> LowHttp.sendRemoteCommand(command) }
    }

    override fun startHttpServer(port: Int) = knightRiderServer.startHttpServer(port)
    override fun startWebsocketServer(port: Int) = knightRiderServer.startWebSocketServer(port)
    override fun stopHttpServer() = knightRiderServer.stopHttpServer()
    override fun stopWebsocketServer() = knightRiderServer.stopWebSocketServer()
    override fun writeToWebSocket(data: String) = knightRiderServer.writeToWebSocket(data)
}