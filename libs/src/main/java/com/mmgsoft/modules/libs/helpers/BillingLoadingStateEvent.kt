package com.mmgsoft.modules.libs.helpers

data class BillingLoadingStateEvent (
    val state: BillingLoadingState
)

enum class BillingLoadingState {
    SHOW_LOADING,
    HIDE_LOADING
}