package com.anddeg.discounter

import com.anddeg.discounter.discount.Discounter
import com.anddeg.discounter.order.OrderFileReader
import com.anddeg.discounter.order.Orderable
import com.anddeg.discounter.transaction.Transaction
import com.anddeg.discounter.transaction.TransactionFactory
import com.anddeg.discounter.transaction.TransactionFormatter

class Main(private val outputWriter: OutputWriter = OutputWriter()) {

    fun execute(inputFilePath: String) =
        inputFilePath.toOrders().toTransactions().toDiscounted().toFormatted().writeOutput()

    private fun String.toOrders() = OrderFileReader().createOrders(this)

    private fun String.writeOutput() = outputWriter.write(this)

    private fun List<Orderable>.toTransactions() = TransactionFactory().create(this)

    private fun List<Transaction>.toDiscounted() = Discounter().discount(this)

    private fun List<Transaction>.toFormatted() = TransactionFormatter().format(this)

}

fun main(args: Array<String>) {

    if (args.isEmpty()) {

        println("Using default file path \"input.txt\"")

        Main().execute("input.txt")

        return
    }

    Main().execute(args[0])

}
