package de.unitrier.st.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileUtils {
    public static void checkIfFileExists(Path file) throws IllegalArgumentException {
        // ensure that file exists
        if (!Files.exists(file) || Files.isDirectory(file)) {
            throw new IllegalArgumentException("File not found: " + file);
        }
    }

    public static void ensureDirectoryExists(Path dir) throws IllegalArgumentException, IOException {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Directory does not exist: " + dir);
        }
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }
    }

    public static void ensureEmptyDirectoryExists(Path dir) throws IOException {
        // ensure that output dir exists, but is empty
        if (Files.exists(dir)) {
            if (Files.isDirectory(dir)) {
                org.apache.commons.io.FileUtils.deleteDirectory(dir.toFile());
            } else {
                throw new IllegalArgumentException("Not a directory: " + dir);
            }
        }
        Files.createDirectories(dir);
    }

    public static void deleteFileIfExists(Path file) throws IOException {
        if (Files.exists(file)) {
            if (Files.isDirectory(file)) {
                throw new IllegalArgumentException("File is a directory: " + file);
            }

            Files.delete(file);
        }
    }

    public static void createDirectory(Path dir) throws IOException {
        Files.createDirectories(dir);
    }

    public static <T> List<T> processFiles(Path dir, Predicate<Path> filter, Function<Path, T> map) {
        // ensure that input directory exists
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Directory not found: " + dir);
        }

        try {
            return Files.list(dir)
                    .filter(filter)
                    .map(map)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
