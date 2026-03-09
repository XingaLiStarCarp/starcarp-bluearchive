package javabase;

public class Algorithms {
	/**
	 * 一维边界判断。<br>
	 * 如果一个数value<boundValues[0]则返回0，boundValues[0]<value<boundValues[1]则返回1，以此类推<br>
	 * 
	 * @param value       要判定的数值
	 * @param boundValues 从小到大排列的数，即边界值
	 * @return
	 */
	public static int getBoundLevel(double value, double[] boundValues) {
		if (boundValues == null || boundValues.length == 0 || value < boundValues[0]) {
			return 0;
		}
		int last = boundValues.length - 1;
		if (value >= boundValues[last]) {
			return boundValues.length;
		}
		int left = 0;
		int right = last;
		// 二分查找
		while (left < right) {
			int mid = (left + right) / 2;
			if (value >= boundValues[mid]) {
				left = mid + 1;
			} else {
				right = mid;
			}
		}
		return left;
	}
}
