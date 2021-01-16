package com.mars.kit.criterion.common;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public final class Assertion {
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}

	
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	
	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - this argument is required; it must not be null");
	}

	


	
	public static void notEmpty(Object[] array, String message) {
		if (ArrayUtils.isEmpty(array)) {
			throw new IllegalArgumentException(message);
		}
	}

	
	public static void notEmpty(Object[] array) {
		notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
	}


	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	
	public static void noNullElements(Object[] array) {
		noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
	}

	
	public static void notEmpty(@SuppressWarnings("rawtypes") Collection collection, String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(@SuppressWarnings("rawtypes") Collection collection) {
		notEmpty(collection,
				"[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
	}

	
	public static void notEmpty(@SuppressWarnings("rawtypes") Map map, String message) {
		if (MapUtils.isEmpty(map)) {
			throw new IllegalArgumentException(message);
		}
	}

	
	public static void notEmpty(@SuppressWarnings("rawtypes") Map map) {
		notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
	}
	
	public static void notEmpty(String strval, String message) {
		if (StringUtils.isEmpty(strval)) {
			throw new IllegalArgumentException(message);
		}
	}

	
	public static void notEmpty(String strval) {
		notEmpty(strval, "[Assertion failed] - this String value must not be empty; it must contain at least one entry");
	}


	
	public static void isInstanceOf(@SuppressWarnings("rawtypes") Class clazz, Object obj) {
		isInstanceOf(clazz, obj, "");
	}

	
	public static void isInstanceOf(@SuppressWarnings("rawtypes") Class type, Object obj, String message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			throw new IllegalArgumentException(message +
					". Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
					"] must be an instance of " + type);
		}
	}

	
	public static void isAssignable(@SuppressWarnings("rawtypes") Class superType, @SuppressWarnings("rawtypes") Class subType) {
		isAssignable(superType, subType, "");
	}

	
	@SuppressWarnings("unchecked")
	public static void isAssignable(@SuppressWarnings("rawtypes") Class superType, @SuppressWarnings("rawtypes") Class subType, String message) {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
		}
	}


	
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	
	public static void state(boolean expression) {
		state(expression, "[Assertion failed] - this state invariant must be true");
	}
}
