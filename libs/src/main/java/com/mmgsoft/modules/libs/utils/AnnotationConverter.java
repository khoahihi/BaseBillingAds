package com.mmgsoft.modules.libs.utils;

import com.mmgsoft.modules.libs.helpers.UseCurrency;

public class AnnotationConverter {
    public static <T> void get(Class<T> clz) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            UseCurrency c = clz.getDeclaredAnnotation(UseCurrency.class);
            c.currency();
        }
    }
}
