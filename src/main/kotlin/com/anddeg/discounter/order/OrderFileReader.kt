package com.anddeg.discounter.order

import java.io.File

class OrderFileReader(private val orderFactory: OrderFactory = OrderFactory()) {

    fun createOrders(filePath: String): List<Orderable> {
        val file = File(filePath)
        if (!file.exists())
            throw OrderFileNotFoundException()
        return orderFactory.create(file.readLines())
    }

}
