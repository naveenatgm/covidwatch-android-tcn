package org.covidwatch.android

object GlobalConstants {

    val CHANNEL_ID = "CovidWatchContactTracingNotificationChannel"
    val INTERACTION_MIN_DISTANCE_IN_FEET = 7
    // private const val INTERACTION_DURATION_IN_MINUTES = 0.01 //.250 // equates to 15 seconds after math in logInteraction function
    val INTERACTION_NOTIFY_WAIT_DURATION_IN_SECONDS = 600 // int
    const val INTERACTION_STALE_DURATION_IN_SECONDS = 30 // integer value
    const val DISTANCE_HISTORY_COUNT = 7 // # of historical distances
    const val INTERACTION_DELETE_DURATION_IN_DAYS = 21 // # of days to keep the interaction history

    const val SNOOZE_TIME_IN_SECONDS = 600

    const val MEDICAL_API_CONTACT_URL = "https://your_api_domain.com/api/v1/contacts";
    const val MEDICAL_API_PHONE_REGISTRATION_URL = "https://your_api_domain.com/api/v1/contact_registration";
    const val MEDICAL_API_DATE_PATTERN = "yyyy-MM-dd HH:mm"

    const val MEDICAL_API_CT_KEY = "api_symmetric_key"

    const val GETTING_CLOSE_DISTANCE_IN_FEET = 1.5
    const val TOO_CLOSE_DISTANCE_IN_FEET = .75

    const val WAIT_TO_DOWNLOAD_SIGNED_REPORT_IN_MINUTES :Long = 15
}