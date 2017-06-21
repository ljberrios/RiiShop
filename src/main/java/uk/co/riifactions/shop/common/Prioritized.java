package uk.co.riifactions.shop.common;

import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this to services or commands that should be initialized before other "normal" commands and services.
 *
 * @author Thortex
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Beta // We may be removing this. Adding a rank system would be more efficient. (example of this: HK2)
public @interface Prioritized {}
