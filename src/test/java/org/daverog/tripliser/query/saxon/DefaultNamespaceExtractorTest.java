package org.daverog.tripliser.query.saxon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.daverog.tripliser.query.saxon.DefaultNamespaceExtractor;
import org.junit.Test;

public class DefaultNamespaceExtractorTest {
	
	@Test
	public void aStringContainingADefaultNamespaceDeclarationReturnsTheContainingUrl(){
		String sourceXml = "<bbc xmlns=\"http://bbc.co.uk\"></bbc>";
		assertEquals("http://bbc.co.uk", DefaultNamespaceExtractor.extract(sourceXml ));
	}
	
	@Test
	public void aStringNotContainingADefaultNamespaceDeclarationReturnsNull(){
		String sourceXml = "<bbc xmlns=http://bbc.co.uk\"></bbc>";
		assertNull(DefaultNamespaceExtractor.extract(sourceXml ));
	}

}
