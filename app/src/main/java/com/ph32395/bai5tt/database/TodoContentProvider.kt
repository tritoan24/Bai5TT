package com.ph32395.bai5tt.database


import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class TodoContentProvider : ContentProvider() {

    companion object{
        const val AUTHORITY = "duyndph34554.fpoly.bai_4.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

        private const val TASKS = 1
        private const val TASK_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, TASKS)
            addURI(AUTHORITY, "$TABLE_NAME/#", TASK_ID)
        }
    }

    private lateinit var dbHelper: TodoDatabaseHelper

    override fun onCreate(): Boolean {
        dbHelper = TodoDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        return when(uriMatcher.match(uri)) {
            TASKS -> db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            TASK_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(TABLE_NAME, projection, "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when(uriMatcher.match(uri)) {
            TASKS -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_NAME"
            TASK_ID -> "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        return when(uriMatcher.match(uri)) {
            TASKS -> {
                val id = db.insert(TABLE_NAME, null, values)
                if (id != -1L) {
                    context?.contentResolver?.notifyChange(uri, null)
                    ContentUris.withAppendedId(uri, id)
                } else {
                    null
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            TASKS -> db.delete(TABLE_NAME, selection, selectionArgs)
            TASK_ID -> {
                val id = ContentUris.parseId(uri)
                db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            TASKS -> db.update(TABLE_NAME, values, selection, selectionArgs)
            TASK_ID -> {
                val id = ContentUris.parseId(uri)
                db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}