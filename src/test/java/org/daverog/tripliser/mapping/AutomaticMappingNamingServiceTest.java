package org.daverog.tripliser.mapping;

import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.mapping.AutomaticMappingNamingService;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.GraphMappingBuilder;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.MappingBuilder;
import org.daverog.tripliser.mapping.model.ResourceMapping;
import org.junit.Before;
import org.junit.Test;



public class AutomaticMappingNamingServiceTest extends MockitoTestBase {

	private AutomaticMappingNamingService automaticMappingNamingService;

	@Before
	public void setUp(){
		automaticMappingNamingService = new AutomaticMappingNamingService();
	}

	@Test
	public void providesANameForAllUnamedGraphMappings() throws TripliserException{
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder().name(null).toGraphMapping())
			.addGraphMapping(new GraphMappingBuilder().name("hasAName").toGraphMapping())
			.addGraphMapping(new GraphMappingBuilder().name(null).toGraphMapping())
			.toMapping();

		automaticMappingNamingService.provideNamesForUnamedMappings(mapping);

		List<GraphMapping> graphMappings = mapping.getGraphMappings();

		assertEquals("graph1", graphMappings.get(0).getName());
		assertEquals("hasAName", graphMappings.get(1).getName());
		assertEquals("graph2", graphMappings.get(2).getName());
	}

	@Test
	public void providesANameForAllUnamedResourceMappings() throws TripliserException{
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder()
				.name("hasAGraphMappingName")
				.addProperty(null, "name", "query")
				.addProperty("hasAName", "name", "query")
				.toGraphMapping())
			.toMapping();

		automaticMappingNamingService.provideNamesForUnamedMappings(mapping);

		List<ResourceMapping> resourceMappings = mapping.getGraphMappings().get(0).getResourceMappings();

		assertEquals("resource1", resourceMappings.get(0).getName());
		assertEquals("hasAName", resourceMappings.get(1).getName());
	}

	@Test
	public void ifAAutomaticNameIsAlreadyUsedItIsNotUsedAgain() throws TripliserException{
		Mapping mapping = new MappingBuilder()
			.addGraphMapping(new GraphMappingBuilder().name("graph1").toGraphMapping())
			.addGraphMapping(new GraphMappingBuilder().name(null).toGraphMapping())
			.toMapping();

		automaticMappingNamingService.provideNamesForUnamedMappings(mapping);

		List<GraphMapping> graphMappings = mapping.getGraphMappings();

		assertEquals("graph2", graphMappings.get(1).getName());
	}

}
