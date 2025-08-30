package com.mshdabiola.ui

import kotlinx.browser.window


// Top-level function for encoding URI components using JavaScript
fun encodeURIComponentJs(str: String): JsString = js("encodeURIComponent(str)")

class ReaLogics() :  Logics {
    override fun openUrl(url: String) {
        window.open(url, "_blank")
    }

    override fun openEmail(emailAddress: String, subject: String, body: String) {
        // Simple mailto link for Wasm/JS.
        // Encoding subject and body is important for special characters.
        val encodedSubject = encodeURIComponentJs(subject) // Use the top-level function
        val encodedBody = encodeURIComponentJs(body) // Use the top-level function
        window.open("mailto:$emailAddress?subject=$encodedSubject&body=$encodedBody", "_self")
    }
}
