/*
 * Copyright 2012, Emanuel Rabina (http://www.ultraq.net.nz/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.ultraq.web.lesscss;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Process LessCSS input using less.js on Mozilla Rhino.
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
	 * 
	 * @throws LessCSSException If there was a problem initializing the LessCSS
	 * 							processor.
	 */
	public LessCSSProcessor() throws LessCSSException {

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
	 * Process the LESS file, setting the processed result in the LESS file
	 * object.
	 * 
	 * @param lessfile The LESS file object to process.
	 */
	public void process(LessCSSFile lessfile) {

		lessfile.setProcessedContent(process(lessfile.getFilename(), lessfile.getSourceContent()));
	}

	/**
	 * Process the LESS input, returning the processed CSS.
	 * 
	 * @param input The LESS input to process.
	 * @return Processed LESS input.
	 */
	public String process(String input) {

		return process("(inline input)", input);
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
		catch (JavaScriptException ex) {
			throw new LessCSSException("Unable to process LESS input from " + filename, ex);
		}
		finally {
			Context.exit();
		}
	}
}
