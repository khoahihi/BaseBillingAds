package com.mmgsoft.modules.libs.etx

import kotlin.reflect.KProperty1

internal fun<T, S> List<T>.findIndex(item: T, compare: KProperty1<T, S>): Int {
    var pos = -1
    for(i in indices) {
        if(compare.get(this[i]) == compare.get(item)) {
            pos = i
            break
        }
    }
    return pos
}