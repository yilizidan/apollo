package org.apollo.blog.util;


import org.springframework.stereotype.Component;

/**
 * 归并排序
 */
@Component
public class MergeX {

	private Comparable[] aux;

	private boolean less(Comparable v, Comparable w) {
		return v.compareTo(w) < 0;
	}

	/**
	 * 原地归并的抽象方法
	 */
	private void merge(Comparable[] a, Comparable[] aux, int lo, int mid, int hi) {
		int i = lo, j = mid + 1;
		for (int k = lo; k <= hi; k++) {
			if (i > mid) {
				a[k] = aux[j++];
			} else if (j > hi) {
				a[k] = aux[i++];
			} else if (less(aux[j], aux[i])) {
				a[k] = aux[j++];
			} else {
				a[k] = aux[i++];
			}
		}
	}

	public void sort(Comparable[] a) {
		// 一次性分配空间
		aux = a.clone();
		sort(a, aux, 0, a.length - 1);
	}

	private void sort(Comparable[] a, Comparable[] aux, int lo, int hi) {
		//自顶向下的并归排序 三个改进
		int mid = lo + (hi - lo) / 2;
		// 对小规模子数组使用插入排序
		if (hi - lo <= 7) {
			insertionSort(a, lo, hi);
			return;
		}
		sort(aux, a, lo, mid);
		sort(aux, a, mid + 1, hi);
		// 已经有序时跳过merge(a中lo到mid mid到hi分别都是有序的)
		if (!less(aux[mid + 1], aux[mid])) {
			System.arraycopy(aux, lo, a, lo, hi - lo + 1);
			return;
		}
		merge(a, aux, lo, mid, hi);
	}

	private void insertionSort(Comparable[] a, int lo, int hi) {
		for (int i = lo; i <= hi; i++) {
			for (int j = i; j > lo && less(a[j], a[j - 1]); j--) {
				exch(a, j, j - 1);
			}
		}
	}

	private void exch(Comparable[] a, int j, int i) {
		// TODO Auto-generated method stub
		Comparable temp;
		temp = a[j];
		a[j] = a[i];
		a[i] = temp;
	}

	public static void main(String[] args) {
		Comparable[] a = {8, 1, 6, 8, 4, 6, 9, 7, 1, 2, 3, 4, 8, 5, 2, 6, 4, 3, 8};
		new MergeX().sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
		}
	}
}
