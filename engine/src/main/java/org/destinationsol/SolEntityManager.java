/*
 * Copyright 2018 MovingBlocks
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
package org.destinationsol;

import com.google.common.base.Preconditions;
import org.terasology.entitysystem.component.CodeGenComponentManager;
import org.terasology.entitysystem.core.Component;
import org.terasology.entitysystem.core.EntityRef;
import org.terasology.entitysystem.entity.inmemory.InMemoryEntityManager;
import org.terasology.entitysystem.transaction.TransactionManager;
import org.terasology.valuetype.ImmutableCopy;
import org.terasology.valuetype.TypeHandler;
import org.terasology.valuetype.TypeLibrary;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SolEntityManager {

    private static TransactionManager transactionManager;
    private static InMemoryEntityManager entityManager;
    private static boolean isTransaction;

    static void setup(TransactionManager transactionManager) {
        final TypeLibrary typeLibrary = new TypeLibrary();
        typeLibrary.addHandler(new TypeHandler<>(Integer.class, ImmutableCopy.create()));
        SolEntityManager.transactionManager = transactionManager;
        final CodeGenComponentManager library = new CodeGenComponentManager(typeLibrary);
        entityManager = new InMemoryEntityManager(library, transactionManager);
        isTransaction = false;
    }

    static void begin() {
        transactionManager.begin();
        isTransaction = true;
    }

    static void end() {
        transactionManager.commit();
        isTransaction = false;
    }

    @SafeVarargs
    public static EntityRef createEntity(Class<? extends Component>... components) {
        Preconditions.checkArgument(isTransaction, "Accessing entityManager outside of gameLoop");
        final long id = entityManager.createEntity().getId();
        for (Class<? extends Component> component : components) {
            entityManager.addComponent(id, component);
        }
        return entityManager.getEntity(id);
    }

    public static List<EntityRef> getEntitiesWith(Class<? extends Component> components) {
        Preconditions.checkArgument(isTransaction, "Accessing entityManager outside of gameLoop");
        Iterable<EntityRef> iterable = () -> entityManager.allEntities();
        final Stream<EntityRef> stream = StreamSupport.stream(iterable.spliterator(), false);
        return stream.filter(entityRef ->
                entityRef.isPresent() && Stream.of(components).allMatch(c -> entityRef.getComponent(c).isPresent())).collect(Collectors.toList());
    }
}
