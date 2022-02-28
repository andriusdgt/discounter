package com.anddeg.discounter.discount

import com.anddeg.discounter.order.InvalidOrder
import com.anddeg.discounter.order.Order
import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import com.anddeg.discounter.transaction.Transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import java.math.BigInteger
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class LargeShipmentDiscountTest {

    private val largeLPOrder = Order(LocalDate.of(2021, 9, 25), ShipmentSize.L, CarrierCode.LP)
    private val mediumLPOrder = Order(LocalDate.of(2021, 9, 25), ShipmentSize.M, CarrierCode.LP)
    private val nextMonthLargeMROrder = Order(LocalDate.of(2021, 10, 1), ShipmentSize.L, CarrierCode.LP)

    @Mock
    private lateinit var sourceDecoratorStub: DiscountDecorator

    @BeforeEach
    fun setUp(){
        doAnswer { invoke -> invoke.arguments[0] }.`when`(sourceDecoratorStub).discount(any())
    }

    @Test
    fun `Reads init transactions from source decorator`() {
        val transactions = listOf(Transaction(InvalidOrder(), BigInteger.valueOf(0)))

        LargeShipmentDiscountDecorator(sourceDecoratorStub).discount(transactions)

        verify(sourceDecoratorStub).discount(transactions)
    }

    @Test
    fun `Third large shipment from LP provider is free every month`() {
        val largeMROrder = Order(LocalDate.of(2021, 9, 25), ShipmentSize.L, CarrierCode.MR)
        val transactions = listOf(
            Transaction(InvalidOrder(), BigInteger.valueOf(0)),
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(largeMROrder, BigInteger.valueOf(690)),
            Transaction(mediumLPOrder, BigInteger.valueOf(490)),
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(largeLPOrder, BigInteger.valueOf(690))
        )

        val discountedTransactions = LargeShipmentDiscountDecorator(sourceDecoratorStub).discount(transactions)

        assertEquals(
            listOf(
                Transaction(InvalidOrder(), BigInteger.valueOf(0), BigInteger.valueOf(0)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(largeMROrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(mediumLPOrder, BigInteger.valueOf(490), BigInteger.valueOf(0)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(690)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
            ),
            discountedTransactions
        )
    }

    @Test
    fun `Order count resets on new calendar month`() {
        val transactions = listOf(
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690)),
        )

        val discountedTransactions = LargeShipmentDiscountDecorator(sourceDecoratorStub).discount(transactions)

        assertEquals(
            listOf(
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
            ),
            discountedTransactions
        )
    }

    @Test
    fun `Discount is also applied on next month`() {
        val transactions = listOf(
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(largeLPOrder, BigInteger.valueOf(690)),
            Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690)),
            Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690)),
            Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690))
        )

        val discountedTransactions = LargeShipmentDiscountDecorator(sourceDecoratorStub).discount(transactions)

        assertEquals(
            listOf(
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(largeLPOrder, BigInteger.valueOf(690), BigInteger.valueOf(690)),
                Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690), BigInteger.valueOf(0)),
                Transaction(nextMonthLargeMROrder, BigInteger.valueOf(690), BigInteger.valueOf(690))
            ),
            discountedTransactions
        )
    }

}
