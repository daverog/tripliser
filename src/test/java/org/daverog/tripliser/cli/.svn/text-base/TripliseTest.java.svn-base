package org.daverog.tripliser.cli;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daverog.mockito.MockitoTestBase;
import org.daverog.tripliser.Constants;
import org.daverog.tripliser.Tripliser;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.Constants.Scope;
import org.daverog.tripliser.cli.FileService;
import org.daverog.tripliser.cli.Triplise;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.TripleGraph;
import org.daverog.tripliser.report.ReportEntry;
import org.daverog.tripliser.report.TripliserReport;
import org.daverog.tripliser.report.ReportEntry.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;



public class TripliseTest extends MockitoTestBase {

	@Mock TripliserFactory tripliserFactory;
	@Mock Tripliser tripliser;
	@Mock TripleGraph tripleGraph;
	@Mock FileService fileService;
	@Mock InputStream input;
	@Mock InputStream mapping;


	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errStream = new ByteArrayOutputStream();;

	private PrintStream out = new PrintStream(outStream);
	private PrintStream err = new PrintStream(errStream);

	private Triplise triplise;
	private TripliserReport report;

	private String mimeType;

	@Before
	public void setUp() throws IOException, TripliserException {
		triplise = new Triplise(fileService, tripliserFactory);

		report = new TripliserReport() {

			@Override
			public void writeXml(OutputStream stream) throws IOException {
				stream.write("XML Report output".getBytes());
			}

			@Override
			public void writeText(PrintStream out) {
				out.println("Report output");
			}

			@Override
			public String toString() {
				return "Report output";
			}

			@Override
			public boolean isSuccess() {
				return false;
			}

			@Override
			public Status getStatus() {
				return null;
			}

			@Override
			public int getNumberOfEntries(Status status, Scope scope) {
				return 0;
			}

			@Override
			public int getNumberOfEntries(Status status) {
				return 0;
			}

			@Override
			public int getNumberOfEntries(Scope scope) {
				return 0;
			}

			@Override
			public int getNumberOfEntries() {
				return 0;
			}

			@Override
			public Iterator<ReportEntry> getEntries() {
				return null;
			}
		};

		mimeType = Constants.MIME_TYPE_APPLICATION_RDF_XML;

		when(fileService.loadFile("input.xml")).thenReturn(input);
		when(fileService.loadFile("mapping.xml")).thenReturn(mapping);
		when(tripliserFactory.create()).thenReturn(tripliser);
		when(tripliserFactory.setMapping(mapping)).thenReturn(tripliserFactory);
		when(tripliser.generateTripleGraph()).thenReturn(tripleGraph);
		when(tripleGraph.toString(mimeType)).thenReturn("graph");
		when(tripliser.setInputStream(input)).thenReturn(tripliser);
		when(tripleGraph.getReport()).thenReturn(report);

	}

	@Test
	public void runsWithSystemOutputStreams() {
		String[] args = new String[0];
		triplise.run(args, System.out, System.err);
	}

	@Test
	public void printsUsageStatement() {
		String[] args = new String[0];
		triplise.run(args, out, err);

		contains("usage: input mapping", true);
	}

	@Test
	public void printsUsageStatementForABadOption() {
		String[] args = new String[]{"a b -badarg"};
		triplise.run(args, out, err);

		contains("usage: input mapping", true);
	}

	@Test
	public void printsUsageStatementForTooFewArguments() {
		String[] args = new String[]{"one"};
		triplise.run(args, out, err);

		contains("usage: input mapping", true);
	}

	@Test
	public void printsUsageStatementForTooManyArguments() {
		String[] args = new String[]{"one", "two", "three"};
		triplise.run(args, out, err);

		contains("usage: input mapping", true);
	}

	@Test
	public void mapsAnInputFileToTheDefaultOutputFileAndShowsReport() throws IOException {
		String[] args = new String[]{"input.xml", "mapping.xml"};

		triplise.run(args, out, err);

		verify(fileService).writeStringToNewFile("input.xml.rdf", "graph");
		contains("Report output", true);
	}

