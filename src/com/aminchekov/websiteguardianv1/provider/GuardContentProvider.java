package com.aminchekov.websiteguardianv1.provider;

import com.aminchekov.websiteguardianv1.database.StatusData;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class GuardContentProvider extends ContentProvider {

	private static final String TAG = GuardContentProvider.class
			.getSimpleName();
	StatusData statusData;
	/** Authority string for this provider. */
	public static final String AUTHORITY = "com.aminchekov.websiteguardianv1.provider";

	/**
	 * The content:// style URL for this provider
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	/**
	 * Contains the web guardian responses.
	 */
	public static class Responses implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/responsedata");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * responses.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aminchekov.mresponse";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * word.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aminchekov.response";

		public static final String _ID = BaseColumns._ID;

		public static final String C_CREATED_AT = "created_at";
		public static final String C_CODE = "status_code";
		public final static String DEFAULT_SOTR_ODRER =  _ID + " desc";
	}

	@Override
	public boolean onCreate() {
		statusData = new StatusData(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		long id = getId(uri);
		Cursor resultCursor;
		SQLiteDatabase db = statusData.getDbHelper().getReadableDatabase();
		String orderBy = TextUtils.isEmpty(sortOrder) ? Responses.DEFAULT_SOTR_ODRER : sortOrder;
		if (id < 0) {
			resultCursor = db.query(StatusData.TABLE, projection, selection,
					selectionArgs, null, null, orderBy);
		} else {
			resultCursor = db.query(StatusData.TABLE, projection, StatusData.C_ID + "="
					+ id, null, null, null, orderBy); //
		}
		resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return resultCursor;
	}

	@Override
	public String getType(Uri uri) {
		return getId(uri) < 0 ? Responses.CONTENT_TYPE : Responses.CONTENT_ITEM_TYPE;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri result = null;
		long id = statusData.insertOrIgnore(values);
		if (id == -1) {
			throw new RuntimeException(String.format(
					"%s: Failed to insert [%s] to [%s] for unknown reasons.",
					TAG, values, uri)); //
		} else {
			result = ContentUris.withAppendedId(uri, id);
			getContext().getContentResolver().notifyChange(uri, null);
			return result;
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		long id = getId(uri);
		int count;
		SQLiteDatabase db = statusData.getDbHelper().getWritableDatabase();
		try {
			if (id < 0) {
				count = db.delete(StatusData.TABLE, selection, selectionArgs);
			} else
				count = db.delete(StatusData.TABLE, StatusData.C_ID + "=" + id,
						null);
		} finally {
			db.close();
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	private long getId(Uri uri) {
		String lastPathSegment = uri.getLastPathSegment(); //
		if (lastPathSegment != null) {
			try {
				return Long.parseLong(lastPathSegment); //
			} catch (NumberFormatException e) { //
				// at least we tried
			}
		}
		return -1;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
