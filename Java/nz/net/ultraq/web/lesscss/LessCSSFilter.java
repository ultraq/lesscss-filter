
package nz.net.ultraq.web.lesscss;

import nz.net.ultraq.web.filter.ResourceProcessingFilter;

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
	urlPatterns = "*.css"	
)
public class LessCSSFilter extends ResourceProcessingFilter<LessCSSFile> {

	private LessCSSProcessor processor;

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

		processor.process(resource);
	}

	/**
	 * Initialze the LessCSS processor.
	 * 
	 * @param filterConfig
	 */
	@Override
	public void init(FilterConfig filterConfig) {

		processor = new LessCSSProcessor();
	}
}
