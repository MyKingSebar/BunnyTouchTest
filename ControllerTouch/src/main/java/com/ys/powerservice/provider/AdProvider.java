package com.ys.powerservice.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by yanwei on 2017/4/19.
 */

public class AdProvider extends ContentProvider{

    private static final int        URL_SYSPARAM     = 1;
    private static final int        URL_SYSPARAM_ID  = 2;
    private static final int        URL_NETCONN      = 3;
    private static final int        URL_NETCONN_ID   = 4;
    private static final int        URL_SERVINFO     = 5;
    private static final int        URL_SERVINFO_ID  = 6;
    private static final int        URL_SIGOUT       = 7;
    private static final int        URL_SIGOUT_ID    = 8;
    private static final int        URL_WIFICFG      = 9;
    private static final int        URL_WIFICFG_ID   = 10;
    private static final int        URL_ONOFFTIME    = 11;
    private static final int        URL_ONOFFTIME_ID = 12;
    private static final int        URL_OFFDL        = 13;
    private static final int        URL_OFFDL_ID     = 14;
    private static final int        URL_AUTHINFO     = 15;
    private static final int        URL_AUTHINFO_ID  = 16;
    private static final int        URL_PGMPATH      = 17;
    private static final int        URL_PGMPATH_ID   = 18;
    private static final int        URL_MULTICAST    = 19;
    private static final int        URL_MULTICAST_ID = 20;

    private AdDbHelper              mDbHelper       = null;

