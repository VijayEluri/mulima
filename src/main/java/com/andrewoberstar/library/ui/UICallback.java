package com.andrewoberstar.library.ui;

import java.util.Map;

public interface UICallback<T> {
	T call(Map<String, Object> parms);
}
