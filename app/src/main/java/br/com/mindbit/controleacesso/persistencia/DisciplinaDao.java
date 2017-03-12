/*package br.com.mindbit.controleacesso.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.mindbit.controleacesso.dominio.Disciplina;
import br.com.mindbit.controleacesso.negocio.SessaoUsuario;


public class DisciplinaDao {

    private static DatabaseHelper databaseHelper;
    private SessaoUsuario sessaoUsuario = SessaoUsuario.getInstancia();
    private static Context context;

    private static DisciplinaDao instanciaDisciplinaDao = new DisciplinaDao();

    private DisciplinaDao() {}

    // singleton /
    public static DisciplinaDao getInstancia(Context context) {
        DisciplinaDao.databaseHelper = new DatabaseHelper(context);
        return instanciaDisciplinaDao;
    }


    public void cadastrarDisciplina(Disciplina disciplina) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.DISCIPLINA_NOME, disciplina.getNome());
        values.put(DatabaseHelper.DISCIPLINA_CODIGO, disciplina.getCodigo());

        int idPessoa = SessaoUsuario.getInstancia().getPessoaLogada().getId();
        values.put(DatabaseHelper.PESSOA_CRIADORA_DISCIPLINA_ID, idPessoa);

        db.insert(DatabaseHelper.TABELA_DISCIPLINA, null, values);
        db.close();
    }



    private Disciplina criarDisciplina(Cursor cursor) {
        Disciplina disciplina = new Disciplina();
        disciplina.setId(cursor.getInt(0));
        disciplina.setNome(cursor.getString(1));
        disciplina.setCodigo(cursor.getString(2));

        return disciplina;
    }


    public Disciplina buscarDisciplinaNome(String nome) {
        SQLiteDatabase db;
        db = databaseHelper.getReadableDatabase();

        Disciplina disciplina = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABELA_DISCIPLINA +
                " WHERE " + DatabaseHelper.DISCIPLINA_NOME + " =?", new String[]{nome});
        if (cursor.moveToNext()) {
            disciplina = criarDisciplina(cursor);
        }
        db.close();
        cursor.close();
        return disciplina;
    }




}
*/