package br.com.sharing.controleacesso.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.sharing.R;
import br.com.sharing.controleacesso.dominio.Objeto;
import br.com.sharing.controleacesso.dominio.Pessoa;
import br.com.sharing.controleacesso.negocio.ObjetoNegocio;
import br.com.sharing.controleacesso.negocio.SessaoUsuario;
import br.com.sharing.controleacesso.negocio.UsuarioNegocio;
import br.com.sharing.controleacesso.persistencia.ObjetoDao;
import br.com.sharing.infra.gui.GuiUtil;
import br.com.sharing.infra.gui.SharingException;

public class MeuObjetoActivity extends AppCompatActivity {
    private SessaoUsuario sessaoUsuario;
    private Pessoa pessoaLogada;
    private ObjetoNegocio objetoNegocio;
    private UsuarioNegocio usuarioNegocio;

    private ImageView imageView;
    private TextView textViewNome;
    private TextView textViewDescricao;
    private TextView textViewCategoria;
    private TextView textViewEstado;
    private TextView textViewAlugador;

    private int idObjeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meu_objeto);

        Bundle bundle = getIntent().getExtras();
        idObjeto = bundle.getInt("selected-item");

        imageView = (ImageView) findViewById(R.id.meu_objeto_imagem_principal);
        textViewNome = (TextView) findViewById(R.id.nome_meu_objeto_show);
        textViewDescricao = (TextView) findViewById(R.id.descricao_meu_objeto_show);
        textViewCategoria = (TextView) findViewById(R.id.categoria_meu_objeto_show);
        textViewEstado = (TextView) findViewById(R.id.estado_meu_objeto_show);
        textViewAlugador = (TextView) findViewById(R.id.alugador_meu_objeto_show);
        Button btnRetornarObjeto = (Button)findViewById(R.id.pedir_devolucao);

        sessaoUsuario = SessaoUsuario.getInstancia();
        usuarioNegocio = UsuarioNegocio.getInstancia(this);
        pessoaLogada = sessaoUsuario.getPessoaLogada();
        objetoNegocio = ObjetoNegocio.getInstancia(this);

        Objeto objeto = getObjeto(idObjeto);
        String informacoesAlugador;
        if (objeto.getIdAlugador()!=0){
            Pessoa objetoAlugador = getPessoa(objeto.getIdAlugador());
            informacoesAlugador = objetoAlugador.getNome()+"\n"+objetoAlugador.getEmail();
        }else {
            informacoesAlugador = "  - - -";
        }

        textViewNome.setText(objeto.getNome());
        textViewDescricao.setText(objeto.getDescricao());
        textViewCategoria.setText(objeto.getCategoriaEnum().toString());
        textViewEstado.setText(objeto.getEstadoEnum().toString());
        textViewAlugador.setText(informacoesAlugador);
        imageView.setImageURI(objeto.getFoto());

        btnRetornarObjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retornarObjeto();
            }
        });
    }


    public Objeto getObjeto(int idObjeto) {
        Objeto objeto = new Objeto();

        try {
            objeto = objetoNegocio.pesquisarPorId(idObjeto);
        } catch (SharingException e) {
            Log.d("MeuObjetoActivity", e.getMessage());
        }

        return objeto;
    }

    public Pessoa getPessoa(int idDono){
        Pessoa pessoa = new Pessoa();
        try{
            pessoa = usuarioNegocio.pesquisarPorId(idDono);
        }catch (SharingException e){
            Log.d("MeuObjetoActivity",e.getMessage());
        }
        return pessoa;
    }

    public void onButtonClickMeuObjeto(View v){
     /*
        if (v.getId() == R.id.meu_objeto_imagem_principal){
            Intent intent= new Intent(this, ListaImagensObjetoActivity.class);
            intent.putExtra("imagem", (getObjeto(idObjeto).getId()));
            startActivity(intent);
        }*/
    }

    public void retornarObjeto(){
    String variavel;
        variavel = String.valueOf(idObjeto);
        Objeto objeto = getObjeto(idObjeto);
        ObjetoDao objetoDao = new ObjetoDao();
        if (objeto.getIdAlugador()!=0) {
            try {
                GuiUtil.exibirMsg(this,String.valueOf(objeto.getIdAlugador()));
                objetoDao.devolverObjeto(getObjeto(idObjeto).getId());
                pessoaLogada.addPontuacao(10);
                getPessoa(objeto.getIdAlugador()).addPontuacao(10);
                GuiUtil.exibirMsg(this, "Objeto devolvido");
            } catch (SharingException e) {
                GuiUtil.exibirMsg(this, e.getMessage());
            }
        }else {
            GuiUtil.exibirMsg(this,"Objeto nao está alugado");
        }
    }


}
