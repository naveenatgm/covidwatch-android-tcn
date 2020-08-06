package org.covidwatch.android.presentation.home

import androidx.annotation.StringRes

sealed class ContactWarningBannerState {
    data class Visible(@StringRes val text: Int) : ContactWarningBannerState()

    object Hidden : ContactWarningBannerState()
}

sealed class InfoBannerState {

    data class Visible(@StringRes val text: Int) : InfoBannerState()

    object Hidden : InfoBannerState()
}