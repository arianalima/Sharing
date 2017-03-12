/*package br.com.mindbit.controleacesso.gui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.mindbit.R;
import br.com.mindbit.controleacesso.dominio.Amigo;
import br.com.mindbit.controleacesso.dominio.Objeto;
import br.com.mindbit.controleacesso.dominio.Pessoa;
import br.com.mindbit.controleacesso.negocio.ObjetoNegocio;
import br.com.mindbit.controleacesso.negocio.SessaoUsuario;
import br.com.mindbit.infra.gui.GuiUtil;
import br.com.mindbit.infra.gui.MindbitException;

public class CompartilharEventoActivity extends AppCompatActivity{
    private Resources resources;

    private ObjetoNegocio objetoNegocio;
    private SessaoUsuario sessaoUsuario;
    private Pessoa pessoaLogada;

    private ArrayList<Objeto> eventosPessoa;
    private ArrayList<Objeto> eventosMarcardos;
    private ArrayList<Objeto> listItems = new ArrayList<>();
    private String todosEmails;

    private ListView listaEventos;
    private Button btnCompartilhar;
    private Button btnEscolher;
    private AdapterCompartilharEvento adapterCompartilhar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartilhar_evento);

        resources = getResources();

        objetoNegocio = ObjetoNegocio.getInstancia(this);
        sessaoUsuario = SessaoUsuario.getInstancia();
        pessoaLogada = sessaoUsuario.getPessoaLogada();

        listaEventos = (ListView) findViewById(R.id.ListView_compartilhar_evento);
        btnCompartilhar = (Button) findViewById(R.id.btn_compartilhar_evento);
        btnEscolher = (Button) findViewById(R.id.btn_escolher_amigos);
        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilhar(v);
            }
        });

        btnEscolher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirEscolherAmigo(v);
            }
        });

        adapterCompartilhar = new AdapterCompartilharEvento(this, listItems);

        try {
            iniciarLista();
        } catch (MindbitException e) {
            Log.d("CompartilharEventoActvt", e.getMessage());
        }
    }

    public List<Objeto> getEventosSelecionados(){
        ArrayList<String> nomes = adapterCompartilhar.getNomesEventos();
        eventosMarcardos = new ArrayList<>();

        for (String nome:nomes){
            try {
                if (objetoNegocio.pesquisarPorNome(nome)!=null) {
                    Objeto objeto = objetoNegocio.pesquisarPorNome(nome);
                    eventosMarcardos.add(objeto);
                }
            }catch (MindbitException e){
                Log.d("CompartilharEventoActvt", e.getMessage());
            }
        }
        return eventosMarcardos;
    }

    public void abrirEscolherAmigo(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_escolher_amigos:
                if (getEventosSelecionados().size()!=0) {
                    StringBuilder conteudoEmail = getAppConteudo(getEventosSelecionados());
                    Intent i = new Intent(this, EscolherAmigoActivity.class);
                    i.putExtra("message", conteudoEmail.toString());
                    startActivity(i);
                }else{
                    GuiUtil.exibirMsg(this, resources.getString(R.string.checkbox_evento_vazio));
                }
        }
    }
    public void compartilhar(View v){
        int id = v.getId();
        switch (id){
            case R.id.btn_compartilhar_evento:
                construirMensagemCompartilhada();
        }
    }
    public void construirMensagemCompartilhada(){
        String[] emailsDestino = new String[]{getAmigosEmails()};
        StringBuilder conteudoEmail = getAppConteudo(getEventosSelecionados());

        if (getEventosSelecionados().size()==0){
            GuiUtil.exibirMsg(this,resources.getString(R.string.checkbox_evento_vazio));
        }else {
            String subject = (resources.getString(R.string.compartilhar_email_assunto));
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_EMAIL, emailsDestino);
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, conteudoEmail.toString());
            startActivity(Intent.createChooser(i, resources.getString(R.string.compartilhar)));
        }
    }

    public String getAmigosEmails(){
        List<Amigo> todosAmigos = pessoaLogada.getAmigos();

        if(todosAmigos != null){
            for (Amigo amigo: todosAmigos){
                        todosEmails += amigo.getEmail()+",";
                    }
            }
        return todosEmails;
    }

    public StringBuilder getAppConteudo(List<Objeto> objetos){
        StringBuilder infoEventos = new StringBuilder();

       for (Objeto objeto : objetos) {
           infoEventos.append(getInfoEventoApp(objeto));
       }

        infoEventos.append(resources.getString(R.string.assinatura_email) + pessoaLogada.getNome() + "." + "\n" +
                resources.getString(R.string.rodape_email));
        return infoEventos;
    }

    public void iniciarLista() throws MindbitException{
        eventosPessoa = objetoNegocio.listarEventosProximo(pessoaLogada.getId());

        adapterCompartilhar = new AdapterCompartilharEvento(this, eventosPessoa);
        listaEventos.setAdapter(adapterCompartilhar);
    }

    public String getInfoEventoApp(Objeto objeto){
        String nomeEvento = objeto.getNome();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String dataInicio = simpleDateFormat.format(addOneMonth(objeto.getDataInicio()));
        String dataFim = simpleDateFormat.format(addOneMonth(objeto.getDataFim()));

        String informacoes = "\n" + nomeEvento + resources.getString(R.string.data_inicio_evento) + dataInicio +
                resources.getString(R.string.data_fim_evento) + dataFim + "\n";
        return informacoes;
    }

    public static Date addOneMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

}


*/