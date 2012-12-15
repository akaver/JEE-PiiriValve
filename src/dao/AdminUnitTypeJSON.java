package dao;

import java.util.Collection;

public class AdminUnitTypeJSON {
	private String text;
	private Boolean expanded;
	private Collection<AdminUnitTypeJSON> children;
	private String id;
	private Boolean hasChildren;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public Collection<AdminUnitTypeJSON> getChildren() {
		return children;
	}
	public void setChildren(Collection<AdminUnitTypeJSON> children) {
		this.children = children;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
}
