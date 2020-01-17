package org.lamisplus.modules.base.module;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.yml.ModuleConfig;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ModuleUtils {

    public static void copyPathFromJar(final URL jarPath, final String path, final Path target) throws Exception {
        Map<String, String> env = new HashMap<>();
        String absPath = jarPath.toString();
        URI uri = URI.create("jar:" + absPath);
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Path pathInZipfile = zipfs.getPath(path);
            Files.walkFileTree(pathInZipfile, new SimpleFileVisitor<Path>() {

                private Path currentTarget;

                @Override
                public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                    currentTarget = target.resolve(pathInZipfile.relativize(dir)
                            .toString());
                    if (!Files.exists(currentTarget)) {
                        Files.createDirectories(currentTarget);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(pathInZipfile.relativize(file)
                            .toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    static void addClassPathUrl(URL url, ClassLoader classLoader) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadModuleConfig(InputStream zip, String name, List<ModuleConfig> configs) {
        try (ZipInputStream zin = new ZipInputStream(zip)) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().endsWith(".jar")) {
                    loadModuleConfig(zin, name, configs);
                }
                if (entry.getName().equals(name)) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(zin));
                    Yaml yaml = new Yaml(new Constructor(ModuleConfig.class));
                    configs.add(yaml.load(in));
                }
                zin.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Could not load module.yml");
        }
    }
}
