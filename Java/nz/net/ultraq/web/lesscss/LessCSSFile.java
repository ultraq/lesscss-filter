
package nz.net.ultraq.web.lesscss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Class containing a LESS file, and the processed result.  Automatically
 * resolves all imports in the LESS file.  Used to query the source file and its
 * imports to see if anything has changed since it was processed to assist with
 * caching.
 * 
 * @author Emanuel Rabina
 */
public class LessCSSFile {

	private static final String IMPORT_DECLARATION = "@import";

	private final String sourcefilename;
	private final String sourcecontent;
	private final LinkedHashMap<File,Long> sources = new LinkedHashMap<>();
	private String processedcontent;

	/**
	 * Constructor, set the source file from a servlet response.
	 * 
	 * @param sourcefile
	 * @param responsewrapper
	 */
	LessCSSFile(File sourcefile, LessCSSResponseWrapper responsewrapper) {

		this.sourcefilename = sourcefile.getName();
		sources.put(sourcefile, sourcefile.lastModified());

		StringBuilder sourcefilecontent = new StringBuilder(new String(
				responsewrapper.getLessFileBytes().toByteArray()));

		// Resolve all imports
		try {
			int importindex = sourcefilecontent.indexOf(IMPORT_DECLARATION);
			while (importindex != -1) {
				int importindexend = sourcefilecontent.indexOf(";", importindex);
				String importfilename = sourcefilecontent.substring(
						importindex + IMPORT_DECLARATION.length() + 1, importindexend).trim();

				File importfile = new File(sourcefile.getParent(), importfilename);
				sources.put(importfile, importfile.lastModified());
				sourcefilecontent.replace(importindex, importindexend + 1, readFile(importfile));
			}
		}
		catch (IOException ex) {
			throw new LessCSSException("Unable to resolve or include LESS file imports", ex);
		}

		this.sourcecontent = sourcefilecontent.toString();
	}

	/**
	 * Return the content of the LESS file, including any imports.
	 * 
	 * @return LESS file content.
	 */
	String getLessFileContent() {

		return sourcecontent;
	}

	/**
	 * Return the LESS file name.
	 * 
	 * @return LESS file name.
	 */
	String getLessFilename() {

		return sourcefilename;
	}

	/**
	 * Return the processed version of the LESS file.
	 * 
	 * @return Processed LESS file, or <tt>null</tt> if the file hasn't been
	 * 		   processed.
	 */
	public String getProcessedContent() {

		return processedcontent;
	}

	/**
	 * Reads a file, returning its content as a string.
	 * 
	 * @param file
	 * @return File content.
	 * @throws IOException
	 */
	private String readFile(File file) throws IOException {

		StringBuilder filecontent = new StringBuilder((int)file.length());
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = reader.readLine();
		while (line != null) {
			filecontent.append(line);
			line = reader.readLine();
		}

		return filecontent.toString();
	}

	/**
	 * Set the result of processing the LESS file and its imports.
	 * 
	 * @param processedcontent
	 */
	void setProcessedContent(String processedcontent) {

		this.processedcontent = processedcontent;
	}

	/**
	 * Return whether or not the LESS file and/or its imports have been modified
	 * since the last time the processed result was generated.
	 * 
	 * @return <tt>true</tt> if the LESS file and/or its imports have changed,
	 * 		   <tt>false</tt> otherwise.
	 */
	boolean sourcesModified() {

		for (File source: sources.keySet()) {
			if (!sources.get(source).equals(source.lastModified())) {
				return true;
			}
		}
		return false;
	}
}
