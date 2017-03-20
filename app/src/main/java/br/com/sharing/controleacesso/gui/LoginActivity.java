package br.com.sharing.controleacesso.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import br.com.sharing.controleacesso.dominio.Usuario;
import br.com.sharing.R;
import br.com.sharing.controleacesso.negocio.UsuarioNegocio;
import br.com.sharing.controleacesso.negocio.Criptografia;
import br.com.sharing.infra.gui.GuiUtil;
import br.com.sharing.infra.gui.SharingException;


public class LoginActivity extends Activity implements View.OnClickListener {
    private ImageView icone;

    private EditText edtUser;
    private EditText edtPassword;

    private Button btnEnter;
    private Button btnCadastrar;

    private Resources resources;
    private static Context contexto;
    private Usuario usuario;
    private UsuarioNegocio usuarioNegocio;
    private Criptografia criptografia;
    private String senhaCriptografada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        contexto = this;
        criptografia = Criptografia.getInstancia();
        icone = (ImageView) findViewById(R.id.fotoPerfil);

        btnEnter = (Button) findViewById(R.id.bt_signIn);
        btnEnter.setOnClickListener(this);
        btnCadastrar = (Button) findViewById(R.id.bt_signUp);
        btnCadastrar.setOnClickListener(this);

        edtUser = (EditText) findViewById(R.id.userLogin);
        edtPassword = (EditText) findViewById(R.id.userPassword);

        initViews();

    }

    /**
     * este metodo eh chamado logo que a janela da activity se faz visivel
     */
    @Override
    protected void onResume() {
        super.onResume();
        usuarioNegocio=UsuarioNegocio.getInstancia(this);
    }

    private void initViews() {
        resources = getResources();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtUser = (EditText) findViewById(R.id.userLogin);
        edtUser.addTextChangedListener(textWatcher);

        edtPassword = (EditText) findViewById(R.id.userPassword);
        edtPassword.addTextChangedListener(textWatcher);

    }

    private boolean validateFields(){
        String user = edtUser.getText().toString().trim();
        String pass = edtPassword.getText().toString();
        return (!isEmptyFields(user, pass) && hasSizeValid(user, pass) && !noHasSpaceLogin(user));
    }

    private boolean isEmptyFields(String user, String pass) {
        if (TextUtils.isEmpty(user)) {
            edtUser.requestFocus();
            edtUser.setError(resources.getString(R.string.login_vazio));
            return true;
        } else if (TextUtils.isEmpty(pass)) {
            edtPassword.requestFocus();
            edtPassword.setError(resources.getString(R.string.login_senha_vazia));
            return true;
        }
        return false;
    }

    private boolean hasSizeValid(String user, String pass) {
        if (!(user.length() > 4)) {
            edtUser.requestFocus();
            edtUser.setError(resources.getString(R.string.login_curto));
            return false;
        } else if (!(pass.length() > 4)) {
            edtPassword.requestFocus();
            edtPassword.setError(resources.getString(R.string.login_senha_curta));
            return false;
        }
        return true;
    }

    private boolean noHasSpaceLogin(String user) {
        int idx = user.indexOf(" ");
        if (idx != -1){
            edtUser.requestFocus();
            edtUser.setError(resources.getString(R.string.login_invalido));
            return true;
        }return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.bt_signIn:
                logar(v);
                break;
            case R.id.bt_signUp:
                startSignUpActivity();
                break;
        }
    }

    private void logar(View view){
        if (validateFields()){
            try {
                String login = edtUser.getText().toString();
                String senha = edtPassword.getText().toString();

                criptografia.receberSenhaOriginal(senha);
                senhaCriptografada = criptografia.getSenhaCriptografada();

                usuario = usuarioNegocio.logar(login, senhaCriptografada);
                GuiUtil.exibirSaudacao(this);
                startNavigationActivity();

            }catch (SharingException e){
                GuiUtil.exibirMsg(LoginActivity.this, e.getMessage());

            }
        }
    }

    public void startSignUpActivity() {
        Intent i = new Intent(LoginActivity.this,CadastroActivity.class);
        startActivity(i);
    }

    public void startNavigationActivity() {
        startActivity(new Intent(this, PerfilActivity.class));
        finish();
    }

   public static Context getContexto(){ return contexto; }

}
