package com.example.movies
import  android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.provider.BaseColumns

class MovieProvider : ContentProvider() {
    private lateinit var mOpenHelper: MovieDbHelper

    companion object {
        const val AUTHORITY = "com.example.movies.provider"
        const val TABLE_NAME = "movies"
        const val CODE_MOVIES = 100
        const val CODE_MOVIE_WITH_ID = 101
        val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, CODE_MOVIES)
        sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", CODE_MOVIE_WITH_ID)
    }

    override fun onCreate(): Boolean {
        mOpenHelper = MovieDbHelper(context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mOpenHelper.writableDatabase
        val id: Long
        when (sUriMatcher.match(uri)) {
            CODE_MOVIES -> id = db.insertOrThrow(TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(uri, id.toString())
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = mOpenHelper.readableDatabase
        val cursor: Cursor
        when (sUriMatcher.match(uri)) {
            CODE_MOVIES -> cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            CODE_MOVIE_WITH_ID -> {
                val id = uri.lastPathSegment
                cursor = db.query(
                    TABLE_NAME,
                    projection,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = mOpenHelper.writableDatabase
        val count: Int
        when (sUriMatcher.match(uri)) {
            CODE_MOVIES -> count = db.update(TABLE_NAME, values, selection, selectionArgs)
            CODE_MOVIE_WITH_ID -> {
                val id = uri.lastPathSegment
                count = db.update(
                    TABLE_NAME,
                    values,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id)
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper.writableDatabase
        val count: Int
        when (sUriMatcher.match(uri)) {
            CODE_MOVIES -> count = db.delete(TABLE_NAME, selection, selectionArgs)
            CODE_MOVIE_WITH_ID -> {
                val id = uri.lastPathSegment
                count = db.delete(
                    TABLE_NAME,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id)
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? {
        return when (sUriMatcher.match(uri)) {
            CODE_MOVIES -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_NAME"
            CODE_MOVIE_WITH_ID -> "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}