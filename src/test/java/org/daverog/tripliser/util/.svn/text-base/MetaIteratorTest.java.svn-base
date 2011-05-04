package org.daverog.tripliser.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.util.MetaIterator;
import org.junit.Test;


public class MetaIteratorTest extends MockitoTestBase {
	
	@Test
	public void whenTheUnderlyingIteratorHasNoItemsTheMetaIteratorHasNoItems() {
		List<Iterator<String>> strings = new ArrayList<Iterator<String>>();
		
		strings.add(new ArrayList<String>().iterator());
		
		MetaIteratorOfStrings metaIteratorOfStrings = new MetaIteratorOfStrings(strings); 
		assertFalse(metaIteratorOfStrings.hasNext());
	}
	
	@Test
	public void whenTheUnderlyingIteratorHasOneItemTheMetaIteratorHasOneItem() {
		List<Iterator<String>> strings = new ArrayList<Iterator<String>>();
		
		strings.add(Arrays.asList("a").iterator());
		
		MetaIteratorOfStrings metaIteratorOfStrings = new MetaIteratorOfStrings(strings); 

		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("a", metaIteratorOfStrings.next());
		assertFalse(metaIteratorOfStrings.hasNext());
	}
	
	@Test
	public void whenTheUnderlyingIteratorHasTwoItemsTheMetaIteratorHasTwoItems() {
		List<Iterator<String>> strings = new ArrayList<Iterator<String>>();
		
		strings.add(Arrays.asList("a", "b").iterator());
		
		MetaIteratorOfStrings metaIteratorOfStrings = new MetaIteratorOfStrings(strings); 
		
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("a", metaIteratorOfStrings.next());
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("b", metaIteratorOfStrings.next());
		assertFalse(metaIteratorOfStrings.hasNext());
	}
	
	@Test
	public void whenTwoUnderlyingIteratorsHaveOneItemEachTheMetaIteratorHasTwoItems() {
		List<Iterator<String>> strings = new ArrayList<Iterator<String>>();
		
		strings.add(Arrays.asList("a").iterator());
		strings.add(Arrays.asList("b").iterator());
		
		MetaIteratorOfStrings metaIteratorOfStrings = new MetaIteratorOfStrings(strings); 
		
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("a", metaIteratorOfStrings.next());
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("b", metaIteratorOfStrings.next());
		assertFalse(metaIteratorOfStrings.hasNext());
	}
	
	@Test
	public void whenTwoUnderlyingIteratorsHaveTwoItemsEachTheMetaIteratorHasFourItems() {
		List<Iterator<String>> strings = new ArrayList<Iterator<String>>();
		
		strings.add(Arrays.asList("a", "b").iterator());
		strings.add(Arrays.asList("c", "d").iterator());
		
		MetaIteratorOfStrings metaIteratorOfStrings = new MetaIteratorOfStrings(strings); 
		
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("a", metaIteratorOfStrings.next());
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("b", metaIteratorOfStrings.next());
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("c", metaIteratorOfStrings.next());
		assertTrue(metaIteratorOfStrings.hasNext());
		assertEquals("d", metaIteratorOfStrings.next());
		assertFalse(metaIteratorOfStrings.hasNext());
	}
	
	public class MetaIteratorOfStrings extends MetaIterator<String> {

		private Iterator<Iterator<String>> strings;
		
		public MetaIteratorOfStrings(List<Iterator<String>> strings) {
			this.strings = strings.iterator();
		}

		@Override
		public boolean hasNextNestedIterator() {
			return strings.hasNext();
		}

		@Override
		public Iterator<String> nextNestedIterator() {
			return strings.next();
		}
		
	}

}
