
package nz.net.ultraq.web.lesscss;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Processes a LESS file using the Mozilla Rhino JavaScript engine for Java.
 * 
 * @author Emanuel Rabina
 */
public class LessCSSProcessor {

	private static final String ENV_RHINO_JS = "nz/net/ultraq/web/lesscss/env.rhino-1.2.13.js";
	private static final String LESS_JS      = "nz/net/ultraq/web/lesscss/less-1.3.0.js";

	/* (function() {
	 * 	 var result = null;
	 *   var parser = new less.Parser({
	 *     filename: '{0}'
	 *   });
	 * 
	 *   parser.parse('{1}', function(err, tree) {
	 *     result = err ? err : tree.toCSS();
	 *   });
	 *   return result;
	 * })();
	 */
	private static final String processjs = "(function() { var result = null; var parser = new less.Parser({ filename: '{0}' }); parser.parse('{1}', function(err, tree) { result = err ? err : tree.toCSS(); }); return result; })();";

	private final Scriptable scope;

	/**
	 * Create a new LessCSS processor.
	 */
	public LessCSSProcessor() {

		try {
			Context context = Context.enter();
			context.setLanguageVersion(Context.VERSION_1_8);
			context.setOptimizationLevel(-1);
			Global global = new Global();
			global.init(context);
			scope = context.initStandardObjects(global);

			context.evaluateReader(scope, new BufferedReader(new InputStreamReader(
					LessCSSProcessor.class.getClassLoader().getResource(ENV_RHINO_JS).openStream())),
					"env.rhino.js", 1, null);
			context.evaluateReader(scope, new BufferedReader(new InputStreamReader(
					LessCSSProcessor.class.getClassLoader().getResource(LESS_JS).openStream())),
					"less.js", 1, null);
		}
		catch (IOException ex) {
			throw new LessCSSException("Unable to initialize LessCSS processor.", ex);
		}
		finally {
			Context.exit();
		}
	}

	/**
	 * Process the LESS input, returning the processed CSS.
	 * 
	 * @param input The LESS input to process.
	 * @return Processed LESS input.
	 */
	public String process(String input) {

		try {
			Context context = Context.enter();
			context.setLanguageVersion(Context.VERSION_1_8);

			// Process the LESS input
			String processless = processjs.replace("{0}", "(inline input)")
					.replace("{1}", input.replace("'", "\\'").replaceAll("\\s", " "));
			return context.evaluateString(scope, processless, "process-less.js", 1, null).toString();
		}
		catch (RuntimeException ex) {
			throw new LessCSSException("Unable to process LESS input", ex);
		}
		finally {
			Context.exit();
		}
	}

	/**
	 * Process the LESS input, returning the processed CSS.
	 * 
	 * @param filename Name of the LESS file to process.
	 * @param input	   The LESS input to process.
	 * @return Processed LESS input.
	 */
	public String process(String filename, String input) {

		try {
			Context context = Context.enter();
			context.setLanguageVersion(Context.VERSION_1_8);

			// Process the LESS input
			String processless = processjs.replace("{0}", filename)
					.replace("{1}", input.replace("'", "\\'").replaceAll("\\s", " "));
			return context.evaluateString(scope, processless, "process-less.js", 1, null).toString();
		}
		catch (RuntimeException ex) {
			throw new LessCSSException("Unable to process LESS input from " + filename, ex);
		}
		finally {
			Context.exit();
		}
	}

	/**
	 * Process the LESS file, setting the processed result in the LESS file
	 * object.
	 * 
	 * @param input The LESS file object to process.
	 * @return The same LESS file with updated processed content.
	 */
	public LessCSSFile process(LessCSSFile input) {

		input.setProcessedContent(process(input.getLessFilename(), input.getLessFileContent()));
		return input;
	}
}
