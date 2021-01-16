criterion-toolkit
====
criterion-toolkit
- 测试用例
<!-- -->
	public  void testInExpression(){
		InExpression in = new InExpression("age1", new Integer[]{1,3,4,5},ExprOper.in);
		System.out.println(in.getSqlString(criterionQuery));
	}
	
	
	public  void testSimpleExpression(){
		Criterion simple = new SimpleExpression("t.age",123,ExprOper.eq);
		System.out.println(simple.getSqlString(criterionQuery));
		
	}
	
	
	public  void testLikeExpression(){
		Criterion like = IbsRestrictions.ilike("name", "coll");
		System.out.println(like.getSqlString(criterionQuery));
		like = IbsRestrictions.notLike("name1", "c", IbsMatchMode.ANYWHERE);
		System.out.println(like.getSqlString(criterionQuery));
		PropertyExpression pro = IbsRestrictions.gtProperty("t2.age", "t.age");
		System.out.println(pro.getSqlString(criterionQuery));
		
		JunctionExpression junction = IbsRestrictions.conjunction();
		junction.add(like);
		junction.add(pro);
		System.out.println(junction.getSqlString(criterionQuery));
		
		FuzzyExpression fuzzy = IbsRestrictions.fuzzy("test", "test", ">=");
		System.out.println(fuzzy.getSqlString(criterionQuery));
		junction.add(fuzzy);
		System.out.println(junction.getSqlString(criterionQuery));
	}
	
	
	public  void testSelectSql(){
		DetachedHsCriteria detachedIbtsCriteria = DetachedHsCriteria.forInstance();
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t2.AD_KEYWORDS_ID,t2.KEYWORD_MAX_CPC as historyValue,t2.CHANNEL_ID"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t2.CHANNEL_ACCOUNT_ID,t2.MATCH_TYPE,t3.AD_GROUP_ID,t4.AD_CAMPAIGN_ID"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t4.name as AD_CAMPAIGN_NAME,t2.STATUS as historyStatus,t3.name as AD_GROUP_NAME"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t2.name as AD_KEYWORD_NAME"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t5.ORDER_COUNT"));
		detachedIbtsCriteria.addColumnName(AliasColumn.as("CASE WHEN SUM(t5.ORDER_COUNT_NEW_CLIENT) = 0 THEN 0 ELSE CAST(ROUND(SUM(t5.COSTS)/SUM(t5.ORDER_COUNT_NEW_CLIENT),2) AS CHAR(50)) END","CPN"));
		detachedIbtsCriteria.addColumnName(AliasColumn.as("CASE WHEN SUM(t5.COSTS) = 0 THEN 0 ELSE CAST(ROUND(SUM(t5.SALES_VALUE)/SUM(t5.COSTS),2) AS CHAR(50)) END","ROI"));
		
		//设置from table
		detachedIbtsCriteria.addFromClause(TableFromCriteria.setTableName("AD_KEYWORD t2"));
		
		//设置left join
		JoinCriteria join = TableJoinCriteria.leftJoinOn("AD_KEYWORD_REPORT t5", new String[]{"t5.AD_KEYWORDS_ID","t2.AD_KEYWORDS_ID"});
		 //join 的其它条件
		join.add(IbsRestrictions.fuzzy("t2.client_tag_id", "22", ">"));
		detachedIbtsCriteria.addJoinsClause(join);
		
		//设置in where
		detachedIbtsCriteria.add(IbsRestrictions.in("t2.CHANNEL_ACCOUNT_ID", new Integer[]{1,2,3}));
		//设置 not in
		detachedIbtsCriteria.add(IbsRestrictions.notIn("t2.CHANNEL_ACCOUNT_ID", new Integer[]{1,2,3}));
		//设置 between and
		detachedIbtsCriteria.add(IbsRestrictions.between("t2.CHANNEL_ACCOUNT_ID", 1, 2));
		//设置 not between and
		detachedIbtsCriteria.add(IbsRestrictions.notBetween("t2.CHANNEL_ACCOUNT_ID", 1, 2));
		
		//设置like lower(t2.NAME) like 'NAME' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.ilike("t2.NAME", "NAME"));
		//设置like lower(t2.NAME) like '%NAME' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.ilike("t2.NAME", "NAME", IbsMatchMode.START));
		//设置like lower(t2.NAME) like 'NAME%' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.ilike("t2.NAME", "NAME", IbsMatchMode.END));	
		//设置like lower(t2.NAME) like '%NAME%' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.ilike("t2.NAME", "NAME", IbsMatchMode.ANYWHERE));
		
		//设置 not like t2.NAME not like 'NAME' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.notLike("t2.NAME", "NAME", IbsMatchMode.EXACT));
		//设置not like t2.NAME not like '%NAME' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.notLike("t2.NAME", "NAME", IbsMatchMode.START));
		//设置not like t2.NAME not like 'NAME%' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.notLike("t2.NAME", "NAME", IbsMatchMode.END));	
		//设置not like t2.NAME not like '%NAME%' 不区分大小写
		detachedIbtsCriteria.add(IbsRestrictions.notLike("t2.NAME", "NAME", IbsMatchMode.ANYWHERE));
		
		//设置t2.NAME t2.NAME='NAME'
		detachedIbtsCriteria.add(IbsRestrictions.eq("t2.NAME", "NAME"));
		//设置t2.NAME IS NOT NULL
		detachedIbtsCriteria.add(IbsRestrictions.isNotNull("t2.NAME"));
		
		//设置t2.NAME IS  NULL
		detachedIbtsCriteria.add(IbsRestrictions.isNull("t2.NAME"));
		
		//设置t2.STATUS>2
		detachedIbtsCriteria.add(IbsRestrictions.gt("t2.STATUS", 2));
		
		//设置t2.STATUS<2
		detachedIbtsCriteria.add(IbsRestrictions.lt("t2.STATUS", 2));
		
		//设置t2.STATUS<>2
		detachedIbtsCriteria.add(IbsRestrictions.ne("t2.STATUS", 2));
		
		//设置t2.STATUS<=2
		detachedIbtsCriteria.add(IbsRestrictions.le("t2.STATUS", 2));
		
		//设置t2.STATUS>=2
		detachedIbtsCriteria.add(IbsRestrictions.ge("t2.STATUS", 2));
		
		
		//(A and B and C...) (t2.STATUS>=2 and t2.NAME IS NOT NULL)
		JunctionExpression junction = IbsRestrictions.conjunction();
		junction.add(IbsRestrictions.ge("t2.STATUS", 2));
		junction.add(IbsRestrictions.isNotNull("t2.NAME"));
		detachedIbtsCriteria.add(junction);
		
		//(A or B or C...) (t2.STATUS>=2 or t2.NAME IS NOT NULL)
		JunctionExpression disjunction = IbsRestrictions.disjunction();
		disjunction.add(IbsRestrictions.ge("t2.STATUS", 2));
		disjunction.add(IbsRestrictions.isNotNull("t2.NAME"));
		detachedIbtsCriteria.add(disjunction);
		
		//设置属性操作  t1.NAME=t2.NAME
		detachedIbtsCriteria.add(IbsRestrictions.eqProperty("t1.NAME", "t2.NAME"));
		
		//设置属性操作  t1.NAME>=t2.NAME
		detachedIbtsCriteria.add(IbsRestrictions.geProperty("t1.NAME", "t2.NAME"));
		
		//设置属性操作  t1.NAME<t2.NAME
		detachedIbtsCriteria.add(IbsRestrictions.ltProperty("t1.NAME", "t2.NAME"));
		
		//设置属性操作  t1.NAME>t2.NAME
		detachedIbtsCriteria.add(IbsRestrictions.gtProperty("t1.NAME", "t2.NAME"));
		
		//设置属性操作  t1.NAME<=t2.NAME
		detachedIbtsCriteria.add(IbsRestrictions.leProperty("t1.NAME", "t2.NAME"));
		
		//设置属性操作  t1.NAME<>t2.NAME
		detachedIbtsCriteria.add(IbsRestrictions.neProperty("t1.NAME", "t2.NAME"));
		
		/**
		 * 模糊设置 操作符不确定 支持  > >= <>  < <= like not like  in not in  
		 * 不支持 between not between  IS NULL IS NOT NULL
		 */
		detachedIbtsCriteria.add(IbsRestrictions.fuzzy("t2.client_tag_id", "22", ">"));
		
		/**
		 * group by
		 * group by t2.AD_KEYWORDS_ID,t2.CHANNEL_ID,t3.AD_GROUP_ID,t4.AD_CAMPAIGN_ID HIVEING RANKING>0
		 */
		GroupCriteria groupBy = GroupByCrieria.groupBy(new String[]{"t2.AD_KEYWORDS_ID,t2.CHANNEL_ID,t3.AD_GROUP_ID,t4.AD_CAMPAIGN_ID"});
		groupBy.add(IbsRestrictions.gt("RANKING", 0));
		detachedIbtsCriteria.addGroupByClause(groupBy);
		
		/**
		 * 设置order by
		 * order by RANKING ASC
		 */
		detachedIbtsCriteria.addOrder(IbsOrder.asc("RANKING"));
		
		/**
		 * startPos, pageSize
		 * limit 0,15
		 */
		detachedIbtsCriteria.addLimit(IbsRestrictions.limit(0, 15));
		
		
		DetachedHsCriteria mainCriteria = DetachedHsCriteria.forInstance();
		mainCriteria.addColumnName(AliasColumn.neAs("tt2.*"));
		mainCriteria.addFromClauseAlias(detachedIbtsCriteria.getCriteria(), "tt2");
		
		HsSqlText sqlTxt = mainCriteria.getHsSqlText();
		System.out.println(JSON.toJSONString(sqlTxt.getNewSql()));
		System.out.println(JSON.toJSONString(sqlTxt.getParameter()));
		//BaseJdbcClientDao jdbc = new BaseJdbcClientDao();
   	   // HBoundSql sqlText = jdbc.parseSqlText(sqlTxt.getNewSql(), sqlTxt.getParameter());
   	   // System.out.println(sqlText.getText());
   	   // System.out.println(JSON.toJSONString(jdbc.getParameterObjectValues(sqlText, sqlTxt.getParameter())));
	}
	
	
	public void testInsertSql(){
		
		DetachedHsCriteria detachedIbtsCriteria = DetachedHsCriteria.forInstance();
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t2.AD_KEYWORDS_ID,t2.KEYWORD_MAX_CPC as historyValue,t2.CHANNEL_ID"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t2.CHANNEL_ACCOUNT_ID,t2.MATCH_TYPE,t3.AD_GROUP_ID,t4.AD_CAMPAIGN_ID"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t4.name as AD_CAMPAIGN_NAME,t2.STATUS as historyStatus,t3.name as AD_GROUP_NAME"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t2.name as AD_KEYWORD_NAME"));
		detachedIbtsCriteria.addColumnName(AliasColumn.neAs("t5.ORDER_COUNT"));
		detachedIbtsCriteria.addColumnName(AliasColumn.as("CASE WHEN SUM(t5.ORDER_COUNT_NEW_CLIENT) = 0 THEN 0 ELSE CAST(ROUND(SUM(t5.COSTS)/SUM(t5.ORDER_COUNT_NEW_CLIENT),2) AS CHAR(50)) END","CPN"));
		detachedIbtsCriteria.addColumnName(AliasColumn.as("CASE WHEN SUM(t5.COSTS) = 0 THEN 0 ELSE CAST(ROUND(SUM(t5.SALES_VALUE)/SUM(t5.COSTS),2) AS CHAR(50)) END","ROI"));
		
		//设置from table
		detachedIbtsCriteria.addFromClause(TableFromCriteria.setTableName("AD_KEYWORD t2"));
		
		//设置left join
		JoinCriteria join = TableJoinCriteria.leftJoinOn("AD_KEYWORD_REPORT t5", new String[]{"t5.AD_KEYWORDS_ID","t2.AD_KEYWORDS_ID"});
		 //join 的其它条件
		join.add(IbsRestrictions.fuzzy("t2.client_tag_id", "22", ">"));
		detachedIbtsCriteria.addJoinsClause(join);
		
		DetachedIUCriteria insert = DetachedIUCriteria.forInstance(UIType.INSERT);
		insert.setTableName("CHANNEL_ACCOUNT_REPORT");
		insert.addColumnName(IUColumn.set("name", "colley"));
		insert.addColumnName(IUColumn.set("age", 11));
		insert.addColumnName(IUColumn.consts("createTime", "now()"));
		insert.addFromClause(detachedIbtsCriteria);
		HsSqlText sqlTxt = insert.getHsSqlText();
		
		HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sqlTxt);
		
		System.out.println(JSON.toJSONString(hsBoundSql.getSql()));
		System.out.println(JSON.toJSONString(hsBoundSql.getParamObjectValues()));
		//BaseJdbcClientDao jdbc = new BaseJdbcClientDao();
   	    //HBoundSql sqlText = jdbc.parseSqlText(sqlTxt.getNewSql(), sqlTxt.getParameter());
   	    //System.out.println(sqlText.getText());
   	   // System.out.println(JSON.toJSONString(jdbc.getParameterObjectValues(sqlText, sqlTxt.getParameter())));
   	    
   	    
   	    DetachedIUCriteria update = DetachedIUCriteria.forInstance(UIType.UDAPTE);
   	    update.setTableName("CHANNEL_ACCOUNT_REPORT");
   	    update.addColumnName(IUColumn.set("name", "colley"));
   	    update.addColumnName(IUColumn.set("age", 11));
   	    update.addColumnName(IUColumn.consts("createTime", "now()"));
   	    update.add(IbsRestrictions.eq("name", "张三"));
   	 update.add(IbsRestrictions.eq("name", "张三1"));
   	     sqlTxt = update.getHsSqlText();
   	   hsBoundSql = HBoundSqlBuilder.parseSql(sqlTxt);
		
		System.out.println(JSON.toJSONString(hsBoundSql.getSql()));
		System.out.println(JSON.toJSONString(hsBoundSql.getParamObjectValues()));
		 //jdbc = new BaseJdbcClientDao();
	     //sqlText = jdbc.parseSqlText(sqlTxt.getNewSql(), sqlTxt.getParameter());
	    //System.out.println(sqlText.getText());
	    //System.out.println(JSON.toJSONString(jdbc.getParameterObjectValues(sqlText, sqlTxt.getParameter())));
	}
	
	
	public void testAllOr(){
		DetachedHsCriteria detachedHsCriteria = DetachedHsCriteria.forInstance();
		detachedHsCriteria.addColumnName(AliasColumn.neAs("AD_KEYWORDS_ID,COUNT(*) as NUM"));
		detachedHsCriteria.addFromClause(TableFromCriteria.setTableName("AD_KEYWORD_REPORT_EXTEND"));
		detachedHsCriteria.add(IbsRestrictions.eq("date(DATA_END_TIME)", 123));
		detachedHsCriteria.add(IbsRestrictions.isNull("TYPE"));
		JunctionExpression junctionExpression = IbsRestrictions.disjunction();
		List<Integer> adKeywordsIds = new ArrayList<Integer>();
		adKeywordsIds.add(1);
		adKeywordsIds.add(2);
		adKeywordsIds.add(3);
		adKeywordsIds.add(4);
		adKeywordsIds.add(5);
		for (int i = 0; i < adKeywordsIds.size(); i++) {
			Integer keywordId = adKeywordsIds.get(i);
			junctionExpression.add(IbsRestrictions.eq("AD_KEYWORDS_ID", keywordId));
		}
		detachedHsCriteria.add(junctionExpression);
		detachedHsCriteria.addGroupByClause(GroupByCrieria.groupBy(new String[]{"AD_KEYWORDS_ID"}));
		HsSqlText sqlTxt = detachedHsCriteria.getHsSqlText();
		HsBoundSql hsBoundSql = HBoundSqlBuilder.parseSql(sqlTxt);
		
		System.out.println(JSON.toJSONString(hsBoundSql.getSql()));
		System.out.println(JSON.toJSONString(hsBoundSql.getParamObjectValues()));
			//BaseJdbcClientDao jdbc = new BaseJdbcClientDao();
			//HBoundSql sqlText = jdbc.parseSqlText(sqlTxt.getNewSql(), sqlTxt.getParameter());
		    //System.out.println(sqlText.getText());
		    //System.out.println(JSON.toJSONString(jdbc.getParameterObjectValues(sqlText, sqlTxt.getParameter())));
	}
	