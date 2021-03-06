package br.com.sharing.controleacesso.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.com.sharing.controleacesso.dominio.Categoria;
import br.com.sharing.controleacesso.dominio.Estado;
import br.com.sharing.controleacesso.dominio.Objeto;
import br.com.sharing.controleacesso.negocio.SessaoUsuario;
import br.com.sharing.infra.gui.SharingException;

/**
 * Classe do banco de eventos
 */
public class ObjetoDao {

    private static DatabaseHelper databaseHelper;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    private static ObjetoDao instanciaObjetoDao = new ObjetoDao();

    public ObjetoDao() {}

    /* singleton */
    public static ObjetoDao getInstancia(Context contexto) {
        ObjetoDao.databaseHelper = new DatabaseHelper(contexto);
        return instanciaObjetoDao;
    }


    /**
     * metodo utilizado para fazer o cadastro do objeto no banco
     *
     * @param objeto                    objeto a ser cadastrado no db
     * @throws SharingException         caso o objeto nao consiga ser cadastrado
     */
    public void cadastrarObjeto(Objeto objeto) throws SharingException {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.OBJETO_ID, objeto.getId());

        values = new ContentValues();
        values.put(DatabaseHelper.OBJETO_FOTO, objeto.getFoto().toString());
        values.put(DatabaseHelper.OBJETO_NOME, objeto.getNome());
        values.put(DatabaseHelper.OBJETO_DESCRICAO, objeto.getDescricao());
        values.put(DatabaseHelper.OBJETO_CATEGORIA, objeto.getCategoriaEnum().ordinal());
        values.put(DatabaseHelper.OBJETO_ESTADO, objeto.getEstadoEnum().ordinal());

        int idPessoa = SessaoUsuario.getInstancia().getPessoaLogada().getId();
        values.put(DatabaseHelper.OBJETO_DONO_ID, idPessoa);

