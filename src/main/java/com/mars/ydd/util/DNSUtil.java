package com.mars.ydd.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DNSUtil {

	/**
	 * 清空DNS缓存
	 */
	@SuppressWarnings("rawtypes")
	public static void clearDNSCache() {
		Class clazz = java.net.InetAddress.class;
		try {
			// Positive Cache
			Field cacheField = clazz.getDeclaredField("addressCache");
			cacheField.setAccessible(true);
			Object cache = cacheField.get(clazz);
			Class cacheClazz = cache.getClass();
			Field cacheMapField = cacheClazz.getDeclaredField("cache");
			cacheMapField.setAccessible(true);
			Map cacheMap = (Map) cacheMapField.get(cache);
			synchronized (cacheMap) {
				if (!cacheMap.isEmpty()) {
					cacheMap.clear();
				}
			}

			// Negative Cache
			Field negativeCacheField = clazz.getDeclaredField("negativeCache");
			negativeCacheField.setAccessible(true);
			Object negativeCache = negativeCacheField.get(clazz);
			Class negativeCacheClazz = negativeCache.getClass();
			Field negativeCacheMapField = negativeCacheClazz.getDeclaredField("cache");
			negativeCacheMapField.setAccessible(true);
			Map negativeCacheMap = (Map) negativeCacheMapField.get(negativeCache);
			synchronized (negativeCacheMap) {
				if (!negativeCacheMap.isEmpty()) {
					negativeCacheMap.clear();
				}
			}

		} catch (Exception e) {
		}
	}

	/**
	 * 删除指定DB url的DNS缓存
	 * 
	 * @param host
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeDNSCache(String dbURL) {
		if (null == dbURL || "".equals(dbURL)) {
			return;
		}

		Class clazz = java.net.InetAddress.class;
		try {
			// Positive Cache
			Field cacheField = clazz.getDeclaredField("addressCache");
			cacheField.setAccessible(true);
			Object cache = cacheField.get(clazz);
			Class cacheClazz = cache.getClass();
			Field cacheMapField = cacheClazz.getDeclaredField("cache");
			cacheMapField.setAccessible(true);
			Map cacheMap = (Map) cacheMapField.get(cache);
			synchronized (cacheMap) {
				Iterator<Map.Entry> it = cacheMap.entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = it.next();
					String key = (String) entry.getKey();
					if (dbURL.toLowerCase().contains(key)) {
						it.remove();
						return;
					}
				}
			}

			// Negative Cache
			Field negativeCacheField = clazz.getDeclaredField("negativeCache");
			negativeCacheField.setAccessible(true);
			Object negativeCache = negativeCacheField.get(clazz);
			Class negativeCacheClazz = negativeCache.getClass();
			Field negativeCacheMapField = negativeCacheClazz.getDeclaredField("cache");
			negativeCacheMapField.setAccessible(true);
			Map negativeCacheMap = (Map) negativeCacheMapField.get(negativeCache);
			synchronized (negativeCacheMap) {
				Iterator<Map.Entry> ite = negativeCacheMap.entrySet().iterator();
				while (ite.hasNext()) {
					Entry entry = ite.next();
					String key = (String) entry.getKey();
					if (dbURL.toLowerCase().contains(key)) {
						ite.remove();
						return;
					}
				}
			}

		} catch (Exception e) {
		}
	}

	/**
	 * 删除指定DB url的DNS缓存
	 * 
	 * @param host
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeDNSCache(Set<String> dbURLs) {
		if (null == dbURLs || dbURLs.isEmpty()) {
			return;
		}

		try {
			Class clazz = java.net.InetAddress.class;

			// Positive Cache
			Field cacheField = clazz.getDeclaredField("addressCache");
			cacheField.setAccessible(true);
			Object cache = cacheField.get(clazz);
			Class cacheClazz = cache.getClass();
			Field cacheMapField = cacheClazz.getDeclaredField("cache");
			cacheMapField.setAccessible(true);
			Map cacheMap = (Map) cacheMapField.get(cache);
			synchronized (cacheMap) {
				for (String dbURL : dbURLs) {
					Iterator<Map.Entry> it = cacheMap.entrySet().iterator();
					while (it.hasNext()) {
						Entry entry = it.next();
						String key = (String) entry.getKey();
						if (dbURL.toLowerCase().contains(key)) {
							it.remove();
							break;
						}
					}
				}
			}

			// Negative Cache
			Field negativeCacheField = clazz.getDeclaredField("negativeCache");
			negativeCacheField.setAccessible(true);
			Object negativeCache = negativeCacheField.get(clazz);
			Class negativeCacheClazz = negativeCache.getClass();
			Field negativeCacheMapField = negativeCacheClazz.getDeclaredField("cache");
			negativeCacheMapField.setAccessible(true);
			Map negativeCacheMap = (Map) negativeCacheMapField.get(negativeCache);
			synchronized (negativeCacheMap) {
				for (String dbURL : dbURLs) {
					Iterator<Map.Entry> ite = negativeCacheMap.entrySet().iterator();
					while (ite.hasNext()) {
						Entry entry = ite.next();
						String key = (String) entry.getKey();
						if (dbURL.toLowerCase().contains(key)) {
							ite.remove();
							break;
						}
					}
				}
			}

		} catch (Exception e) {
		}
	}

}
