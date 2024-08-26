package com.mifos.compose

interface PasscodeRepository {
    fun getSavedPasscode(): String
    val hasPasscode: Boolean

    fun setHasPassCode(hasPassCode:Boolean)
    fun savePasscode(passcode: String)
}