package br.com.mindbit.controleacesso.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.mindbit.R;
import br.com.mindbit.controleacesso.dominio.Objeto;
import br.com.mindbit.controleacesso.dominio.Pessoa;
import br.com.mindbit.controleacesso.negocio.ObjetoNegocio;
import br.com.mindbit.controleacesso.negocio.SessaoUsuario;
import br.com.mindbit.controleacesso.negocio.UsuarioNegocio;
import br.com.mindbit.infra.gui.MindbitException;

public class ObjetoActivity extends AppCompatActivity{
    private SessaoUsuario sessaoUsuario;
    private Pessoa pessoaLogada;
    private ObjetoNegocio objetoNegocio;
    private UsuarioNegocio usuarioNegocio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto);

        Bundle bundle = getIntent().getExtras();
        int idObjeto = bundle.getInt("selected");


        ImageView imageView = (ImageView) findViewById(R.id.objeto_imagem_principal);
        TextView textViewNome = (TextView) findViewById(R.id.nome_objeto_show);
        TextView textViewDescricao = (TextView) findViewById(R.id.descricao_objeto_show);
        TextView textViewPlataforma = (TextView) findViewById(R.id.categoria_objeto_show);
        TextView textViewAplicacao = (TextView) findViewById(R.id.dono_objeto_show);

        sessaoUsuario = SessaoUsuario.getInstancia();
        usuarioNegocio = UsuarioNegocio.getInstancia(this);
        pessoaLogada = sessaoUsuario.getPessoaLogada();
        objetoNegocio = ObjetoNegocio.getInstancia(this);

        Objeto objeto = getObjeto(idObjeto);
        String nomeDono;
        //Pessoa objetoDono = getPessoa(objeto.getIdDono());
        //nomeDono = objetoDono.getNome();

        textViewNome.setText(objeto.getNome());
        textViewDescricao.setText(objeto.getDescricao());
        textViewPlataforma.setText(objeto.getCategoriaEnum().toString());
        textViewAplicacao.setText(String.valueOf(objeto.getIdDono()));

    }

    public Objeto getObjeto(int idObjeto) {
        Objeto objeto = new Objeto();

        try {
            objeto = objetoNegocio.pesquisarPorId(idObjeto);
        } catch (MindbitException e) {
            Log.d("MeuObjetoActivity", e.getMessage());
        }

        return objeto;
    }

    public Pessoa getPessoa(int idDono){
        Pessoa pessoa = usuarioNegocio.pesquisarPorId(idDono);
        return pessoa;
    }

}
