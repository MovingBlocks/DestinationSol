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
package org.destinationsol.nui.widgets;

import org.terasology.input.InputSystem;
import org.terasology.input.device.MouseDevice;
import org.terasology.math.TeraMath;
import com.badlogic.gdx.math.Rectangle;
import org.terasology.math.geom.Vector2;
import org.terasology.registry.CoreRegistry;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreWidget;
import org.destinationsol.nui.LayoutConfig;
import org.destinationsol.nui.SubRegion;
import org.destinationsol.nui.UIWidget;
import org.destinationsol.nui.skin.UIStyle;

/**
 */
public class CursorAttachment extends CoreWidget {
    private static final int MOUSE_CURSOR_HEIGHT = 18;

    @LayoutConfig
    private UIWidget attachment;

    public UIWidget getAttachment() {
        return attachment;
    }

    public void setAttachment(UIWidget attachment) {
        this.attachment = attachment;
    }

    @Override
    public void onDraw(Canvas canvas) {
        UIStyle style = canvas.getCurrentStyle();
        Vector2 attachmentSize = canvas.calculatePreferredSize(attachment);
        attachmentSize.add(style.getMargin().getTotals());

        // TODO get rid of CoreRegistry. e.g. by updatin a mousePos field with an InteractionListener
        MouseDevice mouse = CoreRegistry.get(InputSystem.class).getMouseDevice();

        int top;
        switch (style.getVerticalAlignment()) {
            case TOP:
                top = mouse.getPosition().y - attachmentSize.y;
                break;
            case MIDDLE:
                top = mouse.getPosition().y - attachmentSize.y / 2;
                break;
            default:
                top = mouse.getPosition().y + MOUSE_CURSOR_HEIGHT;
                break;
        }
        top = TeraMath.clamp(top, 0, canvas.size().y - attachmentSize.y);
        int left;
        switch (style.getHorizontalAlignment()) {
            case RIGHT:
                left = mouse.getPosition().x - attachmentSize.x;
                break;
            case CENTER:
                left = mouse.getPosition().x - attachmentSize.x / 2;
                break;
            default:
                left = mouse.getPosition().x;
                break;
        }
        left = TeraMath.clamp(left, 0, canvas.size().x - attachmentSize.x);


        try (SubRegion ignored = canvas.subRegion(Rectangle.createFromMinAndSize(left, top, attachmentSize.x, attachmentSize.y), false)) {
            canvas.drawBackground();
            canvas.drawWidget(attachment, style.getBackgroundBorder().shrink(canvas.getRegion()));
        }
    }

    @Override
    public boolean isSkinAppliedByCanvas() {
        return false;
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        return canvas.calculateRestrictedSize(attachment, sizeHint);
    }

    @Override
    public boolean isVisible() {
        // TODO get rid of CoreRegistry. e.g. by updatin a mouseVisibility field with an enhanced InteractionListener
        MouseDevice mouse = CoreRegistry.get(InputSystem.class).getMouseDevice();
        return super.isVisible() && mouse.isVisible() && getAttachment() != null && getAttachment().isVisible();
    }
}
