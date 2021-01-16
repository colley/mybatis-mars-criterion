/*
 * Copyright (c) 2016-2017 by Colley
 * All rights reserved.
 */
package com.mars.kit.criterion.common;

public enum ExprOper {
	eq("="), 
	ieq("="),
	ne("<>"), 
	like(" LIKE "), 
	ilike(" LIKE "),
	notLike(" NOT LIKE "),
	gt(">"), 
	lt("<"), 
	le("<="), 
	ge(">="), 
	in(" IN"),
	notin(" NOT IN"),
	isNull(" IS NULL"), 
	isNotNull(" IS NOT NULL"), 
	notBetween(" NOT BETWEEN "),
	between(" BETWEEN "),
	proExpr("proExpr"),
	groupBy(" GROUP BY "),
	subSel("subSel"),
	select("select"),
	tName("tName"),
	fuzzy("fuzzy"),
	limit(" limit "),
	AND_JUNC(" AND "),
	OR_JUNC(" OR "),
	LFET_JOIN(" LEFT JOIN "), 
	RIGHT_JOIN(" RIGHT JOIN "),
	INNER_JOIN(" INNER JOIN "),
	JOIN(" JOIN "),
	INSERT(" INSERT "),
	REPLACE(" REPLACE "),
	UPDATE(" UPDATE "),
	DELETE(" DELETE "),
	NOT(" NOT "),
    WHEN(" WHEN "),
    WHEN_PROPER("proper"),
    S_FUNC("S_FUNC"),
    FUNC("FUNC"),
    CONST("const");
	
	
	private String op;
	ExprOper(String op){
		this.op = op;
	}
	public String getOp() {
		return op;
	}
}
