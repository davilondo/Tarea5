package com.example.usuario1.tarea5;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.Thread.getAllStackTraces;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private ArrayAdapter adapter;
    private ArrayList<Punto> lista;
    private ListView lv;
    private GestionDB gest;
    private MainActivity activity=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mientras no se tengan permisos de ubicacion se pediran al usuario
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        gest=new GestionDB(this);//se inicializa el objeto gestor de la base de datos
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1);//adapter de la lista con las ubicaciones de la lista
        lista=new ArrayList<Punto>();//lista de puntos
        lista=gest.recuperarPuntos();//recuperar lista de puntos de la base de datos
        adapter.addAll(lista);
        lv=(ListView) findViewById(R.id.lst_rutas);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Punto p=lista.get(i);
                Intent intent=new Intent(activity,MapsActivity.class);
                intent.putExtra("Punto",p);
                startActivity(intent);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final int ind=i;
                AlertDialog.Builder builder= new AlertDialog.Builder(activity);
                builder.setTitle("Eliminar");
                builder.setMessage("¿Estás seguro de que deseas eliminar esta ubicacion?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {//confirmacion de borrar
                        Punto p=lista.get(ind);
                        gest.eliminar(p);
                        adapter.remove(p);
                        lista.remove(p);
                        lv.setAdapter(adapter);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.borrar://si se pulsa en el boton borrar se borran todos de la bd y de la lista
                AlertDialog.Builder builder= new AlertDialog.Builder(activity);
                builder.setTitle("Eliminar");
                builder.setMessage("¿Estás seguro de que deseas eliminar las ubicaciones?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {//confirmacion de borrar
                        for (int j=0;i<lista.size();j++) {
                            Punto p = lista.get(j);
                            gest.eliminar(p);
                        }
                        lista.clear();
                        adapter.clear();
                        lv.setAdapter(adapter);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

                return true;
            case R.id.marcar://si se pulsa marcar se abrira la activity con el mapa esperando como resultado un punto a guardar en la bd
                Intent i=new Intent(this,MapsActivity.class);
                startActivityForResult(i,0);
                return true;

        }
        return true;
    }

    /**
     * metodo escuchador de los resultados lanzados por esta activity. en este caso obtendra un objeto Punto que se almacenara en la bd y la lista
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            Bundle b=data.getExtras();
            Punto p=(Punto) b.getSerializable("Punto");
            lista.add(p);
            gest.insertar(p);
            adapter.add(p);
            lv.setAdapter(adapter);
        }
    }
}