    private static final UriMatcher s_urlMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "sysparam", URL_SYSPARAM);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "sysparam/#", URL_SYSPARAM_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "netconnect", URL_NETCONN);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "netconnect/#", URL_NETCONN_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "servinfo", URL_SERVINFO);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "servinfo/#", URL_SERVINFO_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "sigout", URL_SIGOUT);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "sigout/#", URL_SIGOUT_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "wificfg", URL_WIFICFG);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "wificfg/#", URL_WIFICFG_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "onofftime", URL_ONOFFTIME);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "onofftime/#", URL_ONOFFTIME_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "offdltime", URL_OFFDL);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "offdltime/#", URL_OFFDL_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "authinfo", URL_AUTHINFO);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "authinfo/#", URL_AUTHINFO_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "pgmpath", URL_PGMPATH);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "pgmpath/#", URL_PGMPATH_ID);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "multicast", URL_MULTICAST);
        s_urlMatcher.addURI(DbConstants.AUTHORITY, "multicast/#", URL_MULTICAST_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new AdDbHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        Uri deleteUri;

        switch (s_urlMatcher.match(uri)) {
            case URL_SYSPARAM:
                count = db.delete(DbConstants.TABLE_SYSPARAM, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_SYSPARAM;
                break;

            case URL_SYSPARAM_ID:
                count = db.delete(DbConstants.TABLE_SYSPARAM,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_SYSPARAM;
                break;

            case URL_NETCONN:
                count = db.delete(DbConstants.TABLE_NETCONN, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_NETCONN;
                break;

            case URL_NETCONN_ID:
                count = db.delete(DbConstants.TABLE_NETCONN,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_NETCONN;
                break;

            case URL_SERVINFO:
                count = db.delete(DbConstants.TABLE_SERVINFO, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_SERVINFO;
                break;

            case URL_SERVINFO_ID:
                count = db.delete(DbConstants.TABLE_SERVINFO,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_SERVINFO;
                break;

            case URL_SIGOUT:
                count = db.delete(DbConstants.TABLE_SIGOUT, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_SIGOUT;
                break;

            case URL_SIGOUT_ID:
                count = db.delete(DbConstants.TABLE_SIGOUT,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_SIGOUT;
                break;

            case URL_WIFICFG:
                count = db.delete(DbConstants.TABLE_WIFICFG, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_WIFICFG;
                break;

            case URL_WIFICFG_ID:
                count = db.delete(DbConstants.TABLE_WIFICFG,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_WIFICFG;
                break;

            case URL_ONOFFTIME:
                count = db.delete(DbConstants.TABLE_ONOFFTIME, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_ONOFFTIME;
                break;

            case URL_ONOFFTIME_ID:
                count = db.delete(DbConstants.TABLE_ONOFFTIME,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_ONOFFTIME;
                break;

            case URL_OFFDL:
                count = db.delete(DbConstants.TABLE_OFFDLTIME, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_OFFDLTIME;
                break;

            case URL_OFFDL_ID:
                count = db.delete(DbConstants.TABLE_OFFDLTIME,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_OFFDLTIME;
                break;

            case URL_AUTHINFO:
                count = db.delete(DbConstants.TABLE_AUTHINFO, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_AUTHINFO;
                break;

            case URL_AUTHINFO_ID:
                count = db.delete(DbConstants.TABLE_AUTHINFO,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_AUTHINFO;
                break;

            case URL_PGMPATH:
                count = db.delete(DbConstants.TABLE_PGM_PATH, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_PGMPATH;
                break;

            case URL_PGMPATH_ID:
                count = db.delete(DbConstants.TABLE_PGM_PATH,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_PGMPATH;
                break;

            case URL_MULTICAST:
                count = db.delete(DbConstants.TABLE_MULTICAST, where, whereArgs);
                deleteUri = DbConstants.CONTENTURI_MULTICAST;
                break;

            case URL_MULTICAST_ID:
                count = db.delete(DbConstants.TABLE_MULTICAST,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                deleteUri = DbConstants.CONTENTURI_MULTICAST;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(deleteUri, null);
        }

        return count;
    }

    @Override
    public String getType(Uri url) {
        switch (s_urlMatcher.match(url)) {
            case URL_SYSPARAM:
            case URL_NETCONN:
            case URL_SERVINFO:
            case URL_SIGOUT:
            case URL_WIFICFG:
            case URL_ONOFFTIME:
            case URL_OFFDL:
            case URL_AUTHINFO:
            case URL_PGMPATH:
            case URL_MULTICAST:
                return DbConstants.CONTENT_TYPE;

            case URL_SYSPARAM_ID:
            case URL_NETCONN_ID:
            case URL_SERVINFO_ID:
            case URL_SIGOUT_ID:
            case URL_WIFICFG_ID:
            case URL_ONOFFTIME_ID:
            case URL_OFFDL_ID:
            case URL_AUTHINFO_ID:
            case URL_PGMPATH_ID:
            case URL_MULTICAST_ID:
                return DbConstants.CONTENT_TYPE_ITME;

            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId;
        Uri insertUri = null;

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        switch (s_urlMatcher.match(uri)) {
            case URL_SYSPARAM:
                rowId = db.insert(DbConstants.TABLE_SYSPARAM, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_SYSPARAM, rowId);
                }
                break;

            case URL_NETCONN:
                rowId = db.insert(DbConstants.TABLE_NETCONN, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_NETCONN, rowId);
                }
                break;

            case URL_SERVINFO:
                rowId = db.insert(DbConstants.TABLE_SERVINFO, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_SERVINFO, rowId);
                }
                break;

            case URL_SIGOUT:
                rowId = db.insert(DbConstants.TABLE_SIGOUT, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_SIGOUT, rowId);
                }
                break;

            case URL_WIFICFG:
                rowId = db.insert(DbConstants.TABLE_WIFICFG, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_WIFICFG, rowId);
                }
                break;

            case URL_ONOFFTIME:
                rowId = db.insert(DbConstants.TABLE_ONOFFTIME, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_ONOFFTIME, rowId);
                }
                break;

            case URL_OFFDL:
                rowId = db.insert(DbConstants.TABLE_OFFDLTIME, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_OFFDLTIME, rowId);
                }
                break;

            case URL_AUTHINFO:
                rowId = db.insert(DbConstants.TABLE_AUTHINFO, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_AUTHINFO, rowId);
                }
                break;

            case URL_PGMPATH:
                rowId = db.insert(DbConstants.TABLE_PGM_PATH, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_PGMPATH, rowId);
                }
                break;

            case URL_MULTICAST:
                rowId = db.insert(DbConstants.TABLE_MULTICAST, null, values);
                if (rowId > 0) {
                    insertUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_MULTICAST, rowId);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (rowId > 0) {
            getContext().getContentResolver().notifyChange(insertUri, null);
            return insertUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (s_urlMatcher.match(uri)) {
            case URL_SYSPARAM:
                qb.setTables(DbConstants.TABLE_SYSPARAM);
                break;

            case URL_SYSPARAM_ID:
                qb.setTables(DbConstants.TABLE_SYSPARAM);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_NETCONN:
                qb.setTables(DbConstants.TABLE_NETCONN);
                break;

            case URL_NETCONN_ID:
                qb.setTables(DbConstants.TABLE_NETCONN);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_SERVINFO:
                qb.setTables(DbConstants.TABLE_SERVINFO);
                break;

            case URL_SERVINFO_ID:
                qb.setTables(DbConstants.TABLE_SERVINFO);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_SIGOUT:
                qb.setTables(DbConstants.TABLE_SIGOUT);
                break;

            case URL_SIGOUT_ID:
                qb.setTables(DbConstants.TABLE_SIGOUT);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_WIFICFG:
                qb.setTables(DbConstants.TABLE_WIFICFG);
                break;

            case URL_WIFICFG_ID:
                qb.setTables(DbConstants.TABLE_WIFICFG);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_ONOFFTIME:
                qb.setTables(DbConstants.TABLE_ONOFFTIME);
                break;

            case URL_ONOFFTIME_ID:
                qb.setTables(DbConstants.TABLE_ONOFFTIME);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_OFFDL:
                qb.setTables(DbConstants.TABLE_OFFDLTIME);
                break;

            case URL_OFFDL_ID:
                qb.setTables(DbConstants.TABLE_OFFDLTIME);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_AUTHINFO:
                qb.setTables(DbConstants.TABLE_AUTHINFO);
                break;

            case URL_AUTHINFO_ID:
                qb.setTables(DbConstants.TABLE_AUTHINFO);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_PGMPATH:
                qb.setTables(DbConstants.TABLE_PGM_PATH);
                break;

            case URL_PGMPATH_ID:
                qb.setTables(DbConstants.TABLE_PGM_PATH);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            case URL_MULTICAST:
                qb.setTables(DbConstants.TABLE_MULTICAST);
                break;

            case URL_MULTICAST_ID:
                qb.setTables(DbConstants.TABLE_MULTICAST);
                qb.appendWhere(DbConstants._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count;
        Uri updateUri;

        switch (s_urlMatcher.match(uri)) {
            case URL_SYSPARAM:
                count = db.update(DbConstants.TABLE_SYSPARAM, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_SYSPARAM;
                break;

            case URL_SYSPARAM_ID:
                count = db.update(DbConstants.TABLE_SYSPARAM, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_SYSPARAM,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_NETCONN:
                count = db.update(DbConstants.TABLE_NETCONN, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_NETCONN;
                break;

            case URL_NETCONN_ID:
                count = db.update(DbConstants.TABLE_NETCONN, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_NETCONN,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_SERVINFO:
                count = db.update(DbConstants.TABLE_SERVINFO, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_SERVINFO;
                break;

            case URL_SERVINFO_ID:
                count = db.update(DbConstants.TABLE_SERVINFO, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_SERVINFO,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_SIGOUT:
                count = db.update(DbConstants.TABLE_SIGOUT, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_SIGOUT;
                break;

            case URL_SIGOUT_ID:
                count = db.update(DbConstants.TABLE_SIGOUT, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_SIGOUT,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_WIFICFG:
                count = db.update(DbConstants.TABLE_WIFICFG, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_WIFICFG;
                break;

            case URL_WIFICFG_ID:
                count = db.update(DbConstants.TABLE_WIFICFG, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_WIFICFG,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_ONOFFTIME:
                count = db.update(DbConstants.TABLE_ONOFFTIME, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_ONOFFTIME;
                break;

            case URL_ONOFFTIME_ID:
                count = db.update(DbConstants.TABLE_ONOFFTIME, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_ONOFFTIME,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_OFFDL:
                count = db.update(DbConstants.TABLE_OFFDLTIME, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_OFFDLTIME;
                break;

            case URL_OFFDL_ID:
                count = db.update(DbConstants.TABLE_OFFDLTIME, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_OFFDLTIME,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_AUTHINFO:
                count = db.update(DbConstants.TABLE_AUTHINFO, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_AUTHINFO;
                break;

            case URL_AUTHINFO_ID:
                count = db.update(DbConstants.TABLE_AUTHINFO, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_AUTHINFO,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_PGMPATH:
                count = db.update(DbConstants.TABLE_PGM_PATH, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_PGMPATH;
                break;

            case URL_PGMPATH_ID:
                count = db.update(DbConstants.TABLE_PGM_PATH, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_PGMPATH,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            case URL_MULTICAST:
                count = db.update(DbConstants.TABLE_MULTICAST, values, where, whereArgs);
                updateUri = DbConstants.CONTENTURI_MULTICAST;
                break;

            case URL_MULTICAST_ID:
                count = db.update(DbConstants.TABLE_MULTICAST, values,
                        DbConstants._ID + "=" + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(where) ? " AND " + where : ""), whereArgs);
                updateUri = ContentUris.withAppendedId(DbConstants.CONTENTURI_MULTICAST,
                        Long.parseLong(uri.getPathSegments().get(1)));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(updateUri, null);
        }

        return count;
    }

}
