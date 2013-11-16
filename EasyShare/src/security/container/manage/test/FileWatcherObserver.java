package security.container.manage.test;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.container.manage.FileSystemEvent;
import security.container.manage.FileWatcher;

public class FileWatcherObserver implements Observer {

	private static Log logger = LogFactory.getLog(FileWatcherObserver.class);

	@Override
	public void update(Observable observable, Object fileSystemEvent) {
		FileSystemEvent event = (FileSystemEvent) fileSystemEvent;
		logger.info(event.getFilePath() + " has been "
				+ event.getEventKind().name());
	}

	public static void main(String[] args) {
		try {
			FileWatcher watcher = new FileWatcher("");
			FileWatcherObserver observer = new FileWatcherObserver();
			watcher.addObserver(observer);
			watcher.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