	@Test
	public void mapsAnInputFileToASpecifiedOutputFile() throws IOException {
		String[] args = new String[]{"input.xml", "mapping.xml", "-o", "output.rdf"};

		triplise.run(args, out, err);

		verify(fileService).writeStringToNewFile("output.rdf", "graph");
	}

	@Test
	public void mapsAnInputFileToALongOptionSpecifiedOutputFile() throws IOException {
		String[] args = new String[]{"input.xml", "mapping.xml", "--output", "output.rdf"};

		triplise.run(args, out, err);

		verify(fileService).writeStringToNewFile("output.rdf", "graph");
	}

	@Test
	public void mapsWritesTheOutputUsingTheSpecifiedMimeType() throws TripliserException {
		String[] args = new String[]{"input.xml", "mapping.xml", "-m", "mimeType"};
		mimeType = "mimeType";

		triplise.run(args, out, err);

		verify(tripleGraph).toString(mimeType);
	}

	@Test
	public void mapsWritesReportToSpecifiedOutputFile() throws IOException {
		String[] args = new String[]{"input.xml", "mapping.xml", "-r", "report.txt"};
		mimeType = "mimeType";

		triplise.run(args, out, err);

		verify(fileService).writeStringToNewFile("report.txt", "Report output");
		notContains("Report output", true);
	}

	@Test
	public void mapsWritesReportToSpecifiedXmlOutputFile() throws IOException {
		String[] args = new String[]{"input.xml", "mapping.xml", "-x", "report.xml"};
		mimeType = "mimeType";

		FileOutputStream fileOutput = mock(FileOutputStream.class);
		when(fileService.loadOutputStream("report.xml")).thenReturn(fileOutput);

		triplise.run(args, out, err);

		verify(fileOutput).write("XML Report output".getBytes());
		notContains("XML Report output", true);
	}

	@Test
	public void addSupportingInputsToTheTripliserIfSupplied() throws IOException, TripliserException {
		String[] args = new String[]{"input.xml", "mapping.xml", "-s", "id1:file1,id2:file2"};
		InputStream supportingInput1 = mock(InputStream.class);
		InputStream supportingInput2 = mock(InputStream.class);
		Map<String, InputStream> inputStreamMap = new HashMap<String, InputStream>();
		inputStreamMap.put("id1", supportingInput1);
		inputStreamMap.put("id2", supportingInput2);

		when(fileService.loadFile("file1")).thenReturn(supportingInput1);
		when(fileService.loadFile("file2")).thenReturn(supportingInput2);

		triplise.run(args, out, err);

		verify(tripliser).setSupportingInputStreams(inputStreamMap);
	}

	@Test
	public void showsInvalidMessageIfTheInputFormatIsIncorrect() {
		String[] args = new String[]{"input.xml", "mapping.xml", "-s", "file1,file2"};

		triplise.run(args, out, err);

		contains("Invalid supporting input parameter: file1,file2", false);
	}

	@Test
	public void showsInvalidMessageIfAnInputIssBlank() {
		String[] args = new String[]{"input.xml", "mapping.xml", "-s", "id1:"};

		triplise.run(args, out, err);

		contains("Invalid supporting input parameter: id1:", false);
	}

	private void contains(String expected, boolean out) {
		try {
			String output = outStream.toString("UTF-8");
			if (!out) output = errStream.toString("UTF-8");

			assertTrue("'" + expected + "' not found in '" + output + "'", output.contains(expected));
		} catch (UnsupportedEncodingException e) {
			fail("UnsupportedEncodingException");
		}
	}

	private void notContains(String expected, boolean out) {
		try {
			String output = outStream.toString("UTF-8");
			if (!out) output = errStream.toString("UTF-8");

			assertFalse("'" + expected + "' found in '" + output + "'", output.contains(expected));
		} catch (UnsupportedEncodingException e) {
			fail("UnsupportedEncodingException");
		}
	}

}
