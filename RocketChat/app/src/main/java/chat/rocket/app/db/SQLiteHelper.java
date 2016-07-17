/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package chat.rocket.app.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import chat.rocket.app.db.dao.CollectionDAO;
import chat.rocket.app.db.dao.EditedByDAO;
import chat.rocket.app.db.dao.MessageDAO;
import chat.rocket.app.db.dao.RcSubscriptionDAO;
import chat.rocket.app.db.dao.UrlPartsDAO;
import chat.rocket.app.db.dao.UsernameIdDAO;
import chat.rocket.app.db.util.TableBuilder;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteHelper";
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 3;
    private static SQLiteHelper mInstance;

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SQLiteHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CollectionDAO.createTableString());
            db.execSQL(TableBuilder.createIndexString(CollectionDAO.TABLE_NAME, CollectionDAO.COLUMN_COLLECTION_NAME));
            db.execSQL(TableBuilder.createIndexString(CollectionDAO.TABLE_NAME, CollectionDAO.COLUMN_DOCUMENT_ID));


            db.execSQL(EditedByDAO.createTableString());
            db.execSQL(MessageDAO.createTableString());
            db.execSQL(UrlPartsDAO.createTableString());
            db.execSQL(UsernameIdDAO.createTableString());
            db.execSQL(RcSubscriptionDAO.createTableString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CollectionDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EditedByDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UrlPartsDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsernameIdDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RcSubscriptionDAO.TABLE_NAME);
        onCreate(db);
    }
}