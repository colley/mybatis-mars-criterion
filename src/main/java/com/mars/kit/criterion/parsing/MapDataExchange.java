/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-7 下午3:30
 * History:
 */
package com.mars.kit.criterion.parsing;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;


/**
 * MapDataExchange.java
 *
 * @author ColleyMa
 * @version 19-5-7 下午3:30
*/
public class MapDataExchange {
    public Object[] getData(ParamMapping[] mappings, Object parameterObject) {
    	if(ArrayUtils.isEmpty(mappings) || parameterObject==null) {
    		return null;
    	}
        if (!(parameterObject instanceof Map)) {
            throw new RuntimeException("Error.  Object passed into MapDataExchange was not an instance of Map.");
        }
        
        Object[] data = new Object[mappings.length];
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = (Map<Object, Object>) parameterObject;
        for (int i = 0; i < mappings.length; i++) {
            data[i] = map.get(mappings[i].getPropertyName());
        }
        return data;
    }
}
