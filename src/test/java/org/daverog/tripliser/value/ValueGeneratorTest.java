package org.daverog.tripliser.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.daverog.tripliser.graphs.GraphContext;
import org.daverog.tripliser.graphs.GraphContextBuilder;
import org.daverog.tripliser.mapping.model.ValueMapping;
import org.daverog.tripliser.mapping.model.ValueMappingBuilder;
import org.daverog.tripliser.report.TripliserReporter;
import org.daverog.tripliser.value.ValueGenerator;
import org.daverog.tripliser.value.ValueValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ValueGeneratorTest {
	
	private ValueGenerator valueGenerator;
	
	@Mock TripliserReporter tripliserReporter;

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		
		valueGenerator = new ValueGenerator();
	}
	
	@Test
	public void aRawValueResultsInTheSameValueForASimpleValueMapping() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("text").toValueMapping();
		
		assertEquals("text", valueGenerator.getValue("text", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueResultsInAPrependedValueForAPrependedValueMapping() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("text").prepend("pre").toValueMapping();
		
		assertEquals("pretext", valueGenerator.getValue("text", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueResultsInAnAppendedValueForAnAppendedValueMapping() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("text").append("post").toValueMapping();
		
		assertEquals("textpost", valueGenerator.getValue("text", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueResultsInPrependedAndAppendedValueForAPrependedAndAppendedValueMapping() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("text").prepend("pre").append("post").toValueMapping();
		
		assertEquals("pretextpost", valueGenerator.getValue("text", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueHasAConstantConvertedIntoItsValue() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("${constant}").toValueMapping();
		
		assertEquals("constantValue", valueGenerator.getValue("${constant}", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueHasATwoConstantsConvertedIntoTheirValues() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.addConstant("constant2", "constantValue2")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("${constant}${constant2}").toValueMapping();
		
		assertEquals("constantValueconstantValue2", valueGenerator.getValue("${constant}${constant2}", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueHasAConstantConvertedIntoItsValueWithSurroundingText() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("abc${constant}def").toValueMapping();
		
		assertEquals("abcconstantValuedef", valueGenerator.getValue("abc${constant}def", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueHasAConstantConvertedIntoItsValueInThePrepend() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("a").prepend("${constant}").toValueMapping();
		
		assertEquals("constantValuea", valueGenerator.getValue("a", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueHasAConstantConvertedIntoItsValueInTheAppend() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("a").append("${constant}").toValueMapping();
		
		assertEquals("aconstantValue", valueGenerator.getValue("a", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueDoesNotHaveAMalformedConstantConvertedIntoItsValue() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("${constant").toValueMapping();
		
		assertEquals("${constant", valueGenerator.getValue("${constant", valueMapping, graphContext));
	}
	
	@Test
	public void aRawValueDoesNotHaveAnotherMalformedConstantConvertedIntoItsValue() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("$constant}").toValueMapping();
		
		assertEquals("$constant}", valueGenerator.getValue("$constant}", valueMapping, graphContext));
	}
	
	@Test
	public void throwsAnValueValidationExceptionIfRawValueIsBlank() {
		GraphContext graphContext = new GraphContextBuilder()
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().value("").toValueMapping();
		
		try {
			valueGenerator.getValue("", valueMapping, graphContext);
			fail("Exception should have been thrown");
		} catch(ValueValidationException e) {
			assertEquals("A value cannot be empty", e.getMessage());
		}
	}
	
	@Test
	public void doesNotConvertConstantsForAXPathMapping() throws ValueValidationException   {
		GraphContext graphContext = new GraphContextBuilder()
			.addConstant("constant", "constantValue")
			.toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().query("${constant}").toValueMapping();
		
		assertEquals("${constant}", valueGenerator.getValue("${constant}", valueMapping, graphContext));
	}
		
	@Test
	public void throwsAValueValidationExceptionWhenAValidationRegexIsSuppliedAndTheRawValueDoesNotMatch() {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().validationRegex("[0-9]*").query("abc").toValueMapping();
		
		try {
			valueGenerator.getValue("invalidForRegex", valueMapping, graphContext);
			fail("Exception should have been thrown");
		} catch(ValueValidationException e) {
			assertEquals("The value 'invalidForRegex' does not match the regular expression '[0-9]*'", e.getMessage());
		}
	}
	
	@Test
	public void doesNotThrowsAValueValidationExceptionWhenAValidationRegexIsSuppliedAndTheRawValueMatches() throws ValueValidationException {
		GraphContext graphContext = new GraphContextBuilder().toGraphContext();
		ValueMapping valueMapping = new ValueMappingBuilder().validationRegex("[0-9]*").query("abc").toValueMapping();
		
		valueGenerator.getValue("3464256", valueMapping, graphContext);
	}
	
}
