package com.example.textbookexchangeapp

                        import android.content.Context
                        import android.net.ConnectivityManager
                        import android.net.Network
                        import android.os.Bundle
                        import android.util.Log
                        import androidx.activity.ComponentActivity
                        import androidx.activity.compose.setContent
                        import androidx.activity.enableEdgeToEdge
                        import androidx.navigation.compose.rememberNavController
                        import com.example.textbookexchangeapp.data.local.AppDatabase
                        import com.example.textbookexchangeapp.data.local.BookRepository
                        import com.example.textbookexchangeapp.data.local.BookViewModel
                        import com.example.textbookexchangeapp.data.remote.ApiClient
                        import com.example.textbookexchangeapp.navigation.AppNavigation
                        import com.example.textbookexchangeapp.ui.theme.TextbookExchangeAppTheme
                        import kotlinx.coroutines.CoroutineScope
                        import kotlinx.coroutines.Dispatchers
                        import kotlinx.coroutines.launch

                        class MainActivity : ComponentActivity() {
                            private lateinit var bookRepository: BookRepository
                            private lateinit var connectivityManager: ConnectivityManager
                            private val TAG = "MainActivity"

                            override fun onCreate(savedInstanceState: Bundle?) {
                                super.onCreate(savedInstanceState)
                                enableEdgeToEdge()

                                val database = AppDatabase.getDatabase(applicationContext)
                                val apiService = ApiClient.apiService
                                bookRepository = BookRepository(database.bookDao())
                                val viewModel = BookViewModel(bookRepository)

                                // Setup network connectivity monitoring
                                setupNetworkObserver()

                                setContent {
                                    TextbookExchangeAppTheme {
                                        val navController = rememberNavController()
                                        AppNavigation(navController, viewModel)
                                    }
                                }
                            }

                            private fun setupNetworkObserver() {
                                connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                                // Create network callback
                                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                                    override fun onAvailable(network: Network) {
                                        Log.d(TAG, "Network available")
                                        bookRepository.setNetworkStatus(true)

                                        // Trigger sync when network becomes available
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                bookRepository.syncPendingChanges()
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Error syncing on network available: ${e.message}")
                                            }
                                        }
                                    }

                                    override fun onLost(network: Network) {
                                        Log.d(TAG, "Network lost")
                                        bookRepository.setNetworkStatus(false)
                                    }
                                }

                                // Check current connectivity and set initial state
                                val currentNetwork = connectivityManager.activeNetwork
                                val isConnected = currentNetwork != null
                                bookRepository.setNetworkStatus(isConnected)
                                Log.d(TAG, "Initial network state: ${if (isConnected) "CONNECTED" else "DISCONNECTED"}")

                                // Register the callback to monitor future changes
                                connectivityManager.registerDefaultNetworkCallback(networkCallback)
                            }

                            override fun onDestroy() {
                                super.onDestroy()
                                // Clean up any resources if needed
                                try {
                                    val networkCallback = object : ConnectivityManager.NetworkCallback() {}
                                    connectivityManager.unregisterNetworkCallback(networkCallback)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error unregistering network callback: ${e.message}")
                                }
                            }
                        }