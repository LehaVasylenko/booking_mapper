package com.example.demo.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Configuration properties class for the Pop Order integration.
 * <p>
 * This class holds the properties required for configuring the integration with the Booking system.
 * It is populated with values from the application configuration (e.g., application.properties or application.yml)
 * based on the prefix "pop-order".
 * </p>
 * <p>
 * It includes properties such as the Booking URL, pop-order path, update path, agent identifier, and rate settings.
 * </p>
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Component
@ConfigurationProperties(prefix = "pop-order")
@Getter
@Setter
@Primary
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PopOrderProperties {
    /**
     * The base URL for the Booking integration.
     * <p>
     * This URL is used to connect to the Booking service.
     * </p>
     */
    String url;
    /**
     * The path to /pop-order.
     */
    String pop;
    /**
     * The path to /upd-order.
     */
    String upd;
}