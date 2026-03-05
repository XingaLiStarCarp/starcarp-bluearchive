package javabase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 校验码
 */
public class CheckCode {
	public static final int DEFAULT_CHUNK_SIZE = 4096;

	private static MessageDigest SHA256;

	static {
		try {
			SHA256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
	}

	public static String sha256(ByteBuffer bytes) {
		SHA256.update(bytes);
		StringBuilder sb = new StringBuilder();
		for (byte b : SHA256.digest()) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	/**
	 * 分块读取文件并计算sha256校验码
	 * 
	 * @param filePath
	 * @param chunkSize
	 * @return
	 */
	public static String sha256(Path filePath, int chunkSize) {
		if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
			ByteBuffer bytes = ByteBuffer.allocate(chunkSize);
			try (FileChannel channel = FileChannel.open(filePath)) {
				while (channel.read(bytes, chunkSize) != -1) {
					bytes.flip();
					SHA256.update(bytes);
					bytes.clear();
				}
			} catch (IOException ex) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (byte b : SHA256.digest()) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} else {
			return null;
		}
	}

	public static String sha256(Path filePath) {
		return sha256(filePath, DEFAULT_CHUNK_SIZE);
	}

	/**
	 * 获取dirPath目录下所有文件的sha256校验码
	 * 
	 * @param dirPath
	 * @param chunkSize
	 * @return
	 */
	public static Map<String, String> sha256Map(Path path, int chunkSize) {
		if (Files.exists(path)) {
			final Map<String, String> sha256Map = new HashMap<>();
			if (Files.isRegularFile(path)) {
				sha256Map.put(path.getFileName().toString(), sha256(path, chunkSize));
			} else {
				try (Stream<Path> files = Files.walk(path)) {
					files.filter(Files::isRegularFile)
							.forEach((f) -> {
								sha256Map.put(f.getFileName().toString(), sha256(f, chunkSize));
							});
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return sha256Map;
		} else {
			return null;
		}
	}

	public static Map<String, String> sha256Map(Path path) {
		return sha256Map(path, DEFAULT_CHUNK_SIZE);
	}
}
