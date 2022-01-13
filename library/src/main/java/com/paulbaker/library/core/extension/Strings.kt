package com.paulbaker.library.core.extension

fun String.isValidValue(): Boolean {
    return this.isNotEmpty() && this.isNotBlank()
}

fun String?.isNotNull(): Boolean {
    return this != null
}