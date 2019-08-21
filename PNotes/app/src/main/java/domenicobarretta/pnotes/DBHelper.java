package domenicobarretta.pnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";

    private static final String IMAGE_TABLE_NAME = "images";
    private static final String IMAGE_COLUMN_TITLE = "title";
    private static final String IMAGE_COLUMN_PHOTOPATH ="photopath";
    private static final String IMAGE_COLUMN_AUDIOPATH ="audiopath";

    private static final String WEB_TABLE_NAME = "web";
    private static final String WEB_COLUMN_TITLE = "title";
    private static final String WEB_COLUMN_WEBPATH = "webpath";
    private static final String WEB_COLUMN_LINK = "webLink";
    private static final String WEB_COLUMN_AUDIOPATH = "webaudiopath";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    /*Creo un database con due tabelle. La tabella immagini contiene le informazioni delle foto:
    percorso della foto, della sua nota vocale (se c'è) e del titolo. Stessa cosa avviene per la
    tabella web, solo che oltre alle informazioni precedenti viene memorizzato anche il link della
    pagina
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+IMAGE_TABLE_NAME+
                "("+IMAGE_COLUMN_TITLE+","+IMAGE_COLUMN_PHOTOPATH+","+IMAGE_COLUMN_AUDIOPATH+")"
        );
        db.execSQL("create table "+WEB_TABLE_NAME+
                "("+WEB_COLUMN_TITLE+","+WEB_COLUMN_WEBPATH+","+WEB_COLUMN_LINK+","+
                    WEB_COLUMN_AUDIOPATH+")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+IMAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+WEB_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertImage(String title, String photoPath, String audioPath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_COLUMN_TITLE,title);
        contentValues.put(IMAGE_COLUMN_PHOTOPATH,photoPath);
        contentValues.put(IMAGE_COLUMN_AUDIOPATH,audioPath);
        db.insert(IMAGE_TABLE_NAME,null,contentValues);
        return true;
    }

    public void deleteImageAt (int position){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM images ";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToPosition(position);
        String imageName = cursor.getString(cursor.getColumnIndex(IMAGE_COLUMN_PHOTOPATH));
        db.delete(IMAGE_TABLE_NAME, IMAGE_COLUMN_PHOTOPATH + "=?",  new String[]{imageName});
    }

    /*modifica l'immagine che ha come percorso photoPath*/
    public void modifyItemAt(String title, String photoPath, String audioPath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_COLUMN_TITLE, title);
        contentValues.put(IMAGE_COLUMN_AUDIOPATH, audioPath);
        db.update(IMAGE_TABLE_NAME, contentValues, IMAGE_COLUMN_PHOTOPATH+"=?", new String[] { photoPath } );
    }

    public boolean insertWeb(String title, String link, String prevPath, String audioPath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEB_COLUMN_TITLE,title);
        contentValues.put(WEB_COLUMN_WEBPATH,prevPath);
        contentValues.put(WEB_COLUMN_LINK,link);
        contentValues.put(WEB_COLUMN_AUDIOPATH,audioPath);
        db.insert(WEB_TABLE_NAME,null,contentValues);
        return true;
    }

    public void deleteWebAt (int position){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM web ";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToPosition(position);
        String imageName = cursor.getString(cursor.getColumnIndex(WEB_COLUMN_LINK));
        db.delete(WEB_TABLE_NAME, WEB_COLUMN_LINK + "=?",  new String[]{imageName});
    }

    public void modifyWebAt(String title, String link, String prevPath, String audioPath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEB_COLUMN_TITLE, title);
        contentValues.put(WEB_COLUMN_AUDIOPATH, audioPath);
        contentValues.put(WEB_COLUMN_WEBPATH,prevPath);
        db.update(WEB_TABLE_NAME, contentValues, WEB_COLUMN_LINK+"=?", new String[] { link } );
    }

    /*Mostra la tabella selezionata in modo da poterne controllare il contenuto*/
    public String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        return tableString;
    }
}
