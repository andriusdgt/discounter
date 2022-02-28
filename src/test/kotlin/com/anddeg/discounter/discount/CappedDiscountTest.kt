package com.anddeg.discounter.discount

import com.anddeg.discounter.order.InvalidOrder
import com.anddeg.discounter.order.Order
import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import com.anddeg.discounter.transaction.Transaction
import org.junit.jupiter.api.AfterEach
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
class CappedDiscountTest {

    private val septemberOrder = Order(LocalDate.of(2021, 9, 25), ShipmentSize.M, CarrierCode.LP)
    private val octoberOrder = Order(LocalDate.of(2021, 10, 1), ShipmentSize.M, CarrierCode.LP)

    @Mock
    private lateinit var sourceDecoratorStub: DiscountDecorator

    @BeforeEach
    fun setUp(){
        doAnswer { invoke -> invoke.arguments[0] }.`when`(sourceDecoratorStub).discount(any())
        MonthlyDiscountWalletSingleton.load("src/test/resources/monthlyDiscountWallet.txt")
    }

    @AfterEach
    fun tearDown(){
        MonthlyDiscountWalletSingleton.loadDefault()
    }

    @Test
    fun `Reads init transactions from source decorator`() {
        val transactions = listOf(Transaction(InvalidOrder(), BigInteger.valueOf(0)))

        CappedDiscountDecorator(sourceDecoratorStub).discount(transactions)

        verify(sourceDecoratorStub).discount(transactions)
    }

    @Test
    fun `All discounts are capped by calendar month (at 8 eur per month)`() {
        val transactions = listOf(
            Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(400)),
            Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(300)),
            Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(200)),
            Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(100)),
        )

        val discountedTransactions = CappedDiscountDecorator(sourceDecoratorStub).discount(transactions)

        assertEquals(
            listOf(
                Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(400)),
                Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(300)),
                Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(100)),
                Transaction(septemberOrder, BigInteger.valueOf(420), BigInteger.valueOf(0))
            ),
            discountedTransactions
        )
    }

    @Test
    fun `Capped discounts reset on next month`() {
        val transactions = listOf(
            Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
            Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
            Transaction(octoberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
            Transaction(octoberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600))
        )

        val discountedTransactions = CappedDiscountDecorator(sourceDecoratorStub).discount(transactions)

        assertEquals(
            listOf(
                Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
                Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(200)),
                Transaction(octoberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
                Transaction(octoberOrder, BigInteger.valueOf(650), BigInteger.valueOf(200))
            ),
            discountedTransactions
        )
    }

    @Test
    fun `Invalid orders are ignored and put into the end of month list`() {
        val transactions = listOf(
            Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
            Transaction(InvalidOrder(), BigInteger.valueOf(0), BigInteger.valueOf(0)),
            Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
            Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
            Transaction(octoberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600))
        )

        val discountedTransactions = CappedDiscountDecorator(sourceDecoratorStub).discount(transactions)

        assertEquals(
            listOf(
                Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600)),
                Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(200)),
                Transaction(septemberOrder, BigInteger.valueOf(650), BigInteger.valueOf(0)),
                Transaction(InvalidOrder(), BigInteger.valueOf(0), BigInteger.valueOf(0)),
                Transaction(octoberOrder, BigInteger.valueOf(650), BigInteger.valueOf(600))
            ),
            discountedTransactions
        )
    }

}
