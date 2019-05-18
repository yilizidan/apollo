package org.apollo.blog.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序算法
 * 总结：
 * 一、稳定性:
 * 　  稳定：冒泡排序、插入排序、归并排序和基数排序
 * 　　不稳定：选择排序、快速排序、希尔排序、堆排序
 * 二、平均时间复杂度
 * 　　O(n^2):直接插入排序，简单选择排序，冒泡排序。
 * 　　在数据规模较小时（9W内），直接插入排序，简单选择排序差不多。当数据较大时，冒泡排序算法的时间代价最高。性能为O(n^2)的算法基本上是相邻元素进行比较，基本上都是稳定的。
 * 　　O(nlogn):快速排序，归并排序，希尔排序，堆排序。
 * 　　其中，快排是最好的， 其次是归并和希尔，堆排序在数据量很大时效果明显。
 * 三、排序算法的选择
 * 　　1.数据规模较小
 * 　　 （1）待排序列基本序的情况下，可以选择直接插入排序；
 * 　　 （2）对稳定性不作要求宜用简单选择排序，对稳定性有要求宜用插入或冒泡
 * 　2.数据规模不是很大
 * 　　 （1）完全可以用内存空间，序列杂乱无序，对稳定性没有要求，快速排序，此时要付出log（N）的额外空间。
 * 　　 （2）序列本身可能有序，对稳定性有要求，空间允许下，宜用归并排序
 * 　3.数据规模很大
 * 　   （1）对稳定性有求，则可考虑归并排序。
 * （2）对稳定性没要求，宜用堆排序。
 * 　4.序列初始基本有序（正序），宜用直接插入，冒泡
 */
public class SortUtils {

	/*============ 插入排序 ============*/

	/**
	 * 插入排序
	 */
	public static void insertSort(int[] a) {
		//单独把数组长度拿出来，提高效率
		int len = a.length;
		int insertNum;//要插入的数
		//因为第一次不用，所以从1开始
		for (int i = 1; i < len; i++) {
			insertNum = a[i];
			//序列元素个数
			int j = i - 1;
			//从后往前循环，将大于insertNum的数向后移动
			while (j >= 0 && a[j] > insertNum) {
				//元素向后移动
				a[j + 1] = a[j];
				j--;
			}
			//找到位置，插入当前元素
			a[j + 1] = insertNum;
		}
	}

	/*============ 希尔排序 ============*/

	/**
	 * 希尔排序
	 */
	public static void sheelSort(int[] a) {
		//单独把数组长度拿出来，提高效率
		int len = a.length;
		while (len != 0) {
			len = len / 2;
			//分组
			for (int i = 0; i < len; i++) {
				//元素从第二个开始
				for (int j = i + len; j < a.length; j += len) {
					//k为有序序列最后一位的位数
					int k = j - len;
					//要插入的元素
					int temp = a[j];
					//从后往前遍历
					while (k >= 0 && temp < a[k]) {
						a[k + len] = a[k];
						//向后移动len位
						k -= len;
					}
					a[k + len] = temp;
				}
			}
		}
	}

	/*============ 简单选择排序 ============*/

	/**
	 * 简单选择排序
	 */
	public static void selectSort(int[] a) {
		int len = a.length;
		//循环次数
		for (int i = 0; i < len; i++) {
			int value = a[i];
			int position = i;
			//找到最小的值和位置
			for (int j = i + 1; j < len; j++) {
				if (a[j] < value) {
					value = a[j];
					position = j;
				}
			}
			//进行交换
			a[position] = a[i];
			a[i] = value;
		}
	}

	/*============ 堆排序 ============*/

	/**
	 * 堆排序
	 */
	public static void heapSort(int[] a) {
		int len = a.length;
		//循环建堆
		for (int i = 0; i < len - 1; i++) {
			//建堆
			buildMaxHeap(a, len - 1 - i);
			//交换堆顶和最后一个元素
			swap(a, 0, len - 1 - i);
		}
	}

	/**
	 * 交换方法
	 */
	private static void swap(int[] data, int i, int j) {
		int tmp = data[i];
		data[i] = data[j];
		data[j] = tmp;
	}

