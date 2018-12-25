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
package org.destinationsol.nui.internal;

import org.terasology.assets.ResourceUrn;
import org.terasology.math.Border;
import org.terasology.math.geom.BaseVector2;
import org.terasology.math.geom.Quat4f;
import com.badlogic.gdx.math.Rectangle;
import org.terasology.math.geom.Vector2;
import org.terasology.math.geom.Vector3f;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.assets.material.Material;
import org.terasology.rendering.assets.mesh.Mesh;
import org.terasology.rendering.assets.texture.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import org.destinationsol.nui.HorizontalAlign;
import org.destinationsol.nui.ScaleMode;
import org.destinationsol.nui.VerticalAlign;
import org.terasology.rendering.opengl.FrameBufferObject;

/**
 */
public interface CanvasRenderer {

    void preRender();

    void postRender();

    Vector2 getTargetSize();

    void crop(Rectangle cropRegion);

    FrameBufferObject getFBO(ResourceUrn urn, BaseVector2 size);

    void drawMesh(Mesh mesh, Material material, Rectangle drawRegion, Rectangle cropRegion, Quat4f rotation, Vector3f offset, float scale, float alpha);

    void drawMaterialAt(Material material, Rectangle drawRegion);

    void drawLine(int sx, int sy, int ex, int ey, Color color);

    void drawTexture(TextureRegion texture, Color color, ScaleMode mode, Rectangle absoluteRegion, float ux, float uy, float uw, float uh, float alpha);

    void drawText(String text, Font font, HorizontalAlign hAlign, VerticalAlign vAlign, Rectangle absoluteRegion, Color color,
                  Color shadowColor, float alpha, boolean underlined);

    void drawTextureBordered(TextureRegion texture, Rectangle absoluteRegion, Border border, boolean tile, float ux, float uy, float uw, float uh, float alpha);
}
