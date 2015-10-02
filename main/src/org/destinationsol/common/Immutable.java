package org.destinationsol.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation is used to indicate that a type is immutable, meaning that it's internal is fixed after construction.
 */
@Documented
@Target({ElementType.TYPE})
public @interface Immutable {
}
