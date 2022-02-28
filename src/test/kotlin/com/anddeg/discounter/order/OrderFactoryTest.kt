package com.anddeg.discounter.order

import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate

class OrderFactoryTest {

    @Test
    fun `Empty orders input produces nothing`() {
        val orders = OrderFactory().create(listOf())

        assertEquals(emptyList<Orderable>(), orders)
    }

    @Test
    fun `Blank orders input produces invalid orders`() {
        val orders = OrderFactory().create(listOf("", ""))

        assertEquals(listOf(InvalidOrder(), InvalidOrder()), orders)
    }

    @Test
    fun `Invalid input produces invalid orders`() {
        val orders = OrderFactory().create(listOf("foo#bar#baz"))

        assertEquals(listOf(InvalidOrder("foo#bar#baz")), orders)
    }

    @Test
    fun `Order has an ISO date without hours, a shipment size and a carrier code`() {
        val orders = OrderFactory().create(listOf("2021-09-25 S MR"))

        assertEquals(listOf(Order(LocalDate.parse("2021-09-25"), ShipmentSize.S, CarrierCode.MR)), orders)
    }

    @ParameterizedTest
    @ValueSource(strings = ["2021-01-01", "1994-12-29", "1994-04-14", "1994-12-01"])
    fun `Order supports various dates`(date: String) {
        val orders = OrderFactory().create(listOf("$date S MR"))

        assertEquals(listOf(Order(LocalDate.parse(date), ShipmentSize.S, CarrierCode.MR)), orders)
    }

    @ParameterizedTest
    @ValueSource(strings = ["S", "M", "L"])
    fun `Order supports various shipment sizes`(size: String) {
        val orders = OrderFactory().create(listOf("2021-09-25 $size MR"))

        assertEquals(listOf(Order(LocalDate.parse("2021-09-25"), ShipmentSize.valueOf(size), CarrierCode.MR)), orders)
    }

    @ParameterizedTest
    @ValueSource(strings = ["LP", "MR"])
    fun `Order supports various delivery couriers`(carrierCode: String) {
        val orders = OrderFactory().create(listOf("2021-09-25 S $carrierCode"))

        assertEquals(
            listOf(Order(LocalDate.parse("2021-09-25"), ShipmentSize.S, CarrierCode.valueOf(carrierCode))),
            orders
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["2021-09-25Z14:32:11", "199-12-29", "1994-4-14", "1994-12-1", "1994-13-01", "1994-02-29"])
    fun `Order with incorrect date format is declined`(date: String) {
        val orders = OrderFactory().create(listOf(date))

        assertEquals(listOf(InvalidOrder(date)), orders)
    }

    @Test
    fun `Order with unsupported shipment size is declined`() {
        val orders = OrderFactory().create(listOf("2021-09-25 XXL MR"))

        assertEquals(listOf(InvalidOrder("2021-09-25 XXL MR")), orders)
    }

    @Test
    fun `Order with unsupported delivery carrier code is declined`() {
        val orders = OrderFactory().create(listOf("2021-09-25 S UPS"))

        assertEquals(listOf(InvalidOrder("2021-09-25 S UPS")), orders)
    }
    
}
