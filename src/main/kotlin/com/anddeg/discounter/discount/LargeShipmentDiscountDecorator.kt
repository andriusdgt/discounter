package com.anddeg.discounter.discount

import com.anddeg.discounter.order.Order
import com.anddeg.discounter.order.Orderable
import com.anddeg.discounter.shipment.CarrierCode
import com.anddeg.discounter.shipment.ShipmentSize
import com.anddeg.discounter.transaction.Transaction
import java.time.Month

class LargeShipmentDiscountDecorator(private val source: DiscountDecorator): DiscountDecorator() {

    override fun discount(transactions: List<Transaction>): List<Transaction> {

        val zippedTransactions = zipWithIndex(source.discount(transactions))

        val discountableTransactions =
            getMonthlyIndexedTransactions(zippedTransactions)
                .filter { (_, indexedTransactions) -> containsThirdDiscountableShipment(indexedTransactions) }
                .map { (_, indexedTransactions) -> getThirdDiscountableShipment(indexedTransactions) }

        return zippedTransactions.toMutableList().toDiscounted(discountableTransactions, zippedTransactions)
    }

    private fun getMonthlyIndexedTransactions(zippedTransactions: List<ZippedTransaction>)
        : List<MonthlyZippedTransactions> =
        zippedTransactions
            .filter { (_, transaction) -> isLargeLaPoste(transaction.order) }
            .groupBy { (_, transaction) -> (transaction.order as Order).date.month }
            .toList()

    private fun isLargeLaPoste(o: Orderable) =
        o is Order &&
            o.shipmentSize == ShipmentSize.L &&
            o.carrierCode == CarrierCode.LP

    private fun containsThirdDiscountableShipment(transactions: List<ZippedTransaction>) =
        transactions.size >= 3

    private fun getThirdDiscountableShipment(indexedTransactions: List<ZippedTransaction>) =
        zipWithIndex(indexedTransactions)[2].second

    private fun <T> zipWithIndex(transactions: List<T>) =
        generateSequence(0) { it + 1 }
            .take(transactions.size)
            .toList()
            .zip(transactions)

    private fun MutableList<ZippedTransaction>.toDiscounted(
        discountableTransactions: List<ZippedTransaction>,
        zippedTransactions: List<ZippedTransaction>
    ): List<Transaction> {

        this.replaceAll { (index, transaction) ->
            if (index.matchesDiscountable(discountableTransactions))
                Pair(index, findDiscountedTransaction(zippedTransactions, index))
            else
                Pair(index, transaction)
        }
        return this.map { (_, transaction) -> transaction }
    }

    private fun Int.matchesDiscountable(discountableTransactions: List<ZippedTransaction>) =
        discountableTransactions.map { (index, _) -> index }.contains(this)

    private fun findDiscountedTransaction(zippedTransactions: List<ZippedTransaction>, index: Int) =
        Transaction(
            zippedTransactions[index].second.order,
            zippedTransactions[index].second.price,
            zippedTransactions[index].second.price
        )

}

typealias MonthlyZippedTransactions = Pair<Month, List<ZippedTransaction>>

typealias ZippedTransaction = Pair<Int, Transaction>
