package com.racavalieri.quickgalleryorg.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.racavalieri.quickgalleryorg.Configurations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DAO {
    private static String dbPath;

    private static Context dbContext = null;

    private static Database database;

    public DAO(Context context) {
        dbContext = context;

        try {
            database = new Database(context);
        } catch( Exception e) {
            e.printStackTrace();
        }
    }

    public static void execute(String query){
        SQLiteDatabase db = database.getWritableDatabase();
        db.execSQL(query);
    }

    public static long insert(String table, ContentValues values){
        SQLiteDatabase db = database.getWritableDatabase();
        long rowsInserted = -1;
        try {
            rowsInserted = db.insert(table, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close(); // Closing database connection

        return rowsInserted;
    }


    /**
     * Executa uma Query de SELECT para retornar uma tupla no banco de dados
     *
     * @param tabela
     * @param campos
     * @param where
     * @param whereArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return Cursor
     */
    public static Cursor select(String tabela, String campos[], String where, String[] whereArgs, String groupBy, String having, String orderBy) {
        Cursor resultado = null;
        SQLiteDatabase db = database.getReadableDatabase();

        try {
            resultado = db.query(tabela, campos, where, whereArgs, groupBy, having, orderBy);
        } finally {
            db.close();
        }

        return resultado;
    }

    /**
     * Executa uma Query de SELECT para retornar uma tupla no banco de dados (Menos Complexo)
     *
     * @param oQue
     * @param nomeDaTabela
     * @return Cursor
     */
    public static Cursor select(String oQue, String nomeDaTabela){
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + oQue + " FROM " + nomeDaTabela + ";", null);
        return cursor;
    }

    /**
     * Executa uma Query de SELECT com clausula WHERE para retornar uma tupla no banco de dados (Menos Complexo)
     *
     * @param oQue
     * @param nomeDaTabela
     * @param where
     * @return Cursor
     */
    public static Cursor select(String oQue, String nomeDaTabela, String where){
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + oQue + " FROM " + nomeDaTabela + " WHERE " + where + ";", null);
        return cursor;
    }


    /**
     * Seleciona TODAS as tuplas de uma determinada tabela
     *
     * @param nomeDaTabela
     * @return ArrayList <HashMap>
     */
    public static ArrayList<HashMap<String, String>> selectAll(String nomeDaTabela) {
        String selectQuery = "SELECT  * FROM "+nomeDaTabela;

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> map = new HashMap<String, String>();
                for(int i=0;i<cursor.getColumnCount();i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                wordList.add(map);

                //Log.d("DEBUG", "SelectAll | Cursor Atual: "+cursor.getString(cursor.getColumnCount()-1));

                cursor.moveToNext();
            }
        }
        else if(cursor.getCount()>0){
            int name = cursor.getCount();
            Log.d("DEBUG", "Unic Result: " +name);
        }
        else {
            Log.d("DEBUG", "[1] No results in " + nomeDaTabela);
        }

        // return contact list
        return wordList;
    }


    /**
     * Seleciona TODAS as tuplas de uma determinada tabela com Clausula WHERE
     *
     * @param nomeDaTabela
     * @return ArrayList <HashMap>
     */
    public static ArrayList<HashMap<String, String>> selectAll(String nomeDaTabela, String where, String valor) {
        String selectQuery = "SELECT  * FROM "+nomeDaTabela+" WHERE "+where+" = "+valor;

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> map = new HashMap<String, String>();
                for(int i=0;i<cursor.getColumnCount();i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                wordList.add(map);

                //Log.d("DEBUG", "SelectAll | Cursor Atual: "+cursor.getString(cursor.getColumnCount()-1));

                cursor.moveToNext();
            }
        }
        else if(cursor.getCount()>0){
            int name = cursor.getCount();
            Log.d("DEBUG", "Unic Result: " +name);
        }
        else {
            Log.d("DEBUG", "[2] No results in " + nomeDaTabela);
        }

        // return contact list
        return wordList;
    }


    /**
     * Seleciona TODAS as tuplas de uma determinada tabela com Clausula WHERE
     *
     * @param nomeDaTabela
     * @return ArrayList <HashMap>
     */
    public static ArrayList<HashMap<String, String>> selectAllLike(String nomeDaTabela, String where, String valor) {
        String selectQuery = "SELECT  * FROM "+nomeDaTabela+" WHERE '"+where+"' LIKE '%"+valor+"%'";

        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> map = new HashMap<String, String>();
                for(int i=0;i<cursor.getColumnCount();i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                wordList.add(map);

                //Log.d("DEBUG", "SelectAll | Cursor Atual: "+cursor.getString(cursor.getColumnCount()-1));

                cursor.moveToNext();
            }
        }
        else if(cursor.getCount()>0){
            int name = cursor.getCount();
            Log.d("DEBUG", "Unic Result: " +name);
        }
        else {
            Log.d("DEBUG", "[0] No results in " + nomeDaTabela);
        }

        // return contact list
        return wordList;
    }


    /**
     * Verifica se existe o item na tabela
     *
     * @param nomeDaTabela
     * @param coluna
     * @param valor
     * @return
     */
    public static boolean existe(String coluna, String valor, String nomeDaTabela) {
        String selectQuery = "SELECT COUNT(*) FROM "+nomeDaTabela+" WHERE "+coluna+" = "+valor;

        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            if(cursor.getInt(0)>0) {
                Log.d("DEBUG/SQL","Exists: "+cursor.getString(0));
                return true;
            }

            else return false;
        }
        else return false;
    }


    /**
     * Verifica de sincronismo do item no DB
     * Se ele existe, ATUALIZA
     * Se ele não existe, INSERE
     *
     *
     * @param nomeDaTabela
     * @param valores
     * @return
     */
    public static boolean sincroniza(String nomeDaTabela, ContentValues valores) {
        boolean deletar_e_recriar = false; // Método de Sicronizar Dados (false quando fluxo de informações fo grande)
        if(deletar_e_recriar) limparTabela(nomeDaTabela);


        String itemID = valores.getAsString("ID");
        // Remoção dos Excluídos do Sistema
//        if(valores.getAsString("STATUS")=="0" && existe("ID", itemID, nomeDaTabela)) {
//            Log.d("DEBUG/DAO","Item ID "+itemID+" encontrado, Removendo...");
//            DAO.remover(nomeDaTabela, "ID = " + valores.getAsString("ID"), null);
//            return true;
//        }

        // Atualização dos Existentes
        if(existe("ID", itemID, nomeDaTabela)) {
            Log.d("DEBUG/DAO","Item ID "+itemID+" encontrado, Atualizando...");
            atualizar(nomeDaTabela, valores);
            return true;
        }
        else {
            Log.d("DEBUG/DAO","Item "+itemID+" não encontrado, Inserindo...");
            insert(nomeDaTabela, valores);
            return false;
        }

    }


    /**
     * Método de atualização de tupla do banco de dados
     * Retorna o numero de tuplas afetadas
     *
     * @param nomeDaTabela
     * @param valores
     * @return int
     */
    public static int atualizar(String nomeDaTabela, ContentValues valores) {
        SQLiteDatabase db = database.getWritableDatabase();
        int resultado = -1;

        try {
            resultado = db.update(nomeDaTabela,valores,"ID="+valores.getAsString("ID"), null);
        } finally {
            db.close();
        }
        return resultado;
    }


    /**
     * Método de atualização de tupla do banco de dados
     * Retorna o numero de tuplas afetadas
     *
     * @param nomeDaTabela
     * @param valores
     * @return int
     */
    public static int atualizarWhere(String nomeDaTabela, ContentValues valores, String where) {
        SQLiteDatabase db = database.getWritableDatabase();
        int resultado = -1;

        try {
            resultado = db.update(nomeDaTabela,valores,where, null);
        } finally {
            db.close();
        }
        return resultado;
    }


    /**
     * Método de remoção de tupla do banco de dados
     * Retorna o numero de tuplas afetadas
     *
     * @param nomeDaTabela
     * @param where
     * @param whereArgs
     * @return int
     */
    public static int remover(String nomeDaTabela, String where, String[] whereArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int resultado = -1;

        try {
            resultado = db.delete(nomeDaTabela, where, whereArgs);
        } finally {
            db.close();
        }
        return resultado;
    }


    /**
     * Retorna o nome de todas as tabelas
     *
     * (Not working)
     * @return Cursor
     * Thanks to 'erdomester' on http://stackoverflow.com/questions/9647129/android-sqlite-show-tables
     */
    public static Cursor showAllTables(){
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT name FROM "+Configurations.DATABASE_NAME+" WHERE type='table' AND name LIKE 'PR_%'", null);
    }


    /**
     * Show all with array return
     *
     * (Not working)
     */
    public static List<String> showAllTablesArray(){
        Cursor c = null;
        List<String> tables = new ArrayList<String>();
        SQLiteDatabase db = database.getReadableDatabase();

        try {
            c = db.rawQuery("SELECT name FROM "+database.getDatabaseName()+" WHERE type='table'", null);
            Log.d("DEBUG", ""+c.toString());

            if (c.moveToFirst())
            {
                do{
                    tables.add(c.getString(0));

                }while (c.moveToNext());
            }
        } catch( SQLiteException e) {
            Log.d("DEBUG","Erro ao selecionar tabelas: "+e.toString());
        }

        return tables;
    }

    /**
     * Verifica o estado do banco de dados (Se ele existe ou não)
     * (Atualmente obsoleto)
     *
     * @return boolean
     */
    public static boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = dbPath;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (Exception e) {
            Log.d("DEBUG", "Database não existente.." +e.toString());
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }



    /**
     * Verifica se x Tabela existe no banco de dados
     * http://stackoverflow.com/questions/3058909/how-does-one-check-if-a-table-exists-in-an-android-sqlite-database
     * @param tableName
     * @return boolean
     */
    public static boolean tableExists(String tableName) {
        SQLiteDatabase db = database.getReadableDatabase();
        Log.d("DEBUG","Checking: "+db.getPath());

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
//        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from "+Configuracoes._DATABASE_NAME+" where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * Verifica se existem itens na tabela x
     * http://stackoverflow.com/questions/22630307/sqlite-check-if-table-is-empty
     * @param tableName
     */
    public static boolean isTableEmpty(String tableName) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM "+tableName, null);

        if(cursor!=null)
            cursor.moveToFirst();

        if (cursor.getInt(0) > 0)
            return false;
        else
            return true;
    }

    /**
     * Limpa todos os dados da tabela x
     * @param nomeDaTabela
     */
    public static int limparTabela(String nomeDaTabela) {
        SQLiteDatabase db = database.getWritableDatabase();
        return db.delete(nomeDaTabela, "1", null);
    }


    /**
     * DROP do DATABASE ATUAL
     *
     * @return
     */
    public static boolean resetActualDatatase(){
        try {
            Log.d("SQL", "_DATABASE_RESET habilitado!");
            dbContext.deleteDatabase(Configurations.DATABASE_NAME); // RESET DATABASE
            Log.d("SQL", "Database resetado..");
            return true;

        } catch (Exception e) {
            Log.d("SQL", "Falha ao remover database: "+e);
            e.printStackTrace();
            return false;
        }
    }


    /**
     * DROP de TODOS os bancos de dados referentes à aplicação
     *
     * @return
     */
    public static boolean resetAllDatatases(){
        try {
            Log.d("SQL", "_FULL_DATABASE_RESET habilitado!");
            for (String database : dbContext.databaseList()) {
                dbContext.deleteDatabase(database); // RESET DATABASE
                Log.d("SQL", "Removido database: "+database);
            }
            return true;
        } catch (Exception e) {
            Log.d("SQL", "Falha ao remover database: "+e);
            e.printStackTrace();
            return false;
        }
    }
}