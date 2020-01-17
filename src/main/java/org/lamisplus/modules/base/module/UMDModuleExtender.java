package org.lamisplus.modules.base.module;

import com.coveo.nashorn_modules.Require;
import com.coveo.nashorn_modules.ResourceFolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.extern.slf4j.Slf4j;

import javax.script.Invocable;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.LRUMap;

@Slf4j
public class UMDModuleExtender {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final NashornScriptEngine engine;
    private static final Map<String, UMDModule> MODULE_CACHE = new LRUMap<>();

    public static UMDModule extendModule(UMDModule module, List<UMDModule> importModules) {
        String key = module.getName() + importModules.stream()
                .sorted()
                .map(UMDModule::getName)
                .collect(Collectors.joining());
        UMDModule module1 = MODULE_CACHE.get(key);
        if (module1 != null) {
            return module1;
        }
        String content = module.getContent();
        Map<String, String> moduleMap = new HashMap<>();
        try {
            moduleMap = OBJECT_MAPPER.readValue(module.getMap(),
                    new TypeReference<Map<String, String>>() {
                    });
        } catch (IOException ignored) {
        }
        for (UMDModule importModule : importModules) {
            content = extendModule(content, module.getName(), importModule.getName());
            try {
                Map<String, String> map = OBJECT_MAPPER.readValue(importModule.getMap(),
                        new TypeReference<Map<String, String>>() {
                        });
                map.forEach(moduleMap::put);
            } catch (IOException ignored) {
            }
            moduleMap.put(importModule.getName(), importModule.getPath());
        }
        module = new UMDModule(module.getName(), module.getPath(), content);
        try {
            module.setMap(OBJECT_MAPPER.writeValueAsString(moduleMap));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        MODULE_CACHE.put(key, module);
        return module;
    }

    private static String extendModule(String moduleContent, String moduleName, String importModule) {
        try {
            return (String) ((Invocable) engine).invokeFunction("convert", moduleContent, moduleName, importModule);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return moduleContent;
    }

    static {
        String[] options = new String[]{"--language=es6"};
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        engine = (NashornScriptEngine) factory.getScriptEngine(options);
        engine.put("global", new SimpleBindings());

        ResourceFolder rootFolder = ResourceFolder.create(UMDModuleExtender.class.getClassLoader(), "scripts", "UTF-8");
        try {
            engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js')");
            engine.eval("load('classpath:views/static/base/js/umd.modifier.js')");
            Require.enable(engine, rootFolder);
            engine.eval("var esprima=require('esprima');var estraverse=require('estraverse');var escodegen=require('escodegen');");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
