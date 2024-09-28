package com.example.nftrewards

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.nftrewards.ui.theme.NFTRewardsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider

class MainActivity : ComponentActivity() {
    private val INFURA_URL = "https://sepolia.infura.io/v3/0d485fe93d7d47c38e26246f2b20d414"
    private val PRIVATE_KEY = "768c1c9ad6e374c87ce6ecc140c230f759606c594a52b08796d37f700875a1ba"

    private lateinit var web3j: Web3j
    private lateinit var balanceChecker: FirstContract
    private lateinit var credentials: Credentials

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NFTRewardsTheme {
                web3j = Web3j.build(HttpService(INFURA_URL))
                credentials = Credentials.create(PRIVATE_KEY)
                val contractAddress = "0x6160D0Ca6ad8AA9Cc68d143D01591d8050b7dD9f"
                balanceChecker = FirstContract.load(contractAddress, web3j, credentials, DefaultGasProvider())

                LaunchedEffect(Unit) {
                    fetchAccountBalance("0x1234567890abcdef1234567890abcdef12345678")
                }
            }
        }
    }

    private fun fetchAccountBalance(address: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("balance", "Starting balance call for address: $address\ncontract Address ${balanceChecker.contractAddress}" )
                val balance = balanceChecker.getBalance(address).send()
                Log.d("balance", "Fetched balance: ${balance.toString()}")
            } catch (e: Exception) {
                Log.e("balance", "Error fetching balance", e)
            }
        }
    }
}
