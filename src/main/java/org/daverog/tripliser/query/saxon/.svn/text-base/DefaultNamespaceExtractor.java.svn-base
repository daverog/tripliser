package org.daverog.tripliser.query.saxon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultNamespaceExtractor {
	
	public static String extract(String source) {
		Pattern p = Pattern.compile("xmlns=\"([^\"]+)\"");
		Matcher m = p.matcher(source);
		
		if (m.find()) {
			return m.group(1);
		}
		
		return null;
	}

}
