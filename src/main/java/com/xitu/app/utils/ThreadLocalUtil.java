package com.xitu.app.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.Model;

public class ThreadLocalUtil {

	private static Map<Thread, Model> valueMap = Collections.synchronizedMap(new HashMap<Thread, Model>());

	public static void set(Model newValue) {
		// Key为线程对象，Value为本线程的变量副本
		valueMap.put(Thread.currentThread(), newValue);

	}

	public static Model get() {

		Thread currentThread = Thread.currentThread();
		// 返回当前线程对应的变量
		Model o = valueMap.get(currentThread);
		// 如果当前线程在Map中不存在，则将当前线程存储到Map中
		if (o == null && !valueMap.containsKey(currentThread)) {
			o = initialValue();
			valueMap.put(currentThread, o);
		}
		return o;
	}

	public static void remove() {
		valueMap.remove(Thread.currentThread());
	}

	public static Model initialValue() {
		return null;
	}
}
