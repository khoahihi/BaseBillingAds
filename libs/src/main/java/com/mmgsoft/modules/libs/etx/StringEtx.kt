package com.mmgsoft.modules.libs.etx

import com.mmgsoft.modules.libs.helpers.AmazonCurrency
import com.mmgsoft.modules.libs.models.Currency

internal fun String.toCurrency() = Currency(this)

internal fun AmazonCurrency.toCurrency() = Currency(this.c)