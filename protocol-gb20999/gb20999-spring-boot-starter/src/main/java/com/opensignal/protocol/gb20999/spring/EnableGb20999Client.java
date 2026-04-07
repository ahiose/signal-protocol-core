package com.opensignal.protocol.gb20999.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables GB/T 20999 auto-configuration (equivalent to relying on {@code spring.factories}).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(Gb20999AutoConfiguration.class)
public @interface EnableGb20999Client {
}