	/**
	 * 对data数组从0到lastIndex建大顶堆
	 */
	private static void buildMaxHeap(int[] data, int lastIndex) {
		//从lastIndex处节点（最后一个节点）的父节点开始
		for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
			//k保存正在判断的节点
			int k = i;
			//如果当前k节点的子节点存在
			while (k * 2 + 1 <= lastIndex) {
				//k节点的左子节点的索引
				int biggerIndex = 2 * k + 1;
				//如果biggerIndex小于lastIndex，即biggerIndex 1代表的k节点的右子节点存在
				if (biggerIndex < lastIndex) {
					//若果右子节点的值较大
					if (data[biggerIndex] < data[biggerIndex + 1]) {
						//biggerIndex总是记录较大子节点的索引
						biggerIndex++;
					}
				}
				//如果k节点的值小于其较大的子节点的值
				if (data[k] < data[biggerIndex]) {
					//交换他们
					swap(data, k, biggerIndex);
					//将biggerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值大于其左右子节点的值
					k = biggerIndex;
				} else {
					break;
				}
			}
		}
	}

	/*============ 冒泡排序 ============*/

	/**
	 * 冒泡排序
	 */
	public static void bubbleSort(int[] a) {
		int len = a.length;
		for (int i = 0; i < len; i++) {
			//注意第二重循环的条件
			for (int j = 0; j < len - i - 1; j++) {
				if (a[j] > a[j + 1]) {
					int temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
				}
			}
		}
	}

	/*============ 快速排序 ============*/

	/**
	 * 快速排序
	 *
	 * @param start 开始位置
	 * @param end   结束位置
	 */
	public static void quickSort(int[] a, int start, int end) {
		if (start < end) {
			//选基准值
			int baseNum = a[start];
			int midNum;//记录中间值
			int i = start;
			int j = end;
			do {
				while ((a[i] < baseNum) && i < end) {
					i++;
				}
				while ((a[j] > baseNum) && j > start) {
					j--;
				}
				if (i <= j) {
					midNum = a[i];
					a[i] = a[j];
					a[j] = midNum;
					i++;
					j--;
				}
			} while (i <= j);
			if (start < j) {
				quickSort(a, start, j);
			}
			if (end > i) {
				quickSort(a, i, end);
			}
		}
	}

	/*============ 归并排序 ============*/

	/**
	 * 归并排序
	 * Arrays.sort() 就是归并排序优化版
	 */
	public static void mergeSort(int[] a, int first, int last, int[] temp) {
		if (first < last) {
			int mid = (first + last) / 2;
			//左边有序
			mergeSort(a, first, mid, temp);
			//右边有序
			mergeSort(a, mid + 1, last, temp);
			//再将二个有序数列合并
			merge(a, first, mid, last, temp);
		}
	}

	private static void merge(int[] a, int first, int mid, int last, int[] temp) {
		int i = first, j = mid + 1;
		int m = mid, n = last;
		int k = 0;

		while (i <= m && j <= n) {
			if (a[i] <= a[j]) {
				temp[k++] = a[i++];
			} else {
				temp[k++] = a[j++];
			}
		}
		while (i <= m) {
			temp[k++] = a[i++];
		}
		while (j <= n) {
			temp[k++] = a[j++];
		}
		for (i = 0; i < k; i++) {
			a[first + i] = temp[i];
		}
	}

	/*============ 基数排序 ============*/

	/**
	 * 基数排序
	 */
	public static void baseSort(int[] a) {
		//首先确定排序的趟数;
		int max = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		int time = 0;
		//判断位数;
		while (max > 0) {
			max /= 10;
			time++;
		}
		//建立10个队列;
		List<ArrayList<Integer>> queue = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 10; i++) {
			ArrayList<Integer> queue1 = new ArrayList<Integer>();
			queue.add(queue1);
		}
		//进行time次分配和收集;
		for (int i = 0; i < time; i++) {
			//分配数组元素;
			for (int j = 0; j < a.length; j++) {
				//得到数字的第time 1位数;
				int x = a[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
				ArrayList<Integer> queue2 = queue.get(x);
				queue2.add(a[j]);
				queue.set(x, queue2);
			}
			//元素计数器;
			int count = 0;
			//收集队列元素;
			for (int k = 0; k < 10; k++) {
				while (queue.get(k).size() > 0) {
					ArrayList<Integer> queue3 = queue.get(k);
					a[count] = queue3.get(0);
					queue3.remove(0);
					count++;
				}
			}
		}
	}

	public static void main(String[] args) {

		for (int i = 0; i < 8; i++) {
			int[] a = {14, 10, 12, 0, 21, 4, 1, 41, 44, 10, 18};
			//Arrays.sort(a);
			switch (i) {
				case 0:
					insertSort(a);
					break;
				case 1:
					sheelSort(a);
					break;
				case 2:
					selectSort(a);
					break;
				case 3:
					heapSort(a);
					break;
				case 4:
					bubbleSort(a);
					break;
				case 5:
					quickSort(a, 0, a.length - 1);
					break;
				case 6:
					mergeSort(a, 0, a.length - 1, new int[a.length]);
					break;
				default:
					baseSort(a);
					break;
			}
			for (int n : a) {
				System.out.print(n + "  ");
			}
			System.out.println("");
		}
	}
}
