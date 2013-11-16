package security.container.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class CountingHttpEntity implements HttpEntity {
		private HttpEntity entity;
		private CountingOutputStream cos;
		public CountingHttpEntity(long transferred) {
			this.cos = new CountingOutputStream(transferred);
		}
		
		public void setHttpEntity(HttpEntity _entity) {
			this.entity = _entity;
		}
		
		public CountingHttpEntity(HttpEntity _entity) {
			this.entity = _entity;
		}

		@Override
		@Deprecated
		public void consumeContent() throws IOException {
			entity.consumeContent();
		}

		@Override
		public InputStream getContent() throws IOException, IllegalStateException {
			return entity.getContent();
		}

		@Override
		public Header getContentEncoding() {
			return entity.getContentEncoding();
		}

		@Override
		public long getContentLength() {
			return entity.getContentLength();
		}

		@Override
		public Header getContentType() {
			return entity.getContentType();
		}

		@Override
		public boolean isChunked() {
			return entity.isChunked();
		}

		@Override
		public boolean isRepeatable() {
			return entity.isRepeatable();
		}

		@Override
		public boolean isStreaming() {
			return entity.isStreaming();
		}

		@Override
		public void writeTo(OutputStream os) throws IOException {
			cos.setOutstream(os);
			entity.writeTo(cos);
		}
		
		public long getTransferred() {
	    	return cos.getTransferred();
	    }
	}