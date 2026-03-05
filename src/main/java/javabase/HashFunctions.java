package javabase;

import java.util.List;

public class HashFunctions {
	/**
	 * 使用求余进行散列的Hash算法。<br>
	 * 如果传入的多个hashCode值范围集中，那么返回的结果也范围集中。<br>
	 * 映射范围[min, max)是左闭右开区间，max必须大于min。<br>
	 * 
	 * @param hashCode
	 * @param min
	 * @param max
	 * @return
	 */
	public static final int modHash(int hashCode, int min, int max) {
		return min + (int) ((hashCode & 0xFFFFFFFFL) % (max - min));// hash负数转换为无符号整数
	}

	public static final int modHash(Object obj, int min, int max) {
		return modHash(obj.hashCode(), min, max);
	}

	public static final Object modHash(Object obj, Object[] values) {
		if (values.length == 0)
			return null;
		else
			return values[modHash(obj, 0, values.length)];
	}

	public static final <_T> _T modHash(Object obj, List<_T> values) {
		if (values.size() == 0)
			return null;
		else
			return values.get(modHash(obj, 0, values.size()));
	}
}
