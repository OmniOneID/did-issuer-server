package org.omnione.did.issuer.v1.agent.service.oid4vc;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClaimValueFilterTest {
    @Test
    void removesEmptyValuesRecursivelyWhileKeepingZeroAndFalse() {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("empty", "  ");
        claims.put("null", null);
        claims.put("zero", 0);
        claims.put("false", false);
        claims.put("address", Map.of("street", "", "city", "Seoul"));
        claims.put("empty_object", Map.of("value", ""));
        claims.put("items", List.of("", Map.of("name", ""), Map.of("name", "kept")));

        Map<String, Object> filtered = ClaimValueFilter.removeEmptyValues(claims);

        assertFalse(filtered.containsKey("empty"));
        assertFalse(filtered.containsKey("null"));
        assertFalse(filtered.containsKey("empty_object"));
        assertEquals(0, filtered.get("zero"));
        assertEquals(false, filtered.get("false"));
        assertEquals(Map.of("city", "Seoul"), filtered.get("address"));
        assertEquals(List.of(Map.of("name", "kept")), filtered.get("items"));
    }

    @Test
    void removesBothFlatAndNestedRepresentationsOfAClaimPath() {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("address.street", "flat");
        claims.put("address", new LinkedHashMap<>(Map.of("street", "nested", "city", "Seoul")));

        ClaimValueFilter.removeValue(claims, "address.street");

        assertFalse(claims.containsKey("address.street"));
        assertEquals(Map.of("city", "Seoul"), claims.get("address"));
    }
}
