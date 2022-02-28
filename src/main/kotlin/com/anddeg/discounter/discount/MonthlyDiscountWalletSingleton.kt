package com.anddeg.discounter.discount

import java.io.File
import java.math.BigInteger

object MonthlyDiscountWalletSingleton {

    private const val WALLET_VALUE_FILE_PATH = "src/main/resources/monthlyDiscountWallet.txt"

    var value: BigInteger = loadWalletValue(WALLET_VALUE_FILE_PATH)
        private set

    fun load(filePath: String) {
        value = loadWalletValue(filePath)
    }

    fun loadDefault() {
        value = loadWalletValue(WALLET_VALUE_FILE_PATH)
    }

    private fun loadWalletValue(filePath: String) = BigInteger.valueOf(readNumberFromFile(filePath).toLong())

    private fun readNumberFromFile(filePath: String) = File(filePath).readLines()[0]

}
