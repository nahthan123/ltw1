package com.example.schoolmanager.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("render")
public class RenderDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DatabaseCredentials credentials = resolveCredentials();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(credentials.jdbcUrl());
        config.setUsername(credentials.username());
        config.setPassword(credentials.password());
        config.setDriverClassName("org.postgresql.Driver");

        return new HikariDataSource(config);
    }

    private DatabaseCredentials resolveCredentials() {
        String rawUrl = firstNonBlank(System.getenv("DATABASE_URL"), System.getenv("SPRING_DATASOURCE_URL"));
        if (isBlank(rawUrl)) {
            throw new IllegalStateException(
                    "Missing database URL. Set DATABASE_URL to the Render Postgres internal URL.");
        }

        if (rawUrl.startsWith("jdbc:")) {
            URI jdbcUri = URI.create(rawUrl.substring("jdbc:".length()));
            String username = firstNonBlank(
                    System.getenv("SPRING_DATASOURCE_USERNAME"),
                    extractQueryParam(jdbcUri, "user"));
            String password = firstNonBlank(
                    System.getenv("SPRING_DATASOURCE_PASSWORD"),
                    extractQueryParam(jdbcUri, "password"));
            if (isBlank(username) || isBlank(password)) {
                throw new IllegalStateException(
                        "Missing database credentials. Set DATABASE_URL or SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD.");
            }
            return new DatabaseCredentials(rawUrl, username, password);
        }

        URI uri = URI.create(rawUrl);
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + buildPortAndPath(uri);

        String username = firstNonBlank(
                System.getenv("SPRING_DATASOURCE_USERNAME"),
                extractUserInfo(uri, 0),
                extractQueryParam(uri, "user"));
        String password = firstNonBlank(
                System.getenv("SPRING_DATASOURCE_PASSWORD"),
                extractUserInfo(uri, 1),
                extractQueryParam(uri, "password"));

        if (isBlank(username) || isBlank(password)) {
            throw new IllegalStateException(
                    "Missing database credentials. Use the Render Postgres internal URL or set SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD.");
        }

        return new DatabaseCredentials(jdbcUrl, username, password);
    }

    private String buildPortAndPath(URI uri) {
        StringBuilder builder = new StringBuilder();
        if (uri.getPort() > 0) {
            builder.append(':').append(uri.getPort());
        }
        if (!isBlank(uri.getPath())) {
            builder.append(uri.getPath());
        }
        if (!isBlank(uri.getQuery())) {
            builder.append('?').append(uri.getQuery());
        }
        return builder.toString();
    }

    private String extractUserInfo(URI uri, int index) {
        String userInfo = uri.getUserInfo();
        if (isBlank(userInfo)) {
            return null;
        }
        String[] parts = userInfo.split(":", 2);
        if (index == 0) {
            return parts[0];
        }
        return parts.length > 1 ? parts[1] : null;
    }

    private String extractQueryParam(URI uri, String key) {
        String query = uri.getQuery();
        if (isBlank(query)) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && key.equals(parts[0])) {
                return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private record DatabaseCredentials(String jdbcUrl, String username, String password) {
        private DatabaseCredentials {
            Objects.requireNonNull(jdbcUrl, "jdbcUrl");
            Objects.requireNonNull(username, "username");
            Objects.requireNonNull(password, "password");
        }
    }
}
