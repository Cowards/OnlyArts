package com.cowards.onlyarts.config;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * The {@code ApplicationConfig} class is a configuration class for setting up
 * JAX-RS (Java API for RESTful Web Services) resources within an application.
 * It extends {@link javax.ws.rs.core.ResourceConfig}, which is a class provided
 * by JAX-RS for configuring and deploying resources.
 * <p>
 * The {@link javax.ws.rs.ApplicationPath} annotation is used to specify the
 * base URI path for all resources defined in this application. In this case,
 * the base URI path is "/api".
 * <p>
 * Inside the constructor, the {@link #packages(String...)} method is called to
 * specify the package(s) to scan for JAX-RS annotated classes. These annotated
 * classes are then registered as resources for the application.
 * <p>
 * Note: JAX-RS annotations are used for defining RESTful web services in Java.
 */
@ApplicationPath("api")
public class ApplicationConfig extends ResourceConfig {

    /**
     * Constructs a new {@code ApplicationConfig} instance.
     * <p>
     * This constructor initializes the configuration for the application by
     * specifying the package(s) to scan for JAX-RS annotated classes. These
     * annotated classes are then registered as resources for the application.
     */
    public ApplicationConfig() {
        packages("com.cowards.onlyarts.resources");
    }
}
