package com.example.consertaaqui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "conserta_aqui.db";
    private static final int DATABASE_VERSION = 1;

    // Tabelas e colunas
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COL_ID = "id";
    public static final String COL_NOME = "nome";
    public static final String COL_EMAIL = "email";
    public static final String COL_SENHA = "senha";
    public static final String COL_TIPO = "tipo_usuario";
    public static final String COL_TIPO_SERVICO = "tipo_servico";

    public static final String TABLE_SERVICOS = "servicos";
    public static final String COL_SERVICO_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_DESCRICAO = "descricao";
    public static final String COL_EMAIL_PRESTADOR = "email_prestador";

    public static final String TABLE_SOLICITACOES = "solicitacoes";
    public static final String COL_SOLICITACAO_ID = "id";
    public static final String COL_ID_SERVICO = "id_servico";
    public static final String COL_EMAIL_CLIENTE = "email_cliente";
    public static final String COL_OBSERVACAO = "observacao";

    public static final String TABLE_AVALIACOES = "avaliacoes";
    public static final String COL_AVALIACAO_ID = "id";
    public static final String COL_ID_SERVICO_AVALIADO = "id_servico";
    public static final String COL_EMAIL_CLIENTE_AVALIACAO = "email_cliente";
    public static final String COL_NOTA = "nota";
    public static final String COL_COMENTARIO = "comentario";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOME + " TEXT, "
                + COL_EMAIL + " TEXT UNIQUE, "
                + COL_SENHA + " TEXT, "
                + COL_TIPO + " TEXT, "
                + COL_TIPO_SERVICO + " TEXT)";

        String CREATE_SERVICOS = "CREATE TABLE " + TABLE_SERVICOS + " ("
                + COL_SERVICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITULO + " TEXT, "
                + COL_DESCRICAO + " TEXT, "
                + COL_EMAIL_PRESTADOR + " TEXT)";

        String CREATE_SOLICITACOES = "CREATE TABLE " + TABLE_SOLICITACOES + " ("
                + COL_SOLICITACAO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ID_SERVICO + " INTEGER, "
                + COL_EMAIL_CLIENTE + " TEXT, "
                + COL_OBSERVACAO + " TEXT)";

        String CREATE_AVALIACOES = "CREATE TABLE " + TABLE_AVALIACOES + " ("
                + COL_AVALIACAO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ID_SERVICO_AVALIADO + " INTEGER, "
                + COL_EMAIL_CLIENTE_AVALIACAO + " TEXT, "
                + COL_NOTA + " INTEGER, "
                + COL_COMENTARIO + " TEXT)";

        db.execSQL(CREATE_USUARIOS);
        db.execSQL(CREATE_SERVICOS);
        db.execSQL(CREATE_SOLICITACOES);
        db.execSQL(CREATE_AVALIACOES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLICITACOES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AVALIACOES);
        onCreate(db);
    }

    public boolean cadastrarUsuario(String nome, String email, String senha, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOME, nome);
        values.put(COL_EMAIL, email);
        values.put(COL_SENHA, senha);
        values.put(COL_TIPO, tipo);
        values.put(COL_TIPO_SERVICO, "");

        long result = db.insert(TABLE_USUARIOS, null, values);
        return result != -1;
    }

    public boolean verificarLogin(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, null,
                COL_EMAIL + "=? AND " + COL_SENHA + "=?",
                new String[]{email, senha}, null, null, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    public String buscarTipoUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS, new String[]{COL_TIPO},
                COL_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            String tipo = cursor.getString(0);
            cursor.close();
            return tipo;
        }
        cursor.close();
        return null;
    }

    public String[] buscarDadosUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COL_NOME, COL_EMAIL},
                COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            String[] dados = {cursor.getString(0), cursor.getString(1)};
            cursor.close();
            return dados;
        }
        cursor.close();
        return null;
    }

    public String buscarTipoServico(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT tipo_servico FROM usuarios WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        cursor.close();
        return "";
    }

    public boolean atualizarPerfil(String emailOriginal, String nomeNovo, String emailNovo, String tipoServico) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOME, nomeNovo);
        values.put(COL_EMAIL, emailNovo);
        values.put(COL_TIPO_SERVICO, tipoServico);
        int rows = db.update(TABLE_USUARIOS, values, COL_EMAIL + "=?", new String[]{emailOriginal});
        return rows > 0;
    }

    public boolean cadastrarServico(String titulo, String descricao, String emailPrestador) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, titulo);
        values.put(COL_DESCRICAO, descricao);
        values.put(COL_EMAIL_PRESTADOR, emailPrestador);

        long result = db.insert(TABLE_SERVICOS, null, values);
        return result != -1;
    }

    public Cursor listarServicosPorPrestador(String emailPrestador) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SERVICOS, null, COL_EMAIL_PRESTADOR + "=?",
                new String[]{emailPrestador}, null, null, null);
    }

    public boolean excluirServico(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_SERVICOS, COL_SERVICO_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean editarServico(int id, String novoTitulo, String novaDescricao) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITULO, novoTitulo);
        values.put(COL_DESCRICAO, novaDescricao);
        int result = db.update(TABLE_SERVICOS, values, COL_SERVICO_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // 🔥 NOVO MÉTODO: cadastrar avaliação
    public boolean cadastrarAvaliacao(int idServico, String emailCliente, int nota, String comentario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID_SERVICO_AVALIADO, idServico);
        values.put(COL_EMAIL_CLIENTE_AVALIACAO, emailCliente);
        values.put(COL_NOTA, nota);
        values.put(COL_COMENTARIO, comentario);
        long result = db.insert(TABLE_AVALIACOES, null, values);
        return result != -1;
    }

    // 🔥 NOVO MÉTODO: calcular média de notas
    public float calcularMediaAvaliacao(int idServico) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(nota) FROM " + TABLE_AVALIACOES + " WHERE " + COL_ID_SERVICO_AVALIADO + " = ?", new String[]{String.valueOf(idServico)});
        float media = 0;
        if (cursor.moveToFirst()) {
            media = cursor.getFloat(0);
        }
        cursor.close();
        return media;
    }
}
