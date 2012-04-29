
package nz.net.ultraq.web.lesscss;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Response wrapper to capture the .less file.
 * 
 * @author Emanuel Rabina
 */
public class LessCSSResponseWrapper extends HttpServletResponseWrapper {

	private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private ServletOutputStream sos;

	/**
	 * Constructor, set the original response.
	 * 
	 * @param response
	 */
	LessCSSResponseWrapper(HttpServletResponse response) {

		super(response);
	}

	/**
	 * Return the .less file bytes.
	 * 
	 * @return Output stream containing the .less file.
	 */
	ByteArrayOutputStream getLessFileBytes() {

		return bos;
	}

	/**
	 * Return an output stream different from that of the wrapped response to
	 * capture the .less file.
	 * 
	 * @return Output stream for the .less file.
	 */
	@Override
	public ServletOutputStream getOutputStream() {

		if (sos == null) {
			sos = new ServletOutputStream() {
				@Override
				public void write(int b) {
					bos.write(b);
				}
			};
		}
		return sos;
	}
}
