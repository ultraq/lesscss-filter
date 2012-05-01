
package nz.net.ultraq.web.lesscss;

import nz.net.ultraq.web.filter.Resource;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class containing a LESS file, and the processed result.  Automatically
 * resolves all imports in the LESS file.  Used to query the source file and its
 * imports to see if anything has changed since it was processed to assist with
 * caching.
 * 
 * @author Emanuel Rabina
 */
public class LessCSSFile extends Resource {

	private static final String IMPORT_DECLARATION = "@import";
	private static final String LESS_FILE_EXTENSION = ".less";

	/**
	 * Constructor, build the source content from the captured response.
	 * Recursively replaces all imports and watches both the source and every
	 * import for modifications.
	 * 
	 * @param path
	 * @param sourcecontent
	 * @throws IOException
	 */
	public LessCSSFile(String path, String sourcecontent) throws IOException {

		super(path);

		// Resolve all imports
		StringBuilder sourcefilecontent = new StringBuilder(sourcecontent);
		for (int importindex = sourcefilecontent.indexOf(IMPORT_DECLARATION); importindex != -1;
				 importindex = sourcefilecontent.indexOf(IMPORT_DECLARATION)) {

			int importindexend = sourcefilecontent.indexOf(";", importindex);
			String importfilename = sourcefilecontent.substring(
					importindex + IMPORT_DECLARATION.length() + 1, importindexend).trim();
			if (!importfilename.endsWith(LESS_FILE_EXTENSION)) {
				importfilename += LESS_FILE_EXTENSION;
			}

			// Replace the @import line with the actual content of the imported file
			Path importfile = FileSystems.getDefault().getPath(resource.getParent().toString(), importfilename);
			sourcefilecontent.replace(importindex, sourcefilecontent.indexOf("\n", importindex),
					new String(Files.readAllBytes(importfile)));

			// Watch the import
			watchResource(importfile);
		}

		this.sourcecontent = sourcefilecontent.toString();
	}
}
