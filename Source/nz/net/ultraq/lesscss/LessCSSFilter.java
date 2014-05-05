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

package nz.net.ultraq.lesscss;

import nz.net.ultraq.postprocessing.ResourceProcessingFilter;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.annotation.WebFilter;

/**
 * Filter to process .css requests which are actually LESS files, doing on the
 * server side what less.js normally does on the client side.
 * 
 * @author Emanuel Rabina
 */
@WebFilter(
	filterName = "LessCSSFilter",
	urlPatterns = "*.less"
)
public class LessCSSFilter extends ResourceProcessingFilter<LessCSSFile> {

	private LessCSSCompiler compiler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LessCSSFile buildResource(String path, String resourcecontent) throws IOException {

		return new LessCSSFile(path, resourcecontent);
	}

	/**
	 * Process a LESS file, setting the compiled CSS file in the result.
	 * 
	 * @param resource
	 */
	@Override
	protected void doProcessing(LessCSSFile resource) {

		resource.setProcessedContent(compiler.compile(resource.getFilename(), resource.getSourceContent()));
	}

	/**
	 * Initialze the LessCSS processor.
	 * 
	 * @param filterConfig
	 */
	@Override
	public void init(FilterConfig filterConfig) {

		compiler = new LessCSSCompiler();
	}
}
