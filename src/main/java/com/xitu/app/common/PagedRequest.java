package com.xitu.app.common;

public class PagedRequest extends BaseRequest {

	private static final long serialVersionUID = 1L;

	/** 是否分页，默认true*/
	private boolean paged = true;

	/** 分页，页大小 */
	private Integer pageSize = 20;

	/** 分页，页序号 */
	private Integer pageIndex = 1;

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public boolean isPaged() {
		return paged;
	}

	public void setPaged(boolean paged) {
		this.paged = paged;
	}

}
