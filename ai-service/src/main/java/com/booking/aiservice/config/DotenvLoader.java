package com.booking.aiservice.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Loads KEY=value pairs from {@code .env} files into system properties
 * before Spring starts (so {@code OPENAI_API_KEY} and {@link AiEnvHelper} work).
 * <p>
 * Project root {@code .env} first, then {@code ai-service/.env} last (wins).
 */
public final class DotenvLoader {

  private static final Logger log = Logger.getLogger(DotenvLoader.class.getName());

  private DotenvLoader() {}

  public static void load() {
    Path cwd = Path.of("").toAbsolutePath();
    Path inModule = cwd.resolve(".env");
    Path inRoot = cwd.resolve("..").normalize().resolve(".env");

    loadFileIfExists(inRoot);
    loadFileIfExists(inModule);
  }

  private static void loadFileIfExists(Path path) {
    if (!Files.isRegularFile(path)) {
      return;
    }
    try {
      List<String> lines = Files.readAllLines(path);
      int count = 0;
      for (String line : lines) {
        if (applyLine(line)) {
          count++;
        }
      }
      log.info("Loaded " + count + " entries from " + path);
    } catch (Exception ex) {
      log.warning("Could not read " + path + ": " + ex.getMessage());
    }
  }

  /** @return true if a property was set */
  private static boolean applyLine(String raw) {
    String line = raw.trim();
    if (line.isEmpty() || line.startsWith("#")) {
      return false;
    }
    int eq = line.indexOf('=');
    if (eq <= 0) {
      return false;
    }
    String key = line.substring(0, eq).trim();
    String value = line.substring(eq + 1).trim();
    if ((value.startsWith("\"") && value.endsWith("\""))
        || (value.startsWith("'") && value.endsWith("'"))) {
      value = value.substring(1, value.length() - 1);
    }
    // .env file wins over terminal $env: for ai-service (easier local setup)
    System.setProperty(key, value);
    return true;
  }
}
