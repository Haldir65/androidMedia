// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(deps.plugins.android.application) apply false
    alias(deps.plugins.android.library) apply false
    alias(deps.plugins.hilt) apply false
    alias(deps.plugins.ksp) apply false
    alias(deps.plugins.kotlin.android) apply false
}
