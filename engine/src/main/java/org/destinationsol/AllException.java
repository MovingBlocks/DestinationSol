/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * owner: Suraj Datta 2018
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.terasology.assets.ResourceUrn;

import java.util.Set;

public class AllException extends RuntimeException {

    public AllException(String message) {

        super(message);

    }

   /* private ListNode head; // head node to contain all the list

    private static class ListNode {

        private String data;
        private ListNode next;

        public ListNode(String data) {

            this.data = data;
            this.next = null;
        }

    }

    /*public static void main (String args []){

        ListNode head = new ListNode()
    }*/



}
