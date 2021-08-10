package com.dvorak.produtos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.dvorak.produtos.modelo.Produto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {
    EditText edit_Nome, edit_Descricao, edit_Preco, edit_Data; // 03. inicializacao dos elementos do activity_main.xml
    ListView listV_dados;
    TextView textV_Id;

    FirebaseDatabase firebaseDatabase; //elementos de conexao com o banco de dados firebase
    DatabaseReference databaseReference;

    private List<Produto> listProduto = new ArrayList<Produto>(); // 04. criando lista de dados para exibir no listView
    private ArrayAdapter<Produto> arrayAdapterProduto;

    Produto produtoSelecionado; // 05. elemento para pegar o uid do aluno selecionado dentro do banco

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_Nome = findViewById(R.id.edit_Nome);
        edit_Descricao = findViewById(R.id.edit_Descricao);
        edit_Preco = findViewById(R.id.edit_Preco);
        edit_Data = findViewById(R.id.edit_Data);
        listV_dados = findViewById(R.id.listV_dados);
        textV_Id = findViewById(R.id.textV_Id);



        inicializaFirebase(); // 07.  inicializacao do metodo criado para o firebase

        eventoDatabase(); // metodo para selecionar e exibir a lista de Alunos no listView

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                produtoSelecionado = (Produto) parent.getItemAtPosition(position);
                edit_Nome.setText(produtoSelecionado.getNome());
                textV_Id.setText(produtoSelecionado.getUid());
                edit_Descricao.setText(produtoSelecionado.getDescricao());
                edit_Preco.setText(produtoSelecionado.getPreco());
                edit_Data.setText(produtoSelecionado.getData());
            }
        });

    }


    // 09 metodo ciado para selecionar o aluno dentro do firebase
    private void eventoDatabase() {
        databaseReference.child("Produto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listProduto.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Produto p = objSnapshot.getValue(Produto.class);
                    listProduto.add(p);
                }
                arrayAdapterProduto = new ArrayAdapter<Produto>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, listProduto);
                (listV_dados).setAdapter(arrayAdapterProduto);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    //10  configuracoes do firebase para a aplicacao
    private void inicializaFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();


    }


    // 11 configuracao do menu_main com os 3 botoes
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_novo) { // 12. caso clique no icone de adicionar ele ira criar um novo aluno dando um uid aleatorio
            Produto p = new Produto();
            p.setUid(UUID.randomUUID().toString());
            p.setId(UUID.randomUUID().toString());
            p.setNome(edit_Nome.getText().toString());
            p.setPreco(edit_Preco.getText().toString());
            p.setDescricao(edit_Descricao.getText().toString());
            p.setData(edit_Data.getText().toString());
            databaseReference.child("Produto").child(p.getUid()).setValue(p);
            limpaCampos();
        } else if (id == R.id.menu_atualiza) { // 13. caso clique no icone de editar  ele ira buscar o aluno pelo uid criado anteriormente e
            // ira atualizar os dados preenchidos anteriormente
            Produto p = new Produto();
            p.setUid(UUID.randomUUID().toString().trim());
            p.setNome(edit_Nome.getText().toString().trim());
            p.setPreco(edit_Preco.getText().toString().trim());
            p.setDescricao(edit_Descricao.getText().toString().trim());
            p.setData(edit_Data.getText().toString().trim());
            databaseReference.child("Produto").child(p.getUid()).setValue(p);
            limpaCampos();

        } else if (id == R.id.menu_deleta) { // 14. aqui ele ira selecionar o aluno via o uuid e fara a exclusao do banco
            Produto p = new Produto();
            p.setUid(produtoSelecionado.getUid());
            databaseReference.child("Produto").child(p.getUid()).removeValue();
            limpaCampos();
        }
        return true;
    }

    private void limpaCampos() { // 15. apenas um metodo para limpar os campos a cada acao que o usuario toma

        edit_Nome.setText("");
        edit_Descricao.setText("");
        edit_Preco.setText("");
        edit_Data.setText("");
        textV_Id.setText("");

    }
}