/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.issuer.v1.agent.service.oid4vc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClaimValueFilter {
    private ClaimValueFilter() {
    }

    public static Map<String, Object> removeEmptyValues(Map<String, Object> claims) {
        Map<String, Object> filtered = new LinkedHashMap<>();
        if (claims == null) {
            return filtered;
        }
        claims.forEach((key, value) -> {
            Object filteredValue = filter(value);
            if (filteredValue != null) {
                filtered.put(key, filteredValue);
            }
        });
        return filtered;
    }

    public static void removeValue(Map<String, Object> claims, String path) {
        if (claims == null || path == null) {
            return;
        }
        claims.remove(path);
        removeNestedValue(claims, path.split("\\."), 0);
    }

    @SuppressWarnings("unchecked")
    private static boolean removeNestedValue(Map<String, Object> claims, String[] parts, int index) {
        if (index == parts.length - 1) {
            claims.remove(parts[index]);
            return claims.isEmpty();
        }
        Object nested = claims.get(parts[index]);
        if (nested instanceof Map<?, ?>
                && removeNestedValue((Map<String, Object>) nested, parts, index + 1)) {
            claims.remove(parts[index]);
        }
        return claims.isEmpty();
    }

    private static Object filter(Object value) {
        if (value == null || value instanceof CharSequence text && text.toString().isBlank()) {
            return null;
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> filtered = new LinkedHashMap<>();
            map.forEach((key, nestedValue) -> {
                Object valueToKeep = filter(nestedValue);
                if (key != null && valueToKeep != null) {
                    filtered.put(key.toString(), valueToKeep);
                }
            });
            return filtered.isEmpty() ? null : filtered;
        }
        if (value instanceof Iterable<?> iterable) {
            List<Object> filtered = new ArrayList<>();
            iterable.forEach(item -> {
                Object itemToKeep = filter(item);
                if (itemToKeep != null) {
                    filtered.add(itemToKeep);
                }
            });
            return filtered.isEmpty() ? null : filtered;
        }
        return value;
    }
}
