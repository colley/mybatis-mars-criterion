package com.mars.ydd.monitor.util;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlExportParameterVisitor;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleParameterizedOutputVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.ExportParameterizedOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.mars.ydd.monitor.processor.MonitorDataProcessor;
import com.mars.ydd.monitor.processor.MonitorDataProcessor.DbType;
public class SqlUtils {
	 public static String generatePreparedStatement(final String sql, MonitorDataProcessor monitorDataProcessor) {
	        StringBuilder out = new StringBuilder();
	        SQLASTVisitor visitor;
	        SQLStatementParser parser;
	        if (DbType.ORACLE.equals(monitorDataProcessor.getDbtype())) {
	            visitor = new OracleParameterizedOutputVisitor(out);
	            parser = new OracleStatementParser(sql);
	        } else if (DbType.MYSQL.equals(monitorDataProcessor.getDbtype())) {
	            visitor = new MySqlExportParameterVisitor(out);
	            parser = new MySqlStatementParser(sql);
	        } else if (DbType.H2.equals(monitorDataProcessor.getDbtype())) {
	            visitor = new MySqlExportParameterVisitor(out);
	            parser = new MySqlStatementParser(sql);
	        } else {
	            visitor = new ExportParameterizedOutputVisitor(out);
	            parser = new SQLStatementParser(sql);
	        }

	        SQLStatement statement = parser.parseStatementList().get(0);
	        statement.accept(visitor);
	        return out.toString();
	    }
}
