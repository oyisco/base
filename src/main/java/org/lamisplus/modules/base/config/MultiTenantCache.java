package org.lamisplus.modules.base.config;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.tenant.util.TenantContextHolder;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

@Slf4j
//@Component
public final class MultiTenantCache implements Cache {

    public final Cache delegate;


    public MultiTenantCache(final Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return this.delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        Object translatedKey = translateKey(key);
        LOG.info("Cache get: Key: {}", translatedKey);
        return this.delegate.get(translatedKey);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object translatedKey = translateKey(key);
        return delegate.get(translatedKey, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> callable) {
        ValueWrapper val = delegate.get(key);
        if (val != null) {
            return (T) val.get();
        }

        try {
            return callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void put(Object key, Object value) {
        Object translatedKey = translateKey(key);
        LOG.info("Cache put: Key: {}; value: {}", translatedKey, value);
        this.delegate.put(translatedKey, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object translatedKey = translateKey(key);
        return delegate.putIfAbsent(translatedKey, value);
    }

    @Override
    public void evict(Object key) {
        Object translatedKey = translateKey(key);
        this.delegate.evict(translatedKey);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    private String translateKey(Object key) {
        Assert.notNull(key, "Key must have some value");

        LOG.trace("Translating key {}", key);
        String tenantContext = TenantContextHolder.getTenant();

        Assert.hasText(tenantContext, "Tenant context is required but is not available");

        return tenantContext + ":" + this.getName() + ":" + key;
    }
}