        /*
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DATE_FORMATTER;

        try{
            values.put(DatabaseHelper.EVENTO_DATA_INICIO,simpleDateFormat.format(objeto.getDataInicio()));
            values.put(DatabaseHelper.EVENTO_DATA_FIM,simpleDateFormat.format(objeto.getDataFim()));
        } catch (Exception e) {
            throw new SharingException(e.getMessage());
        }
*/
        db.insert(DatabaseHelper.TABELA_OBJETO, null, values);
        db.close();
    }

    /**
     * metodo utilizado para fazer a criacao do evento no banco
     *
     * @param cursor                        cursor a ser usado na criacao do evento
     * @return                              objeto evento preenchido
     * @throws SharingException             caso o evento nao possa ser criado
     */
    public Objeto criarObjeto(Cursor cursor) throws SharingException {
        Objeto objeto = new Objeto();
        objeto.setId(cursor.getInt(0));
        objeto.setNome(cursor.getString(1));
        objeto.setDescricao(cursor.getString(4));
        objeto.setFoto(Uri.parse(cursor.getString(5)));
        objeto.setIdDono(cursor.getInt(6));
        objeto.setIdAlugador(cursor.getInt(7));

        Categoria categoria = Categoria.values()[cursor.getInt(2)];
        objeto.setCategoriaEnum(categoria);

        Estado estado = Estado.values()[cursor.getInt(3)];
        objeto.setEstadoEnum(estado);

        return objeto;
    }

    /**
     * metodo utilizado para fazer a busca dos evento no banco atraves do nome
     *
     * @param nome                      nome do evento que sera encontrado
     * @return                          evento com o nome desejado encontrado
     * @throws SharingException         caso o evento nao possa ser encontrado
     */
    public Objeto buscarEventoNome(String nome) throws SharingException {
        SQLiteDatabase db;
        db = databaseHelper.getReadableDatabase();

        Objeto objeto = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO +
                " WHERE " + databaseHelper.OBJETO_NOME + " =?", new String[]{nome});

        if (cursor.moveToFirst()){
            objeto = criarObjeto(cursor);
        }
        db.close();
        cursor.close();
        return objeto;
    }

    public Objeto buscarObjetoId(long id) throws SharingException {
        SQLiteDatabase db;
        db = databaseHelper.getReadableDatabase();

        Objeto objeto = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO +
            " WHERE " + databaseHelper.OBJETO_ID + " =?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()){
            objeto = criarObjeto(cursor);
        }
        db.close();
        cursor.close();
        return objeto;
    }


    /**
     * metodo utilizado para fazer a busca dos evento no banco atraves de pedacos do nome dos eventos
     *
     * @param id                        id dos eventos que serao procurados
     * @param nome                      nome dos eventos que sao procurados
     * @return                          lista de eventos contendo partes do nome procurado
     * @throws SharingException
     */
    public ArrayList<Objeto> buscarNomeDescricaoParcialPessoa(int id, String nome) throws SharingException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<Objeto> listaObjetos = new ArrayList<Objeto>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_DONO_ID + " = ? AND ("
                + databaseHelper.OBJETO_NOME + " LIKE ? OR " + databaseHelper.OBJETO_DESCRICAO + " LIKE ?)", new String[]{String.valueOf(id), "%" + nome + "%", "%" + nome + "%"});

        Objeto objeto = null;
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                objeto = criarObjeto(cursor);
                listaObjetos.add(objeto);
            }
        }
        cursor.close();
        return listaObjetos;
    }

    public ArrayList<Objeto> buscarNomeDescricaoParcialCategoria(int id, String nome, int categoria) throws SharingException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        ArrayList<Objeto> listaObjetos = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_CATEGORIA +
                " =? AND " + databaseHelper.OBJETO_DONO_ID + " != ? AND " + databaseHelper.OBJETO_ESTADO + " = ? AND (" +
                databaseHelper.OBJETO_NOME + " LIKE ? OR " + databaseHelper.OBJETO_DESCRICAO + " LIKE ?)", new String[]{String.valueOf(categoria),String.valueOf(id),"0", "%" + nome + "%", "%" + nome + "%"});
        Objeto objeto = null;
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                objeto = criarObjeto(cursor);
                listaObjetos.add(objeto);
            }
        }
        cursor.close();
        return listaObjetos;
    }

    public ArrayList<Objeto> buscarNomeDescricaoParcial(int id, String nome) throws SharingException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<Objeto> listaObjetos = new ArrayList<Objeto>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO + " WHERE ("
                + databaseHelper.OBJETO_NOME + " LIKE ? OR " + databaseHelper.OBJETO_DESCRICAO + " LIKE ?) AND " +
                databaseHelper.OBJETO_DONO_ID + " != ?", new String[]{ "%" + nome + "%", "%" + nome + "%", String.valueOf(id)});

        Objeto objeto = null;
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                objeto = criarObjeto(cursor);
                listaObjetos.add(objeto);
            }
        }
        cursor.close();
        return listaObjetos;
    }


    public Objeto buscarObjetoNomeEDono (int id, String nome) throws SharingException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<Objeto> listaObjetos = new ArrayList<Objeto>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_NOME +
                " =? AND " + databaseHelper.OBJETO_DONO_ID + " =?", new String[]{nome, String.valueOf(id)});

        Objeto objeto = null;
        if (cursor.moveToFirst()){
            objeto =criarObjeto(cursor);
        }
        db.close();
        cursor.close();
        return objeto;
    }


    public ArrayList<Objeto> listarObjetos(int id) throws SharingException {
        Objeto objeto = null;
        ArrayList<Objeto> listaObjetos = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        //seleciona objetos disponiveis
        Cursor cursor = db.rawQuery("SELECT * FROM "+ databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_ESTADO +
        " = 0 AND " + databaseHelper.OBJETO_DONO_ID + " != ?", new String[]{String.valueOf(id)});

        //seleciona todos os objetos
        //Cursor cursor = db.rawQuery("SELECT * FROM "+ databaseHelper.TABELA_OBJETO ,null);



        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                objeto = criarObjeto(cursor);
                listaObjetos.add(objeto);
            }
        }

        db.close();
        cursor.close();
        return listaObjetos;
    }

    public ArrayList<Objeto> listarObjetosCategorias(int id,int categoria) throws SharingException {
        Objeto objeto = null;
        ArrayList<Objeto> listaObjetos = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        //seleciona objetos ferramentas
        Cursor cursor = db.rawQuery("SELECT * FROM "+ databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_CATEGORIA +
                " =? AND " + databaseHelper.OBJETO_DONO_ID + " != ?", new String[]{String.valueOf(categoria),String.valueOf(id)});


        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                objeto = criarObjeto(cursor);
                listaObjetos.add(objeto);
            }
        }

        db.close();
        cursor.close();
        return listaObjetos;
    }

    public ArrayList<Objeto> listarObjetosPessoa(int idDono) throws SharingException {
        Objeto objeto = null;
        ArrayList<Objeto> listaObjetos = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_DONO_ID + " = ?"
                , new String[]{String.valueOf(idDono)});


        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                objeto = criarObjeto(cursor);
                listaObjetos.add(objeto);
            }
        }

        db.close();
        cursor.close();
        return listaObjetos;
    }

    public void alugarObjeto(int idObjeto, int idUsuario) throws SharingException {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("UPDATE " + databaseHelper.TABELA_OBJETO + " SET " + databaseHelper.OBJETO_ESTADO +
            " = 1 AND " + databaseHelper.OBJETO_ALUGADOR_ID + " =? WHERE " + databaseHelper.OBJETO_ID + " =?",new String[]{String.valueOf(idUsuario),String.valueOf(idObjeto)});

        db.close();
        cursor.close();
    }

    public void devolverObjeto(int idObjeto) throws SharingException {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("UPDATE " + databaseHelper.TABELA_OBJETO + " SET " + databaseHelper.OBJETO_ESTADO +
        " = 0 AND " + databaseHelper.OBJETO_ALUGADOR_ID + " = NULL WHERE "+ databaseHelper.OBJETO_ID + " =?",new String[]{String.valueOf(idObjeto)});

        db.close();;
        cursor.close();
    }

    /*
    public ArrayList<Objeto> listarEventoProximo(int id) throws SharingException {
        Objeto objeto = null;
        ArrayList<Objeto> eventosCriador = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO + " WHERE " +
                databaseHelper.OBJETO_DONO_ID + " =? ORDER BY "+ databaseHelper.EVENTO_DATA_INICIO +" ASC" , new String[]{String.valueOf(id)});

        while (cursor.moveToNext()){
            objeto = criarObjeto(cursor);
            eventosCriador.add(objeto);
        }

        db.close();
        cursor.close();
        return eventosCriador;
    }




    public ArrayList<Objeto> listarEventoData(String data, int id)throws SharingException{
        Objeto objeto = null;
        ArrayList eventosDia = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABELA_OBJETO + " WHERE " + databaseHelper.OBJETO_DONO_ID + " =? AND "
                + databaseHelper.EVENTO_DATA_INICIO +" LIKE ? ORDER BY "+databaseHelper.EVENTO_DATA_INICIO + " ASC" , new String[]{String.valueOf(id),"%"+data+"%"});

        while (cursor.moveToNext()){
            objeto = criarObjeto(cursor);
            eventosDia.add(objeto);
        }
        db.close();
        cursor.close();
        return eventosDia;
    }
*/
}