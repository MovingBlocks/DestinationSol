package com.miloshpetrov.sol2.common;

import java.lang.annotation.Documented;

/**
 * When used for annotating methods, indicates that the returned Vector is bound.
 * Bound vectors should be freed before leaving the method. They should not be used after they are freed.
 * When used for annotating params, indicates that the param Vector is bound and should be freed in the method
 * (or passed into another method that requires the bound Vector)
 */
@Documented
public @interface Bound {
}
