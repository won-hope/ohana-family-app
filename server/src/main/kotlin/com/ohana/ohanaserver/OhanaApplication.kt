package com.ohana.ohanaserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OhanaApplication

fun main(args: Array<String>) {
    runApplication<OhanaApplication>(*args)
}
