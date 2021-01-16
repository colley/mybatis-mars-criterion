package com.mars.kit.criterion.model;

import java.io.Serializable;


public class PaginationParam implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3116388844335516854L;
	private int pageIndex;
    private int pageSize = 10;
    private int totalRows;

    public PaginationParam() {
    }

    public PaginationParam(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRows() {
        return this.totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalPages() {
        return new Integer(new Double(Math.ceil(new Double(this.totalRows).doubleValue() / new Double(this.pageSize).doubleValue())).intValue()).intValue();
    }

    public int getFirstResult() {
        return (((this.pageIndex - 1) * this.pageSize) > 0) ? ((this.pageIndex - 1) * this.pageSize) : 0;
    }
}
