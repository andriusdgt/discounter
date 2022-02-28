package com.anddeg.discounter.transaction

import com.anddeg.discounter.order.InvalidOrder
import com.anddeg.discounter.order.Order
import java.math.BigInteger
import java.text.DecimalFormat

class TransactionFormatter {

    fun format(transactions: List<Transaction>): String =
        transactions.fold("") { accumulator, transaction ->
            if (transaction.order is InvalidOrder)
                return@fold accumulator + formatInvalidOrder(transaction.order)

            format(transaction, accumulator)
        }

    private fun format(transaction: Transaction, accumulator: String) =
        accumulator + (transaction.order as Order).run {
            "${this.date} ${this.shipmentSize} ${this.carrierCode}" +
                " ${formatPrice(transaction.price - transaction.discount)}" +
                " ${formatDiscount(transaction.discount)}\n"
        }

    private fun formatInvalidOrder(order: InvalidOrder) = "${order.orderContent} Ignored\n"

    private fun formatDiscount(value: BigInteger) = if (formatPrice(value) != "0.00") formatPrice(value) else "-"

    private fun formatPrice(value: BigInteger) = DecimalFormat("0.00").format(value.intValueExact() / 100f)

}
