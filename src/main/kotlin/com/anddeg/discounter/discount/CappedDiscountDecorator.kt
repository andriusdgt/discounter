package com.anddeg.discounter.discount

import com.anddeg.discounter.order.Order
import com.anddeg.discounter.order.Orderable
import com.anddeg.discounter.transaction.Transaction
import java.math.BigInteger
import java.time.LocalDate

class CappedDiscountDecorator(private val source: DiscountDecorator): DiscountDecorator() {

    override fun discount(transactions: List<Transaction>): List<Transaction> =
        getMonthlyDiscountLedger(transactions)
            .map { discountLedger ->
                discountLedger.map { (discountWallet, transaction) ->
                    getDiscountedTransaction(transaction, discountWallet)
                }
            }
            .flatten()

    private fun getMonthlyDiscountLedger(transactions: List<Transaction>): List<DiscountLedger> =
        source
            .discount(transactions)
            .groupBy { getMonth(it.order) }
            .toList()
            .map { (_, monthlyTransactions) -> withDiscountWalletValueZipped(monthlyTransactions) }

    private fun getDiscountedTransaction(transaction: Transaction, discountWallet: DiscountWallet): Transaction {
        var cappedDiscount = transaction.discount

        if (!isFullyDiscountable(discountWallet, transaction))
            cappedDiscount = getPartialDiscount(transaction, discountWallet)

        if (isWalletEmpty(discountWallet))
            cappedDiscount = BigInteger.ZERO

        return Transaction(transaction.order, transaction.price, cappedDiscount)
    }

    private fun getMonth(order: Orderable) =
        if (order is Order)
            order.date.month
        else
            LocalDate.EPOCH

    private fun withDiscountWalletValueZipped(transactions: List<Transaction>): DiscountLedger =
        transactions
            .runningFold(MonthlyDiscountWalletSingleton.value) { wallet, t -> wallet - t.discount }
            .zip(transactions)

    private fun isFullyDiscountable(discountWallet: DiscountWallet, transaction: Transaction) =
        discountWallet >= transaction.discount

    private fun isWalletEmpty(discountWallet: DiscountWallet) =
        discountWallet <= BigInteger.ZERO

    private fun getPartialDiscount(transaction: Transaction, discountWallet: DiscountWallet) =
        transaction.discount - (transaction.discount - discountWallet)

}

typealias DiscountLedger = List<Pair<DiscountWallet, Transaction>>

typealias DiscountWallet = BigInteger
