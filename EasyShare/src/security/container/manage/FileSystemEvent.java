package security.container.manage;

import java.nio.file.WatchEvent.Kind;

/**
 * 文件系统事件类型
 * 
 * @author RunhuaXU
 *
 */
public class FileSystemEvent {
	private final String filePath;
	private final Kind<?> eventKind;

	public FileSystemEvent(String filePath, Kind<?> eventKind) {
		this.filePath = filePath;
		this.eventKind = eventKind;
	}

	/**
	 * 返回文件路径
	 * 
	 * @return 文件路径
	 */
	public String getFilePath() {
		return this.filePath;
	}

	/**
	 * 返回监控事件类型：变更、创建、删除三种类型
	 * 
	 * @return 监控事件类型
	 */
	public Kind<?> getEventKind() {
		return this.eventKind;
	}
}
