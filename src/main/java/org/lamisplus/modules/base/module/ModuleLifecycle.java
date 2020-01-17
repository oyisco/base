package org.lamisplus.modules.base.module;

public interface ModuleLifecycle {
    default void preInstall() {}

    default void preUninstall() {}
}
