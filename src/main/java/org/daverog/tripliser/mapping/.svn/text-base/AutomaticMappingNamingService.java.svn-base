package org.daverog.tripliser.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.mapping.model.GraphMapping;
import org.daverog.tripliser.mapping.model.Mapping;
import org.daverog.tripliser.mapping.model.ResourceMapping;


public class AutomaticMappingNamingService {

	private int currentGraphIndex;
	private int currentResourceIndex;

	public AutomaticMappingNamingService(){
		currentGraphIndex = 1;
		currentResourceIndex = 1;
	}

	public void provideNamesForUnamedMappings(Mapping mapping) throws TripliserException {
		Set<String> usedNames = new HashSet<String>();

		List<GraphMapping> graphMappings = mapping.getGraphMappings();

		for (GraphMapping graphMapping : graphMappings) {
			if (graphMapping.getName() == null) {
				graphMapping.setName(createNewGraphName("graph", usedNames));
			} else {
				usedNames.add(graphMapping.getName());
			}

			List<ResourceMapping> resourceMappings = graphMapping.getResourceMappings();
			for (ResourceMapping resourceMapping : resourceMappings) {
				if (resourceMapping.getName() == null) {
					resourceMapping.setName(createNewResourceName("resource", usedNames));
				} else {
					usedNames.add(resourceMapping.getName());
				}
			}
		}
	}

	private String createNewGraphName(String prefix, Set<String> usedNames) {
		String name = "graph" + currentGraphIndex++;
		if (usedNames.contains(name)) return createNewGraphName(prefix, usedNames);
		return name;
	}

	private String createNewResourceName(String prefix, Set<String> usedNames) {
		String name = "resource" + currentResourceIndex++;
		if (usedNames.contains(name)) return createNewResourceName(prefix, usedNames);
		return name;
	}

}
