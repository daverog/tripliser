package org.daverog.mockito;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.internal.verification.api.VerificationMode;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.stubbing.Stubber;

public class MockitoTestBase {
	
	@Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
	
    public <T> OngoingStubbing<T> when(T methodCall) {
        return Mockito.when(methodCall);
    }
    
    public <T> T mock(Class<T> clazz) {
    	return Mockito.mock(clazz);
    }

    public <T> T verify(T mock) {
        return Mockito.verify(mock);
    }

    public <T> T verify(T mock, VerificationMode mode) {
        return Mockito.verify(mock, mode);
    }

    public void assertNotSame(Object a, Object b) {
        Assert.assertNotSame(a, b);
    }

    public void assertEquals(Object a, Object b) {
        Assert.assertEquals(a, b);
    }

    public void assertEquals(double a, double b, double c) {
        Assert.assertEquals(a, b, c);
    }

    public VerificationMode times(int wantedNumberOfInvocations) {
        return Mockito.times(wantedNumberOfInvocations);
    }

    @SuppressWarnings("unchecked")
    public <T> T anyObject() {
        return (T) Matchers.anyObject();
    }
    
    public <T> T any(Class<T> clazz) {
    	return (T) Matchers.any(clazz);
    }

    public <T> T eq(T mock) {
        return Matchers.eq(mock);
    }

    public Object isNull() {
        return Matchers.isNull();
    }

    public int anyInt() {
        return Matchers.anyInt();
    }

    public boolean anyBoolean() {
        return Matchers.anyBoolean();
    }

    public double anyDouble() {
        return Matchers.anyDouble();
    }

    public Date anyDate() {
        return Matchers.any(Date.class);
    }

    @SuppressWarnings("rawtypes")
	public List anyList() {
        return Matchers.anyList();
    }

    @SuppressWarnings("rawtypes")
	public Set anySet() {
        return Matchers.anySet();
    }

    @SuppressWarnings("rawtypes")
	public Map anyMap() {
        return Matchers.anyMap();
    }

    @SuppressWarnings("rawtypes")
	public Collection anyCollection() {
        return Matchers.anyCollection();
    }

    public String anyString() {
        return Matchers.anyString();
    }

    public InputStream anyInputStream() {
        return Matchers.any(InputStream.class);
    }

    public URI anyURI() {
        return Matchers.any(URI.class);
    }

    public <T> T argThat(ArgumentMatcher<T> matcher) {
        return Matchers.argThat(matcher);
    }
    
    public OutputStream anyOutputStream() {
        return Matchers.any(OutputStream.class);
    }

    public void fail(String message) {
        Assert.fail(message);
    }

    public void message(String message){
        System.out.println(message);
    }

    public void fail() {
        Assert.fail();
    }

    public <T> void assertNull(T a) {
        Assert.assertNull(a);
    }

    public <T> void assertNotNull(T a) {
        Assert.assertNotNull(a);
    }

    public void assertTrue(boolean a) {
        Assert.assertTrue(a);
    }

    public void assertTrue(String message, boolean a) {
        Assert.assertTrue(message, a);
    }

    public void assertFalse(boolean a) {
        Assert.assertFalse(a);
    }

    public void assertFalse(String message, boolean a) {
        Assert.assertFalse(message, a);
    }

    public VerificationMode never() {
        return Mockito.never();
    }

    public void assertSame(Object a, Object b) {
        Assert.assertSame(a, b);
    }

    public void verifyZeroInteractions(Object... mocks) {
        Mockito.verifyZeroInteractions(mocks);
    }

    public Stubber doThrow(Throwable toBeThrown) {
        return Mockito.doThrow(toBeThrown);
    }

    @SuppressWarnings("rawtypes")
	public Stubber doAnswer(Answer answer) {
        return Mockito.doAnswer(answer);
    }

    public void exceptionExpected(){
        fail("An exception should have been thrown");
    }
    
    public void setInternalState(Object target, String field, Object value){
    	Whitebox.setInternalState(target, field, value);
    }
    
    public void getInternalState(Object target, String field){
    	Whitebox.getInternalState(target, field);
    }

}
