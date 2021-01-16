package com.mars.kit.criterion;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mars.kit.criterion.common.HsSqlText;
import com.mars.kit.criterion.common.UIType;
import com.mars.kit.criterion.expression.Criterion;
import com.mars.kit.criterion.model.PaginationParam;
import com.mars.kit.criterion.sql.AliasColumn;
import com.mars.kit.criterion.sql.DeleteCriteria;
import com.mars.kit.criterion.sql.GroupCriteria;
import com.mars.kit.criterion.sql.HsCriteria;
import com.mars.kit.criterion.sql.IbatisSelect;
import com.mars.kit.criterion.sql.IbsOrder;
import com.mars.kit.criterion.sql.JoinCriteria;
import com.mars.kit.criterion.sql.SqlFromCriteria;
import com.mars.kit.criterion.sql.SqlJoinCriteria;
import com.mars.kit.criterion.sql.TableJoinCriteria;


public class DetachedHsCriteria implements Serializable {
    private static final long serialVersionUID = 2860555967858668492L;
    protected final static Log logger = LogFactory.getLog(DetachedHsCriteria.class);
    protected final static String HS_IBATISCRITERIA = "Ibatis_QueryByCriteriaSelect";
    private final HsCriteria criteria;
    private CriterionQuery criterionQuery; 
    private String resultMappingId;
    private PaginationParam pagination;

	protected DetachedHsCriteria(UIType type) {
		HsCriteria _criteria = null;
		if (UIType.SELECT.equals(type)) {
			_criteria = new IbatisSelect();
		}

		if (UIType.SUB_SELECT.equals(type)) {
			_criteria = new SqlFromCriteria();
		}

		if (UIType.DELETE.equals(type)) {
			_criteria = new DeleteCriteria();
		}

		this.criteria = _criteria;
		this.criterionQuery = new CriterionQueryTranslator();
	}
    

    public static DetachedHsCriteria forInstance() {
        return new DetachedHsCriteria(UIType.SELECT);
    }

    public static DetachedHsCriteria forInstance(DetachedHsCriteria subCriteria) {
        return new DetachedHsCriteria(UIType.SELECT);
    }

    public static DetachedHsCriteria deleteInstance() {
        return new DetachedHsCriteria(UIType.DELETE);
    }
    
    public static DetachedHsCriteria subCriteriaInstance() {
        return new DetachedHsCriteria(UIType.SUB_SELECT);
    }

    public DetachedHsCriteria addColumnNames(Map<String, String> resultmapping) {
        if (MapUtils.isNotEmpty(resultmapping)) {
            for (Map.Entry<String, String> entry : resultmapping.entrySet()) {
                String displayName = entry.getKey();
                String columnName = entry.getValue();
                criteria.addColumn(columnName + " AS " + displayName);
            }
        }

        return this;
    }

    public DetachedHsCriteria addColumnName(AliasColumn aliasColumn) {
        criteria.addColumn(aliasColumn.getColumnName());

        return this;
    }

    public DetachedHsCriteria addColumnName(AliasColumn[] aliasColumns) {
        if (ArrayUtils.isNotEmpty(aliasColumns)) {
            for (AliasColumn alias : aliasColumns) {
                criteria.addColumn(alias.getColumnName());
            }
        }

        return this;
    }

    public DetachedHsCriteria addColumnName(String[] columnNames) {
        criteria.addColumn(columnNames);

        return this;
    }

    public DetachedHsCriteria addColumnName(List<String> columnNames) {
        criteria.addColumn(columnNames);

        return this;
    }

    public DetachedHsCriteria addFromClause(HsCriteria fromCriteria) {
        criteria.addFromClause(fromCriteria);

        return this;
    }

    public DetachedHsCriteria addFromClauseAlias(HsCriteria fromCriteria, String aliasTableName) {
        criteria.addFromClause(fromCriteria, aliasTableName);

        return this;
    }

    public DetachedHsCriteria add(Criterion criterion) {
        criteria.add(criterion);

        return this;
    }

    public DetachedHsCriteria add(List<Criterion> criterions) {
        if (CollectionUtils.isNotEmpty(criterions)) {
            for (Criterion criterion : criterions) {
                if (criterion != null) {
                    criteria.add(criterion);
                }
            }
        }

        return this;
    }

    public GroupCriteria addGroupByClause(GroupCriteria groupByClause) {
        criteria.addGroupBy(groupByClause);

        return groupByClause;
    }

    public JoinCriteria addJoinsClause(JoinCriteria joinCriteria) {
        criteria.addFromJoins(joinCriteria);

        return joinCriteria;
    }

    public DetachedHsCriteria leftJoinOn(String tabelName, String[] onClasuse) {
        criteria.addFromJoins(TableJoinCriteria.leftJoinOn(tabelName, onClasuse));

        return this;
    }

    public DetachedHsCriteria rightJoinOn(String tabelName, String[] onClasuse) {
        criteria.addFromJoins(TableJoinCriteria.rightJoinOn(tabelName, onClasuse));

        return this;
    }

    public DetachedHsCriteria joinOn(String tabelName, String[] onClasuse) {
        criteria.addFromJoins(TableJoinCriteria.joinOn(tabelName, onClasuse));

        return this;
    }

    public DetachedHsCriteria leftJoinOn(HsCriteria sqlCriteria, String aliasTabelName, String[] onClasuse) {
        criteria.addFromJoins(SqlJoinCriteria.leftJoinOn(aliasTabelName, sqlCriteria, onClasuse));

        return this;
    }

    public DetachedHsCriteria rightJoinOn(HsCriteria sqlCriteria, String aliasTabelName, String[] onClasuse) {
        criteria.addFromJoins(SqlJoinCriteria.rightJoinOn(aliasTabelName, sqlCriteria, onClasuse));

        return this;
    }

    public DetachedHsCriteria joinOn(HsCriteria sqlCriteria, String aliasTabelName, String[] onClasuse) {
        criteria.addFromJoins(SqlJoinCriteria.joinOn(aliasTabelName, sqlCriteria, onClasuse));

        return this;
    }

    public DetachedHsCriteria setResultMappingId(String namespace, String mappingId) {
        if (StringUtils.isNotEmpty(namespace)) {
            this.resultMappingId = namespace + "." + mappingId;
        } else {
            this.resultMappingId = mappingId;
        }

        return this;
    }

    public DetachedHsCriteria addOrder(IbsOrder order) {
        criteria.addOrder(order);

        return this;
    }

    public DetachedHsCriteria addLimit(Criterion criterion) {
        criteria.addPagingLimit(criterion);

        return this;
    }

    public HsSqlText getHsSqlText() {
        String newSql = criteria.getSqlString(criterionQuery);
        Object parameter = criterionQuery.getParameter();

        return new HsSqlText(newSql, parameter);
    }

    public HsCriteria getCriteria() {
        return criteria;
    }

    public HsCriteria getFromCriteria() {
        return criteria;
    }

    public String getResultMappingId() {
        return StringUtils.isEmpty(resultMappingId) ? "defaultMapping" : resultMappingId;
    }

    public String getDynamicQueryByCriteria() {
        return HS_IBATISCRITERIA;
    }

    public PaginationParam getPagination() {
        return this.pagination;
    }

    public void setPagination(PaginationParam pagination) {
        this.pagination = pagination;
    }

    public DetachedHsCriteria addPagination(int pageIndex, int pageSize) {
        if (this.pagination == null) {
            this.pagination = new PaginationParam(pageIndex, pageSize);
        } else {
            this.pagination.setPageIndex(pageIndex);
            this.pagination.setPageSize(pageSize);
        }

        return this;
    }
}
