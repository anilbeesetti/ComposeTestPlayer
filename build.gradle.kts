buildscript {

}

/**
 * TODO: IDE showing error but it works, this issue is already reported in issue tracker
 * Issue Tracker Link: https://youtrack.jetbrains.com/issue/KTIJ-19369 is fixed
 */
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
