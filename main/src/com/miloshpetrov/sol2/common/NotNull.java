package com.miloshpetrov.sol2.common;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Used to indicate that the value of a parameter/variable is not allowed to be null.
 * When used on a method, it means that the method should never return a null value.
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER,ElementType.LOCAL_VARIABLE, ElementType.METHOD})
public @interface NotNull {

}