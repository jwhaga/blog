package com.aurora.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ConfigValidationRunner implements ApplicationRunner {

    private final Environment environment;

    public ConfigValidationRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProd = false;
        for (String profile : activeProfiles) {
            if ("prod".equals(profile)) {
                isProd = true;
                break;
            }
        }

        if (!isProd) {
            log.info("ConfigValidation skipped (profile: {})", activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default");
            return;
        }

        List<String> missing = new ArrayList<>();

        checkRequired(missing, "MYSQL_URL");
        checkRequired(missing, "MYSQL_USERNAME");
        checkRequired(missing, "MYSQL_PASSWORD");
        checkRequired(missing, "REDIS_HOST");
        checkRequired(missing, "REDIS_PASSWORD");
        checkRequired(missing, "JWT_SECRET");
        checkRequired(missing, "RABBITMQ_USERNAME");
        checkRequired(missing, "RABBITMQ_PASSWORD");

        String uploadMode = environment.getProperty("UPLOAD_MODE");
        if (uploadMode == null || uploadMode.isEmpty()) {
            missing.add("UPLOAD_MODE");
        } else if ("oss".equalsIgnoreCase(uploadMode)) {
            checkRequired(missing, "OSS_ACCESS_KEY_ID");
            checkRequired(missing, "OSS_ACCESS_KEY_SECRET");
        } else if ("minio".equalsIgnoreCase(uploadMode)) {
            checkRequired(missing, "MINIO_ACCESS_KEY");
            checkRequired(missing, "MINIO_SECRET_KEY");
        }

        if (!missing.isEmpty()) {
            log.error("============================================");
            log.error("  CRITICAL CONFIGURATION ERROR");
            log.error("  The following required env vars are not set:");
            for (String key : missing) {
                log.error("    - {}", key);
            }
            log.error("");
            log.error("  The application may fail at runtime.");
            log.error("  Please set these env vars or see .env.example.");
            log.error("============================================");
        } else {
            log.info("ConfigValidation: all required environment variables are set.");
        }
    }

    private void checkRequired(List<String> missing, String key) {
        String value = environment.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            missing.add(key);
        }
    }

}
