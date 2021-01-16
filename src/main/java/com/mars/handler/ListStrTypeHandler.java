/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 20-6-28 下午3:34
 * History:
 */
package com.mars.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * ListStrTypeHandler.java
 * 
 * @author ColleyMa
 * @date 20-6-28 下午3:34
 * @version v1.0
 */
public class ListStrTypeHandler extends BaseTypeHandler<List<String>> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
			throws SQLException {
		if (CollectionUtils.isNotEmpty(parameter)) {
			ps.setString(i, listToString(parameter));
			return;
		}
		ps.setString(i, null);
	}

	@Override
	public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String str = rs.getString(columnName);

		if (StringUtils.isNotEmpty(str)) {
			return StringTolist(str);
		}

		return null;
	}

	@Override
	public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String str = rs.getString(columnIndex);

		if (StringUtils.isNotEmpty(str)) {
			return StringTolist(str);
		}

		return null;
	}

	@Override
	public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String str = cs.getString(columnIndex);

		if (StringUtils.isNotEmpty(str)) {
			return StringTolist(str);
		}

		return null;
	}

	private String listToString(List<String> parameter) {
		return Joiner.on(',').join(parameter).toString();
	}

	private List<String> StringTolist(String sequence) {
		return Splitter.on(",").splitToList(sequence);
	}
}
