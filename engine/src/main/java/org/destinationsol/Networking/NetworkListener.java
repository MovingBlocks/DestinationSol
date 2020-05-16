/*
 * Copyright 2020 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.Networking;

import org.destinationsol.game.WorldConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener extends Thread {

    private final WorldConfig worldConfig;

    public NetworkListener(WorldConfig worldConfig) {
        this.worldConfig = worldConfig;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                PrintWriter outgoing = new PrintWriter(socket.getOutputStream(), true);
                outgoing.print(worldConfig.getSeed());
                outgoing.print(worldConfig.getNumberOfSystems());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
