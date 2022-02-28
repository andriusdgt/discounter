package com.anddeg.discounter.transaction

import com.anddeg.discounter.order.InvalidOrder
import com.anddeg.discounter.order.Order
import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigInteger
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class TransactionFormatterTest {

    @Test
    fun `Formats empty invalid order`() {
        val transaction = Transaction(InvalidOrder(), BigInteger.valueOf(0))

        val output = TransactionFormatter().format(listOf(transaction))

        assertEquals(" Ignored\n", output)
    }

    @Test
    fun `Formats invalid order`() {
        val transaction = Transaction(InvalidOrder("foo#bar#baz"), BigInteger.valueOf(0))

        val output = TransactionFormatter().format(listOf(transaction))

        assertEquals("foo#bar#baz Ignored\n", output)
    }

    @Test
    fun `Formats undiscounted order`() {
        val largeLPOrder = Order(LocalDate.of(2021, 9, 25), ShipmentSize.L, CarrierCode.LP)
        val transaction = Transaction(largeLPOrder, BigInteger.valueOf(690))

        val output = TransactionFormatter().format(listOf(transaction))

        assertEquals("2021-09-25 L LP 6.90 -\n", output)
    }

    @Test
    fun `Formats discounted order`() {
        val smallMROrder = Order(LocalDate.of(2021, 9, 9), ShipmentSize.S, CarrierCode.MR)
        val transaction = Transaction(smallMROrder, BigInteger.valueOf(500), BigInteger.valueOf(200))

        val output = TransactionFormatter().format(listOf(transaction))

        assertEquals("2021-09-09 S MR 3.00 2.00\n", output)
    }

    @Test
    fun `Formats several orders`() {
        val smallMROrder = Order(LocalDate.of(2021, 9, 9), ShipmentSize.S, CarrierCode.MR)
        val smallLPOrder = Order(LocalDate.of(2021, 9, 10), ShipmentSize.S, CarrierCode.LP)

        val invalidTransaction = Transaction(InvalidOrder("2021-09-09 CUSPS"))
        val MRTransaction = Transaction(smallMROrder, BigInteger.valueOf(199), BigInteger.valueOf(0))
        val LPTransaction = Transaction(smallLPOrder, BigInteger.valueOf(299), BigInteger.valueOf(50))

        val output = TransactionFormatter().format(listOf(MRTransaction, invalidTransaction, LPTransaction))

        assertEquals(
            "" +
                "2021-09-09 S MR 1.99 -\n" +
                "2021-09-09 CUSPS Ignored\n" +
                "2021-09-10 S LP 2.49 0.50\n",
            output
        )
    }

}
