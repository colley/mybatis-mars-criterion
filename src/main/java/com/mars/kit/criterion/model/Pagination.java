package com.mars.kit.criterion.model;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;


/**
 * @FileName  Pagination.java
 * @author colley
 * @param <T> 
 */
public class Pagination<T> implements java.io.Serializable {
    /**
    *
    */
    private static final long serialVersionUID = 2655888519198509118L;
    private List<T> rows;
    private int currPage;
    private int pageSize = 10;
    private int totalRows;

    public Pagination() {
    }

    public Pagination(int currPage, int pageSize) {
        this.currPage = currPage;
        this.pageSize = pageSize;
    }
    
    public Pagination(List<T> rows, int totalCount, int pageSize, int currPage) {
        this.rows = rows;
        this.totalRows = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
    }

	public List<T> getRows() {
		return Optional.ofNullable(rows).orElse(Lists.newArrayList());
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	
	 public int getTotalPages() {
	        return new Integer((new Double(Math.ceil(new Double(totalRows) / new Double(pageSize)))).intValue());
	    }
}
