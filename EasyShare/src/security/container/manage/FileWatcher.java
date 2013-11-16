package security.container.manage;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nlsde.ac.SystemConfiguration;

/**
 * 监控目录内文件的更新、创建和删除事件
 * 
 * @author RunhuaXU
 * 
 */
public class FileWatcher extends Observable {
	private static Log logger = LogFactory.getLog(FileWatcher.class);  
	
	private WatchService watcher;
	private Path watchDir;
	private WatchKey key;
	private Executor executor = Executors.newSingleThreadExecutor();
	
	FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
		public Integer call() throws InterruptedException {
			processEvents();
			return Integer.valueOf(0);
		}
	});

	public FileWatcher(String targetPath) throws IOException {
		watchDir = Paths.get(targetPath);
		watcher = FileSystems.getDefault().newWatchService();
		key = watchDir.register(watcher, ENTRY_MODIFY, ENTRY_CREATE,
				ENTRY_DELETE);
//		key = watchDir.register(watcher, ENTRY_CREATE,
//				ENTRY_DELETE);
		logger.info("File Watcher initialized successfully");
		logger.info("Directory->" + targetPath + " was watched");
	}
	
	public void updateWatchDir(SystemConfiguration config) throws IOException {
		watchDir = Paths.get(config.getWatcherDir());
		key.cancel();
		key = watchDir.register(watcher, ENTRY_CREATE,
				ENTRY_DELETE, ENTRY_MODIFY);
		logger.info("File watcher dir update");
		logger.info("Directory->" + config.getWatcherDir() + "was watched");
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * 启动监控过程
	 */
	public void execute() {
		// 通过线程池启动一个额外的线程加载Watching过程
		executor.execute(task);
	}

	/**
	 * 关闭后的对象无法重新启动
	 * 
	 * @throws IOException
	 */
	public void shutdown() throws IOException {
		watcher.close();
		executor = null;
	}

	/**
	 * 监控文件系统事件
	 */
	private void processEvents() {
		while (true) {
			// 等待直到获得事件信号
			WatchKey signal;
			try {
				signal = watcher.take();
				logger.debug("Catch a event singal:" + signal.toString());
			} catch (InterruptedException x) {
				return;
			}

			for (WatchEvent<?> event : signal.pollEvents()) {
				Kind<?> kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					logger.debug("An OVERFLOW event in the signal.");
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				notifiy(name.getFileName().toString(), kind);
			}
			
			// 为监控下一个通知做准备
			key.reset();
		}
	}

	/**
	 * 通知外部各个Observer目录有新的事件更新
	 */
	void notifiy(String fileName, Kind<?> kind) {
		// 标注目录已经被做了更改
		setChanged();
		// 主动通知各个观察者目标对象状态的变更
		// 这里采用的是观察者模式的“推”方式
		notifyObservers(new FileSystemEvent(fileName, kind));
	}
	
	public WatchService getWatcher() {
		return watcher;
	}
	
	
}
