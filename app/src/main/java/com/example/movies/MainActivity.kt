package com.example.movies

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertButton.setOnClickListener {
            val values = ContentValues()
            values.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, titleEditText.text.toString())
            values.put(MovieContract.MovieEntry.COLUMN_NAME_YEAR, yearEditText.text.toString())
            values.put(MovieContract.MovieEntry.COLUMN_NAME_RATING, ratingEditText.text.toString())

            val uri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, values)
            Toast.makeText(this, "Inserted movie with URI: $uri", Toast.LENGTH_LONG).show()
        }

        queryButton.setOnClickListener {
            val cursor = contentResolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            val result = "Results: "
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_TITLE))
                    val year = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_YEAR))
                    val rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME_RATING))
                    result += "\nTitle: $title, Year: $year, Rating: $rating"
                } while (cursor.moveToNext())
            }
            cursor.close()
            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
        }

        updateButton.setOnClickListener {
            val values = ContentValues()
            values.put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, titleEditText.text.toString())
            values.put(MovieContract.MovieEntry.COLUMN_NAME_YEAR, yearEditText.text.toString())
            values.put(MovieContract.MovieEntry.COLUMN_NAME_RATING, ratingEditText.text.toString())

            val selection = "${MovieContract.MovieEntry._ID} = ?"
            val selectionArgs = arrayOf(idEditText.text.toString())
            val count = contentResolver.update(
                MovieContract.MovieEntry.CONTENT_URI,
                values,
                selection,
                selectionArgs
            )
            Toast.makeText(this, "Updated $count rows", Toast.LENGTH_LONG).show()
        }

        deleteButton.setOnClickListener {
            val selection = "${MovieContract.MovieEntry._ID} = ?"
            val selectionArgs = arrayOf(idEditText.text.toString())
            val count = contentResolver.delete(
                MovieContract.MovieEntry.CONTENT_URI,
                selection,
                selectionArgs
            )
            Toast.makeText(this, "Deleted $count rows", Toast.LENGTH_LONG).show()
        }
    }
}