// FUNcube Data Warehouse
// Copyright 2013 (c) David A.Johnson, G4DPZ, AMSAT-UK
// This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ or send a letter
// to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.

package com.badgersoft.satpredict.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of a simple LRU cache with optional expiry.
 * 
 * @param <K> Type of the cache key
 * @param <V> Type of the objects stored in the cache
 */
public class Cache<K, V> {

    private final ConcurrentMap<K, CacheItem> cache;
    private final int maxSize;
    private final int purgeSize;
    private final long defaultExpiry;
    private final Clock clock;

    public Cache(final Clock clock) {
        this(clock, 0, 0);
    }

    public Cache(final Clock clock, final int maxSize) {
        this(clock, maxSize, maxSize);
    }

    public Cache(final Clock clock, final int maxSize, final int initialCapacity) {
        this.clock = clock;
        this.cache = new ConcurrentHashMap<K, CacheItem>(initialCapacity == 0 ? 16 : initialCapacity);
        this.maxSize = maxSize;
        this.purgeSize = (int)(0.75 * maxSize);
        this.defaultExpiry = 0L;
    }

    public void clear() {
        cache.clear();
    }

    public boolean containsKey(final K key) {
        return cache.containsKey(key);
    }

    public V get(final K key) {
        final CacheItem item = cache.get(key);
        if (item != null) {
            final long now = clock.currentTime();
            if (!item.isExpired(now)) {
                item.accessedDate = now;
                return item.value;
            }
            else {
                cache.remove(key);
            }
        }
        return null;
    }

    public Set<K> keySet() {
        return cache.keySet();
    }

    public V put(final K key, final V value) {
        return put(key, value, defaultExpiry);
    }

    public V put(final K key, final V value, final long expiry) {
        final CacheItem put = cache.put(key, new CacheItem(key, value, expiry));
        if (maxSize > 0 && cache.size() > maxSize) {
            purge();
        }
        if (put == null) {
            return null;
        }
        return put.value;
    }

    public V remove(final K key) {
        final CacheItem removed = cache.remove(key);
        if (removed == null) {
            return null;
        }
        return removed.value;
    }

    private void purge() {
        // Remove expired items
        final long now = clock.currentTime();
        final List<CacheItem> toSort = new ArrayList<CacheItem>(cache.values());
        final Iterator<CacheItem> iterator = toSort.iterator();
        while (iterator.hasNext()) {
            final CacheItem item = iterator.next();
            if (item.isExpired(now)) {
                iterator.remove();
                cache.remove(item.key);
            }
        }

        if (cache.size() > purgeSize) {
            // Remove least recently used until we're at or below the purge size
            // (defaults to 3/4 the max size).
            Collections.sort(toSort, new Comparator<CacheItem>() {
                @Override
                public int compare(final CacheItem o1, final CacheItem o2) {
                    if (o1.accessedDate < o2.accessedDate) {
                        return -1;
                    }
                    else if (o1.accessedDate > o2.accessedDate) {
                        return 1;
                    }
                    return 0;
                }
            });
            final int excess = cache.size() - purgeSize;
            for (int i = 0; i < excess; i++) {
                cache.remove(toSort.get(i).key);
            }
        }
    }

    /**
     * Container for a cache item, specifying expiry and last accessed times.
     */
    private class CacheItem {
        private final K key;
        private final V value;
        private final long expiryDate;
        private long accessedDate;

        public CacheItem(final K key, final V value, final long expiry) {
            super();
            this.key = key;
            this.value = value;
            final long now = clock.currentTime();
            accessedDate = now;
            if (expiry > 0) {
                expiryDate = now + expiry;
            }
            else {
                expiryDate = 0L;
            }
        }

        public boolean isExpired(final long time) {
            return (expiryDate > 0 && expiryDate <= time);
        }
    }
}
