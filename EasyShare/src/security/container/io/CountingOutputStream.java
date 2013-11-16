package security.container.io;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {

	    private OutputStream outstream;
	    private volatile long transferred;
	    
	    public CountingOutputStream(long transferred) {
	    	super();
	    	this.transferred = transferred;
	    }

	    public CountingOutputStream(OutputStream outstream, long transferred) {
	    	super();
	        this.outstream = outstream;
	        this.transferred = transferred;
	    }
	    
	   
	    @Override
	    public void write(int b) throws IOException {
	        outstream.write(b);
	        transferred ++;
	    }

	    @Override
	    public void write(byte[] b) throws IOException {
	        outstream.write(b);
	        transferred  += b.length;
	    }

	    @Override
	    public void write(byte[] b, int off, int len) throws IOException {
	        outstream.write(b, off, len);
	        transferred  += len;
	    }

	    @Override
	    public void flush() throws IOException {
	        outstream.flush();
	    }

	    @Override
	    public void close() throws IOException {
	        outstream.close();
	    }
	   
		/**
		 * @param outstream the outstream to set
		 */
		public void setOutstream(OutputStream outstream) {
			this.outstream = outstream;
		}

		public long getTransferred() {
	    	return this.transferred;
	    }
	}