package com.example.textbookexchangeapp.data.local

        import androidx.room.Entity
        import androidx.room.PrimaryKey

        @Entity(tableName = "books")
        data class Book(
            @PrimaryKey(autoGenerate = true)
            var id: Int = 0,

            var firebaseId: String = "",
            var title: String = "",
            var author: String? = null,
            var price: Double = 0.0,
            var category: String = "",
            var description: String = "",
            var imageUrl: String = "",
            var syncStatus: Int = SYNC_STATUS_SYNCED
        ) {
            // No-arg constructor for Firebase
            constructor() : this(0, "", "", null, 0.0, "", "", "", SYNC_STATUS_SYNCED)

            companion object {
                const val SYNC_STATUS_SYNCED = 0
                const val SYNC_STATUS_PENDING_INSERT = 1
                const val SYNC_STATUS_PENDING_UPDATE = 2
                const val SYNC_STATUS_PENDING_DELETE = 3
            }
        }