package org.omnione.did.issuer.v1.agent.service.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreService {
    private static Map<String, String> store = new HashMap<>();
    private static long sequence = 0L;

    private static final StoreService instance = new StoreService();

    public static StoreService getInstance() {
        return instance;
    }

    private StoreService() {
    }


    public String save(String key, String value) {
        store.put(key, value);
        return value;
    }

    public String findById(String key) {
        return store.get(key);
    }

    public List<String> findAll(){
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
