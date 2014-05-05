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

import nz.net.ultraq.postprocessing.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
			Path importfile = Paths.get(resource.getParent().toString(), importfilename);
			sourcefilecontent.replace(importindex, sourcefilecontent.indexOf("\n", importindex),
					new String(Files.readAllBytes(importfile)));

			// Watch the import
			watchResource(importfile);
		}

		this.sourcecontent = sourcefilecontent.toString();
	}
}
