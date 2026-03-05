package javabase;

/**
 * 解决HashMap中多个Key存在Hash冲突的封装对象
 * 
 * @param <_T>
 */
public class HashKey<_T> {
	private final _T key;
	private int hashCode;

	private HashKey(_T key, int hashCode) {
		this.key = key;
		this.hashCode = hashCode;
	}

	/**
	 * 自定义Hash值
	 * 
	 * @param <_T>
	 * @param key
	 * @param hashCode
	 * @return
	 */
	public static final <_T> HashKey<_T> of(_T key, int hashCode) {
		return new HashKey<>(key, hashCode);
	}

	/**
	 * 使用系统的对象Hash标识符，不同对象一定不同
	 * 
	 * @param <_T>
	 * @param key
	 * @return
	 */
	public static final <_T> HashKey<_T> of(_T key) {
		return new HashKey<>(key, System.identityHashCode(key));
	}

	public final _T key() {
		return key;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof HashKey hashKey) {
			return key.equals(hashKey.key);
		} else {
			return false;
		}
	}
}
