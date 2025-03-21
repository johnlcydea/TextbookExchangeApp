package com.example.textbookexchangeapp.data.local

                                                                import android.util.Log
                                                                import androidx.lifecycle.LiveData
                                                                import androidx.lifecycle.MutableLiveData
                                                                import androidx.lifecycle.asLiveData
                                                                import com.google.firebase.firestore.FirebaseFirestore
                                                                import com.google.firebase.firestore.toObject
                                                                import kotlinx.coroutines.CoroutineScope
                                                                import kotlinx.coroutines.Dispatchers
                                                                import kotlinx.coroutines.launch
                                                                import kotlinx.coroutines.tasks.await
                                                                import kotlinx.coroutines.withContext
                                                                import com.example.textbookexchangeapp.data.remote.ApiService
                                                                import com.example.textbookexchangeapp.data.remote.ApiClient

                                                                class BookRepository(private val bookDao: BookDao) {
                                                                    private val TAG = "BookRepository"

                                                                    // API Service
                                                                    private val apiService: ApiService = ApiClient.apiService

                                                                    // Firebase Firestore
                                                                    private val firestore = FirebaseFirestore.getInstance()
                                                                    private val booksCollection = firestore.collection("books")

                                                                    // LiveData for Firestore books
                                                                    private val _firestoreBooks = MutableLiveData<List<Book>>()
                                                                    val firestoreBooks: LiveData<List<Book>> get() = _firestoreBooks

                                                                    // LiveData for local database
                                                                    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks().asLiveData()

                                                                    // Remote Data - Fetch Books from API
                                                                    private val _remoteBooks = MutableLiveData<List<Book>>()
                                                                    val remoteBooks: LiveData<List<Book>> get() = _remoteBooks

                                                                    // Network status
                                                                    private var isOnline = false

                                                                    init {
                                                                        fetchBooksFromFirestore()
                                                                    }

                                                                    // Set network status
                                                                    fun setNetworkStatus(online: Boolean) {
                                                                        val wasOffline = !isOnline
                                                                        isOnline = online

                                                                        if (isOnline && wasOffline) {
                                                                            // We just came back online, sync pending changes
                                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                                syncPendingChanges()
                                                                            }
                                                                        }
                                                                        Log.d(TAG, "Network status changed: " + if (isOnline) "ONLINE" else "OFFLINE")
                                                                    }

                                                                    // Sync all pending changes with Firestore
                                                                    suspend fun syncPendingChanges() {
                                                                        if (!isOnline) {
                                                                            Log.d(TAG, "Cannot sync: Device is offline")
                                                                            return
                                                                        }

                                                                        withContext(Dispatchers.IO) {
                                                                            try {
                                                                                val pendingInserts = bookDao.getBooksByStatus(Book.SYNC_STATUS_PENDING_INSERT)
                                                                                val pendingUpdates = bookDao.getBooksByStatus(Book.SYNC_STATUS_PENDING_UPDATE)
                                                                                val pendingDeletes = bookDao.getBooksByStatus(Book.SYNC_STATUS_PENDING_DELETE)

                                                                                Log.d(TAG, "Starting sync: ${pendingInserts.size} inserts, ${pendingUpdates.size} updates, ${pendingDeletes.size} deletes")

                                                                                // Process inserts
                                                                                for (book in pendingInserts) {
                                                                                    try {
                                                                                        val docRef = booksCollection.document()
                                                                                        val bookToSave = book.copy(firebaseId = docRef.id, syncStatus = Book.SYNC_STATUS_SYNCED)
                                                                                        docRef.set(bookToSave).await()
                                                                                        bookDao.updateBook(bookToSave)
                                                                                        Log.d(TAG, "Synced pending insert for book: ${book.title}")
                                                                                    } catch (e: Exception) {
                                                                                        Log.e(TAG, "Failed to sync insert for book: ${book.title}, ${e.message}")
                                                                                    }
                                                                                }

                                                                                // Process updates
                                                                                for (book in pendingUpdates) {
                                                                                    try {
                                                                                        if (book.firebaseId.isNotEmpty()) {
                                                                                            val bookToUpdate = book.copy(syncStatus = Book.SYNC_STATUS_SYNCED)
                                                                                            booksCollection.document(book.firebaseId).set(bookToUpdate).await()
                                                                                            bookDao.updateBook(bookToUpdate)
                                                                                            Log.d(TAG, "Synced pending update for book: ${book.title}")
                                                                                        } else {
                                                                                            // No firebaseId, treat as insert instead
                                                                                            val docRef = booksCollection.document()
                                                                                            val bookToSave = book.copy(firebaseId = docRef.id, syncStatus = Book.SYNC_STATUS_SYNCED)
                                                                                            docRef.set(bookToSave).await()
                                                                                            bookDao.updateBook(bookToSave)
                                                                                            Log.d(TAG, "Converted update to insert for book: ${book.title}")
                                                                                        }
                                                                                    } catch (e: Exception) {
                                                                                        Log.e(TAG, "Failed to sync update for book: ${book.title}, ${e.message}")
                                                                                    }
                                                                                }

                                                                                // Process deletes
                                                                                for (book in pendingDeletes) {
                                                                                    try {
                                                                                        if (book.firebaseId.isNotEmpty()) {
                                                                                            booksCollection.document(book.firebaseId).delete().await()
                                                                                            bookDao.deleteBook(book)
                                                                                            Log.d(TAG, "Synced pending delete for book: ${book.title}")
                                                                                        } else {
                                                                                            // Just delete locally if no Firebase ID
                                                                                            bookDao.deleteBook(book)
                                                                                            Log.d(TAG, "Deleted local-only book: ${book.title}")
                                                                                        }
                                                                                    } catch (e: Exception) {
                                                                                        Log.e(TAG, "Failed to sync delete for book: ${book.title}, ${e.message}")
                                                                                    }
                                                                                }

                                                                            } catch (e: Exception) {
                                                                                Log.e(TAG, "Error during sync process: ${e.message}")
                                                                            }
                                                                        }
                                                                    }

                                                                    // Firestore Methods
                                                                    private fun fetchBooksFromFirestore() {
                                                                        booksCollection.addSnapshotListener { snapshot, e ->
                                                                            if (e != null) {
                                                                                Log.e(TAG, "Error fetching books from Firestore: ${e.message}")
                                                                                return@addSnapshotListener
                                                                            }

                                                                            try {
                                                                                val bookList = snapshot?.documents?.mapNotNull { document ->
                                                                                    try {
                                                                                        val book = document.toObject(Book::class.java)
                                                                                        book?.apply {
                                                                                            // Set the firebaseId from the Firestore document ID
                                                                                            if (firebaseId.isEmpty()) {
                                                                                                firebaseId = document.id
                                                                                            }
                                                                                            syncStatus = Book.SYNC_STATUS_SYNCED
                                                                                        }
                                                                                        book
                                                                                    } catch (e: Exception) {
                                                                                        Log.e(TAG, "Error converting document to Book: ${e.message}")
                                                                                        null
                                                                                    }
                                                                                }
                                                                                _firestoreBooks.value = bookList ?: emptyList()
                                                                                Log.d(TAG, "Fetched " + (bookList?.size ?: 0) + " books from Firestore")
                                                                            } catch (e: Exception) {
                                                                                Log.e(TAG, "Error processing Firestore snapshot: ${e.message}")
                                                                                _firestoreBooks.value = emptyList()
                                                                            }
                                                                        }
                                                                    }

                                                                    // REST API Methods
                                                                    suspend fun fetchBooksFromApi() {
                                                                        withContext(Dispatchers.IO) {
                                                                            try {
                                                                                val response = apiService.getBooks()
                                                                                if (response.isSuccessful) {
                                                                                    val books = response.body() ?: emptyList()
                                                                                    _remoteBooks.postValue(books)
                                                                                    Log.d(TAG, "Successfully fetched ${books.size} books from API")
                                                                                } else {
                                                                                    Log.e(TAG, "Error fetching books: ${response.code()} - ${response.message()}")
                                                                                }
                                                                            } catch (e: Exception) {
                                                                                Log.e(TAG, "Error fetching books from API: ${e.message}")
                                                                            }
                                                                        }
                                                                    }

                                                                    // Insert Book
                                                                    suspend fun insertBook(book: Book) {
                                                                        withContext(Dispatchers.IO) {
                                                                            try {
                                                                                // Mark appropriate sync status based on network
                                                                                val syncStatus = if (isOnline) Book.SYNC_STATUS_SYNCED else Book.SYNC_STATUS_PENDING_INSERT
                                                                                val bookToInsert = book.copy(syncStatus = syncStatus)

                                                                                // Save Locally - this will generate a new ID
                                                                                val localId = bookDao.insertBook(bookToInsert)

                                                                                // Create a copy with the generated local ID
                                                                                val bookWithId = bookToInsert.copy(id = localId.toInt())

                                                                                // If online, save to Firestore immediately
                                                                                if (isOnline) {
                                                                                    try {
                                                                                        val docRef = booksCollection.document()
                                                                                        val bookWithFirebaseId = bookWithId.copy(firebaseId = docRef.id)
                                                                                        docRef.set(bookWithFirebaseId).await()

                                                                                        // Update local copy with Firebase ID
                                                                                        bookDao.updateBook(bookWithFirebaseId)
                                                                                        Log.d(TAG, "Book inserted online with firebaseId: ${docRef.id}")
                                                                                    } catch (e: Exception) {
                                                                                        // Mark for future sync if Firebase fails
                                                                                        bookDao.updateBook(bookWithId.copy(syncStatus = Book.SYNC_STATUS_PENDING_INSERT))
                                                                                        Log.e(TAG, "Failed Firestore insert, marked for future sync: ${e.message}")
                                                                                    }
                                                                                } else {
                                                                                    Log.d(TAG, "Book inserted locally while offline, will sync later")
                                                                                }
                                                                            } catch (e: Exception) {
                                                                                Log.e(TAG, "Error inserting book: ${e.message}")
                                                                            }
                                                                        }
                                                                    }

                                                                    // Update Book
                                                                    suspend fun updateBook(book: Book) {
                                                                        withContext(Dispatchers.IO) {
                                                                            try {
                                                                                // Determine sync status based on network and existing firebaseId
                                                                                val syncStatus = if (!isOnline || book.firebaseId.isEmpty()) {
                                                                                    Book.SYNC_STATUS_PENDING_UPDATE
                                                                                } else {
                                                                                    Book.SYNC_STATUS_SYNCED
                                                                                }

                                                                                val bookToUpdate = book.copy(syncStatus = syncStatus)

                                                                                // Update Locally
                                                                                bookDao.updateBook(bookToUpdate)
                                                                                Log.d(TAG, "Book updated locally with sync status: $syncStatus")

                                                                                // If online and has firebaseId, update Firestore immediately
                                                                                if (isOnline && book.firebaseId.isNotEmpty()) {
                                                                                    try {
                                                                                        booksCollection.document(book.firebaseId).set(bookToUpdate).await()
                                                                                        Log.d(TAG, "Book updated in Firestore")
                                                                                    } catch (e: Exception) {
                                                                                        // Mark for future sync if Firebase update fails
                                                                                        bookDao.updateBook(book.copy(syncStatus = Book.SYNC_STATUS_PENDING_UPDATE))
                                                                                        Log.e(TAG, "Failed Firestore update, marked for future sync: ${e.message}")
                                                                                    }
                                                                                }
                                                                            } catch (e: Exception) {
                                                                                Log.e(TAG, "Error updating book: ${e.message}")
                                                                            }
                                                                        }
                                                                    }

                                                                    // Delete Book
                                                                    suspend fun deleteBook(book: Book) {
                                                                        withContext(Dispatchers.IO) {
                                                                            try {
                                                                                if (isOnline && book.firebaseId.isNotEmpty()) {
                                                                                    // Delete from Firestore first if online
                                                                                    try {
                                                                                        booksCollection.document(book.firebaseId).delete().await()
                                                                                        // Then delete locally
                                                                                        bookDao.deleteBook(book)
                                                                                        Log.d(TAG, "Book deleted from Firestore and locally")
                                                                                    } catch (e: Exception) {
                                                                                        // Mark for deletion later
                                                                                        bookDao.updateBook(book.copy(syncStatus = Book.SYNC_STATUS_PENDING_DELETE))
                                                                                        Log.e(TAG, "Failed Firestore delete, marked for future delete: ${e.message}")
                                                                                    }
                                                                                } else if (book.firebaseId.isEmpty()) {
                                                                                    // Book only exists locally, just delete it
                                                                                    bookDao.deleteBook(book)
                                                                                    Log.d(TAG, "Local-only book deleted")
                                                                                } else {
                                                                                    // Offline with firebaseId - mark for deletion later
                                                                                    bookDao.updateBook(book.copy(syncStatus = Book.SYNC_STATUS_PENDING_DELETE))
                                                                                    Log.d(TAG, "Offline book marked for deletion when online")
                                                                                }
                                                                            } catch (e: Exception) {
                                                                                Log.e(TAG, "Error deleting book: ${e.message}")
                                                                            }
                                                                        }
                                                                    }

                                                                    // Get Book by ID from Local Database
                                                                    fun getBookById(id: Int): LiveData<Book?> {
                                                                        return bookDao.getBookById(id).asLiveData()
                                                                    }

                                                                    // Get Books by Category from Local Database
                                                                    fun getBooksByCategory(category: String): LiveData<List<Book>> {
                                                                        return bookDao.getBooksByCategory(category).asLiveData()
                                                                    }
                                                                }