package org.daverog.tripliser.mapping.model;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import org.daverog.tripliser.mapping.model.Tag;

public class TagBuilder {

	private boolean required;
	private String name;
	private String query;

	public TagBuilder() {
		required = false;
		name = "name";
		query = "query";
	}
	
	public TagBuilder required(boolean required) {
		this.required = required;
		return this;
	}

	public Tag toTag() {
		Tag tag = new Tag();
		setInternalState(tag, "name", name);
		setInternalState(tag, "query", query);
		setInternalState(tag, "required", required);
		return tag;
	}

	public TagBuilder query(String query) {
		this.query = query;
		return this;
	}

}
