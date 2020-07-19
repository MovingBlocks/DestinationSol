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
package org.destinationsol.moneyDropping.systems;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.In;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.entitysystem.EventReceiver;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.Loot;
import org.destinationsol.game.item.LootBuilder;
import org.destinationsol.game.item.MoneyItem;
import org.destinationsol.location.components.Position;
import org.destinationsol.location.components.Velocity;
import org.destinationsol.moneyDropping.components.DropsMoneyOnDeath;
import org.destinationsol.removal.DefaultDestructionSystem;
import org.destinationsol.removal.DestroyEvent;
import org.destinationsol.size.components.Size;
import org.terasology.gestalt.entitysystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.event.Before;
import org.terasology.gestalt.entitysystem.event.EventResult;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.List;

/**
 * When an entity with a {@link DropsMoneyOnDeath} component is destroyed, this system creates an amount of money based
 * on its {@link Size}.
 */
public class MoneyDroppingSystem implements EventReceiver {

    private final float MIN_MULTIPLIER = 12f;
    private final float MAX_MULTIPLIER = 40f;

    @In
    private SolGame game;

    @In
    private LootBuilder lootBuilder;

    @In
    private ItemManager itemManager;

    @In
    private ObjectManager objectManager;

    @ReceiveEvent(components = {DropsMoneyOnDeath.class, Position.class, Velocity.class, Size.class})
    @Before(DefaultDestructionSystem.class)
    public EventResult onDestroy(DestroyEvent event, EntityRef entity) {

        Vector2 basePosition = entity.getComponent(Position.class).get().position;
        Vector2 baseVelocity = entity.getComponent(Velocity.class).get().velocity;
        float size = entity.getComponent(Size.class).get().size;

        float moneyAmount = size * SolRandom.randomFloat(MIN_MULTIPLIER, MAX_MULTIPLIER);
        List<MoneyItem> moneyItems = itemManager.moneyToItems(moneyAmount);
        for (MoneyItem item : moneyItems) {
            float velocityAngle = SolRandom.randomFloat(180);
            Vector2 moneyVelocity = new Vector2();
            SolMath.fromAl(moneyVelocity, velocityAngle, SolRandom.randomFloat(0, Loot.MAX_SPD));
            moneyVelocity.add(baseVelocity);
            Vector2 moneyPosition = new Vector2();
            SolMath.fromAl(moneyPosition, velocityAngle, SolRandom.randomFloat(0, size / 2));
            moneyPosition.add(basePosition);
            Loot money = lootBuilder.build(game, moneyPosition, item, moneyVelocity, Loot.MAX_LIFE, SolRandom.randomFloat(Loot.MAX_ROT_SPD), null);
            objectManager.addObjDelayed(money);
        }

        return EventResult.CONTINUE;
    }
}
