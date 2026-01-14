package com.uxstudio.contacts

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableAdminServer
@SpringBootApplication
class ContactsApplication

fun main(args: Array<String>) {
	runApplication<ContactsApplication>(*args)
}
