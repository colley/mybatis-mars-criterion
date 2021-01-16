/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-3-11 下午7:45
 * History:
 */
package com.mars.kit.criterion.model;

import java.util.List;

import com.mars.kit.criterion.common.GetterHelper;


/**
 * CouponListPager.java
 *
 * @author ColleyMa
 * @version 19-3-11 下午7:45
*/
public class ListPager<T> implements java.io.Serializable {
    private static final long serialVersionUID = 2449948412115458731L;
    private Integer size;
    private Integer pageSize;
    private Integer totalPage;
    private List<T> item;
    private int pageIndex;
    private int startIndex;
    private int endIndex;

    public ListPager(List<T> item, Integer pageSize) {
    	this.item = item;
        this.size = item.size();
        this.pageSize = pageSize;
        totalPage = size / pageSize;
        this.totalPage = totalPage + (((size % pageSize) > 0) ? 1 : 0);
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getPageList() {
        this.startIndex = Math.min(getFirstResult(), size);
        this.endIndex = Math.min(startIndex + pageSize, size);

        return GetterHelper.split(item, startIndex, endIndex);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public List<T> getItem() {
        return item;
    }

    public ListPager<T> setItem(List<T> item) {
        this.item = item;

        return this;
    }

    public int getFirstResult() {
        return (((pageIndex - 1) * pageSize) > 0) ? ((pageIndex - 1) * pageSize) : 0;
    }

    public int getStartIndex() {
        return startIndex;
    }
}
