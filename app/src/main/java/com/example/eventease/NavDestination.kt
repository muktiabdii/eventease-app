package com.example.eventease

object NavDestination {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_EVENT = "create_event"
    const val MY_EVENTS = "my_events"
    const val PROFILE = "profile"
    const val EVENT_DETAIL_ROUTE = "event_detail"
    const val EVENT_DETAIL_ID_ARG = "eventId"
    const val EVENT_DETAIL = "$EVENT_DETAIL_ROUTE/{$EVENT_DETAIL_ID_ARG}"
}