package org.daverog.tripliser.cli;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.daverog.tripliser.Constants;
import org.daverog.tripliser.Tripliser;
import org.daverog.tripliser.TripliserFactory;
import org.daverog.tripliser.exception.TripliserException;
import org.daverog.tripliser.graphs.TripleGraph;



@SuppressWarnings("static-access")
public class Triplise {

	private static Options OPTIONS;

	static {
		OPTIONS = new Options();
		OPTIONS.addOption(OptionBuilder
				.withDescription("A filename for the report, hides the stdout report output")
				.withLongOpt("report")
				.hasArg()
				.withArgName("filename")
				.create('r'));
		OPTIONS.addOption(OptionBuilder
				.withDescription("A filename for the XML report, hides the stdout report output")
				.withLongOpt("xml-report")
				.hasArg()
				.withArgName("filename")
				.create('x'));
		OPTIONS.addOption(OptionBuilder
				.withDescription("A filename for the output. Default applies '.rdf' extension to input")
				.withLongOpt("output")
				.hasArg()
				.withArgName("filename")
				.create('o'));
		OPTIONS.addOption(OptionBuilder
				.withDescription("The mime-type to use for the output (text/rdf+n3, application/x-turtle, text/plain, application/rdf+xml+abbr, application/rdf+xml)")
				.withLongOpt("mime-type")
				.hasArg()
				.withArgName("mimetype")
				.create('m'));
		OPTIONS.addOption(OptionBuilder
				.withDescription("A comma-separated list of supporting input references, in the format 'id:inputRef', e.g. 'id1:input1,id2:input2'. The mapping will determine the format of the input, where the default is XML files")
				.withLongOpt("supporting-inputs")
				.hasArg()
				.withArgName("inputs")
				.create('s'));
	}

	public static void main(String[] args) {
		Triplise triplise = new Triplise(new FileService(), TripliserFactory.instance());
		triplise.run(args, System.out, System.err);
	}

	private final TripliserFactory tripliserFactory;
	private final FileService fileService;

	public Triplise(FileService fileService, TripliserFactory tripliserFactory) {
		this.fileService = fileService;
		this.tripliserFactory = tripliserFactory;
	}

	public void run(String[] args, PrintStream out, PrintStream err) {
		CommandLineParser parser = new PosixParser();

		try {
		    CommandLine line = parser.parse(OPTIONS, args);

		    String[] arguments = line.getArgs();

		    if (arguments.length < 2) {
		    	usage(out); return;
		    }
		    if (arguments.length > 2) {
		    	usage(out); return;
		    }

			String inputFile = arguments[0];
		    String mappingFile = arguments[1];

		    InputStream input = fileService.loadFile(inputFile);
		    InputStream mapping = fileService.loadFile(mappingFile);

		    Tripliser tripliser = tripliserFactory.setMapping(mapping).create();

		    if (line.hasOption('s')) {
		    	String supportingInputParameter = line.getOptionValue('s');
				String[] supportingInputs = supportingInputParameter.split(",");

		    	Map<String, InputStream> supportingInputStreams = new HashMap<String, InputStream>();
		    	for (String supportingInput : supportingInputs) {
		    		String[] parts = supportingInput.split(":");

		    		if (parts.length != 2 || StringUtils.isBlank(parts[0]) || StringUtils.isBlank(parts[1]))
		    			throw new TripliserException("Invalid supporting input parameter: " + supportingInputParameter);

		    		InputStream supportingInputFile = fileService.loadFile(parts[1]);
					supportingInputStreams.put(parts[0], supportingInputFile);
		    	}

				tripliser.setSupportingInputStreams(supportingInputStreams );
		    }

		    TripleGraph graph = tripliser
		    	.setInputStream(input)
		    	.generateTripleGraph();

		    String outputFilename = inputFile + ".rdf";
		    String mimeType = Constants.MIME_TYPE_APPLICATION_RDF_XML;
		    boolean showReport = true;

		    if (line.hasOption('o')) {
		    	outputFilename = line.getOptionValue('o');
		    }
		    if (line.hasOption('m')) {
		    	mimeType = line.getOptionValue('m');
		    }
		    if (line.hasOption('r')) {
		    	String reportFile = line.getOptionValue('r');
		    	fileService.writeStringToNewFile(reportFile,
			    		graph.getReport().toString());
		    	showReport = false;
		    }
		    if (line.hasOption('x')) {
		    	String reportFile = line.getOptionValue('x');
		    	try {
		    		FileOutputStream outputStream = fileService.loadOutputStream(reportFile);
					graph.getReport().writeXml(outputStream);
					outputStream.flush();
					outputStream.close();
				} catch (Exception e) {
					throw new TripliserException("Error writing report to XML", e, null);
				}
		    	showReport = false;
		    }

		    if (showReport) {
		    	graph.getReport().writeText(out);
		    }

		    fileService.writeStringToNewFile(outputFilename,
		    		graph.toString(mimeType));
		} catch(ParseException e) {
			err.println("Unexpected exception:" + e.getMessage());
		} catch (TripliserException e) {
			err.println("Tripliser error:" + e.getMessage());
		} catch (FileNotFoundException e) {
			err.println(e.getMessage());
		} catch (IOException e) {
			err.println("IO Error:" + e.getMessage());
		}
	}

	private void usage(PrintStream out) {
		HelpFormatter formatter = new HelpFormatter();
		PrintWriter outWriter = new PrintWriter(out);
		formatter.printHelp(outWriter ,
				HelpFormatter.DEFAULT_WIDTH,
				"input mapping",
				"",
				OPTIONS,
				HelpFormatter.DEFAULT_LEFT_PAD,
				HelpFormatter.DEFAULT_DESC_PAD,
				"",
				true);
		outWriter.flush();
	}

}
