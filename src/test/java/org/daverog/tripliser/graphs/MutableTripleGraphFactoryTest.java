package org.daverog.tripliser.graphs;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.JenaModelService;
import org.daverog.tripliser.graphs.MutableTripleGraphFactory;
import org.daverog.tripliser.query.QueryService;
import org.daverog.tripliser.value.ItemToStringConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


import com.hp.hpl.jena.rdf.model.Model;

public class MutableTripleGraphFactoryTest extends MockitoTestBase {
	
	@Mock JenaModelService jenaModelService;
	@Mock QueryService queryService;
	@Mock ItemToStringConverter itemToStringConverter;
	@Mock GraphContext graphContext;

	private MutableTripleGraphFactory mutableTripleGraphFactory;
	
	@Before
	public void setUp() {
		mutableTripleGraphFactory = new MutableTripleGraphFactory(jenaModelService, queryService, itemToStringConverter);
	}
	
	@Test
	public void loadsNamespacesIntoTheJenaModel(){
		mutableTripleGraphFactory.createGraph("name", null, graphContext);
		
		verify(jenaModelService).loadNamespacesIntoModel(eq(graphContext), any(Model.class));
	}

}
