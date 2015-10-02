package org.destinationsol.common;

import java.lang.annotation.Documented;

/**
 * When used for annotating methods, indicates that the returned float value is an angle and is normalized (-180 < a && a <= 180)
 * When used for annotating params, indicates that the param angle must be normalized
 */
@Documented
public @interface Norm {

}
