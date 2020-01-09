/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.game.console.commands;

import org.destinationsol.game.console.annotations.Command;
import org.destinationsol.game.console.annotations.CommandParam;
import org.destinationsol.game.console.annotations.RegisterCommands;

@RegisterCommands
public class TestCommand {
    @Command(shortDescription = "Sets the current world time for the local player in days")
    public String setWorldTime(@CommandParam("day") float day) {
        return "World time changed TO: " + day;
    }

    @Command()
    public String test(@CommandParam("time") int a) {
        return "Test indeed" + a;
    }

    @Command
    public String testmoze(@CommandParam("wtf") int b) {
        return "MOZE TEST " + b;
    }
}
