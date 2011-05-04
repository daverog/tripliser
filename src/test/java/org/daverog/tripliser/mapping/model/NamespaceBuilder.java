package org.daverog.tripliser.mapping.model;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

import org.daverog.tripliser.mapping.model.Namespace;

public class NamespaceBuilder {

	private String prefix;
	private String url;
	private boolean defaultNamespace = false;

	public NamespaceBuilder prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public NamespaceBuilder url(String url) {
		this.url = url;
		return this;
	}

	public Namespace toNamespace() {
		Namespace namespace = new Namespace();
		setInternalState(namespace, "prefix", prefix);
		setInternalState(namespace, "url", url);
		setInternalState(namespace, "defaultNamespace", defaultNamespace);
		return namespace;
	}

	public NamespaceBuilder defaultNamespace(boolean defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
		return this;
	}

}
