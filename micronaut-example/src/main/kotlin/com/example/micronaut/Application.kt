package com.example.micronaut

import io.micronaut.runtime.Micronaut

fun main(args: Array<String>) {
    Micronaut.build()
        .args(*args)
        .packages("com.example.micronaut")
        .start()
}
