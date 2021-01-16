/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-6-3 上午11:32
 * History:
 */
package com.mars.kit.archiver.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mars.kit.common.util.MD5Support;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;


/**
 * EhCacheUtils.java
 *
 * @author ColleyMa
 * @version 19-6-3 上午11:32
*/
public class EhCacheUtils {
    private static EhCacheUtils instance = null;
    private static String fileName = "mailSender-ehcache.xml";
    private static CacheManager cacheManager;
    private static final int MAX_LENGTH = 48;
    private static final String ENCODER = "UTF-8";
    private static final String LOCAL_CACHE_NAME = "mailSender_cache";
    private static final int defaultExpiryMinutes = 30;
    private static Cache localCache = null;
    private static Lock lock = new ReentrantLock();

    public static EhCacheUtils getInstance() {
        lock.lock();
        try {
            if (instance == null) {
                cacheManager = loadPropertiesFile();
                instance = new EhCacheUtils();
            }

            if (localCache == null) {
                localCache = cacheManager.getCache(LOCAL_CACHE_NAME);
            }
        } finally {
            lock.unlock();
        }

        return instance;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public static CacheManager loadPropertiesFile() {
        CacheManager m = null;
        InputStream is = null;

        try {
            File file = new File(fileName);

            if (!file.exists()) {
                is = EhCacheUtils.class.getResourceAsStream(fileName);
            } else {
                is = new FileInputStream(file);
            }

            if (is != null) {
                m = CacheManager.newInstance(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return m;
    }

    public Object get(String key) {
        Object obj = null;
        String goodKey = getGoodKey(key);

        Element v = localCache.get(goodKey);

        if (v != null) {
            obj = v.getObjectValue();
        }

        return obj;
    }

    public void put(String key, Object value) {
        put(key, value, defaultExpiryMinutes);
    }

    public void put(String key, Object value, int expirMins) {
        if ((StringUtils.isNotBlank(key)) && (value != null)) {
            String goodKey = getGoodKey(key);

            Element e = new Element(goodKey, value);
            e.setTimeToIdle(expirMins * 60);
            e.setTimeToLive(expirMins * 60);
            localCache.put(e);
        }
    }

    public void remove(String key) {
        String goodKey = getGoodKey(key);
        localCache.remove(goodKey);
    }

    public static Cache getLocalCache() {
        return localCache;
    }

    public static String getGoodKey(String key) {
        int bylength = 0;
        String oldkey = key;
        String encodekey = "";

        try {
            encodekey = URLEncoder.encode(oldkey, ENCODER);
        } catch (Exception e1) {
            e1.printStackTrace();

            try {
                encodekey = URLEncoder.encode(oldkey, ENCODER);
            } catch (UnsupportedEncodingException e) {
                encodekey = oldkey;
                e.printStackTrace();
            }
        }

        try {
            bylength = encodekey.length();

            if ((bylength > 240) || (oldkey.indexOf(' ') > -1)) {
                String headkey = null;

                if (encodekey.length() >= MAX_LENGTH) {
                    headkey = encodekey.substring(0, MAX_LENGTH);
                } else {
                    headkey = encodekey;
                }

                return headkey + "_" + MD5Support.MD5(oldkey);
            }

            return oldkey;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return oldkey;
    }
}
