package com.example.minifinanzas;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import com.example.minifinanzas.entidades.Recordatorio;
import com.example.minifinanzas.utilidades.utilidades;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView ListaRecord;
    ArrayList <String> listaInformacion;
    ArrayList <Recordatorio> listaRecordatorios;
    ArrayAdapter<String> adapter;
    ConexionSQLiteHelper conexion;
    TextView Vingresos;
    TextView Vgastos;
    TextView Vtotal;
    String[] listaAcciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton Nuevo = (FloatingActionButton) findViewById(R.id.Nuevo);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListaRecord = (ListView) findViewById(R.id.ListaRecord);


        conexion = new ConexionSQLiteHelper(getApplicationContext(),"bd_Recordatorios",null,1);
        consultarListaRecordatorios();

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
        ListaRecord.setAdapter(adapter);

        consultarListaRecordatorios();

        //AQUI SE ACTUALIZA EL NUMERO TOTAL//

        Vingresos = (TextView) findViewById(R.id.Vingresos);
        Double totI = calcularIngresos();
        //String totalI = convierteCifra(totI);
        Vingresos.setText(convierteCifra(totI));

        Vgastos = (TextView) findViewById(R.id.Vgastos);
        Double totG = calcularGastos();
        //String totalG = myFormatter.format(totG);
        Vgastos.setText(convierteCifra(totG));

        Vtotal = (TextView) findViewById(R.id.Vtotal);
        Double totF = calcularTotal();
        Vtotal.setText(convierteCifra(totF));


        ListaRecord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                listaAcciones = new String[]{"Editar", "Borrar"};
                final AlertDialog.Builder constructor = new AlertDialog.Builder(MainActivity.this);

                constructor.setTitle("¿Que deseas hacer con el registro?");
                constructor.setSingleChoiceItems(listaAcciones, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            dialogInterface.cancel();
                            final AlertDialog.Builder con = new AlertDialog.Builder(MainActivity.this);
                            con.setTitle("Editar registro");
                            con.setMessage("¿Desea editar el registro?");

                            con.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int idEditar = listaRecordatorios.get(position).getId();
                                    Intent act = new Intent(MainActivity.this, EditarRecordatorio.class);
                                    act.putExtra("id", idEditar);
                                    startActivity(act);
                                    finish();
                                }
                            });
                            con.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog = con.create();
                            dialog.show();

                        }

                        if (i == 1) {
                            dialogInterface.cancel();
                            final AlertDialog.Builder con = new AlertDialog.Builder(MainActivity.this);
                            con.setTitle("Eliminar registro");
                            con.setMessage("¿Desea borrar el registro?");

                            con.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String borrarSQ = "DELETE FROM " + utilidades.TABLA_RECORDATORIOS + " WHERE " + utilidades.CAMPO_ID + "=" + listaRecordatorios.get(position).getId();
                                    SQLiteDatabase db = conexion.getWritableDatabase();
                                    db.execSQL(borrarSQ);
                                    db.close();

                                    Intent actualizar = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(actualizar);
                                    finish();
                                }
                            });
                            con.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog = con.create();
                            dialog.show();
                        }
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = constructor.create();
                dialog.show();
                return false;
            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_todos) {
            Toast.makeText(getApplicationContext(),"Todos los registros",Toast.LENGTH_SHORT).show();

            conexion = new ConexionSQLiteHelper(getApplicationContext(),"bd_Recordatorios",null,1);
            consultarListaRecordatorios();

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
            ListaRecord.setAdapter(adapter);
            consultarListaRecordatorios();

        } else if (id == R.id.nav_ingresos) {
            Toast.makeText(getApplicationContext(),"Seleccionaste Ingresos",Toast.LENGTH_SHORT).show();

            conexion = new ConexionSQLiteHelper(getApplicationContext(),"bd_Recordatorios",null,1);
            consultarIngresos();

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
            ListaRecord.setAdapter(adapter);
            consultarIngresos();

        } else if (id == R.id.nav_gastos) {
            Toast.makeText(getApplicationContext(),"Seleccionaste Gastos",Toast.LENGTH_SHORT).show();

            conexion = new ConexionSQLiteHelper(getApplicationContext(),"bd_Recordatorios",null,1);
            consultarGastos();

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
            ListaRecord.setAdapter(adapter);
            consultarGastos();

        } else if (id == R.id.nav_metas) {
            Toast.makeText(getApplicationContext(),"Seleccionaste Metas",Toast.LENGTH_SHORT).show();

            conexion = new ConexionSQLiteHelper(getApplicationContext(),"bd_Recordatorios",null,1);
            consultarMetas();

            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,listaInformacion);
            ListaRecord.setAdapter(adapter);
            consultarMetas();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Nuevo (View view){
        Intent nuevo = new Intent(this, CrearRecordatorio.class);
        startActivity(nuevo);
    }


    public String convierteCifra( double cantE){
        String patron ="###,###,###.##";
        DecimalFormat myFormatter = new DecimalFormat(patron);

        Double cantidadEntrante = cantE;
        String cantidadS = myFormatter.format(cantidadEntrante);
        String cantidadSalida = "$ "+cantidadS;
        return cantidadSalida;
    }

    public Double calcularIngresos() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        String sumarT = "SELECT SUM("+utilidades.CAMPO_CANTIDAD+") AS TotalI FROM " +utilidades.TABLA_RECORDATORIOS+ " WHERE "+utilidades.CAMPO_CATEGORIA+" ='Ingreso' ";
        Cursor curt = db.rawQuery(sumarT,null);

        if (curt.moveToFirst()){
            double TotalI = curt.getDouble(curt.getColumnIndex("TotalI"));
            return TotalI;
        }while (curt.moveToNext());
        return calcularIngresos();
    }

    public Double calcularGastos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        String sumarT = "SELECT SUM("+utilidades.CAMPO_CANTIDAD+") AS TotalG FROM " +utilidades.TABLA_RECORDATORIOS+ " WHERE "+utilidades.CAMPO_CATEGORIA+" ='Gasto' ";
        Cursor curt = db.rawQuery(sumarT,null);

        if (curt.moveToFirst()){
            double TotalG = curt.getDouble(curt.getColumnIndex("TotalG"));
            return TotalG;
        }while (curt.moveToNext());
        return calcularGastos();
    }


    public Double calcularTotal() {
        double ingreso = calcularIngresos();
        double gasto= calcularGastos();
        double totalF = ingreso - gasto;

        return totalF;
    }



    public void consultarListaRecordatorios() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        Recordatorio recordatorio = null;

        listaRecordatorios = new ArrayList<Recordatorio>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+utilidades.TABLA_RECORDATORIOS+ " ORDER BY " +utilidades.CAMPO_FECHA+" ASC ",null);

       while (cursor.moveToNext()){
           recordatorio = new Recordatorio();

           recordatorio.setId(cursor.getInt(0));
           recordatorio.setNombre(cursor.getString(1));
           recordatorio.setDescripcion(cursor.getString(2));
           recordatorio.setCantidad(cursor.getDouble(3));
           recordatorio.setFecha(cursor.getString(4));
           recordatorio.setCategoria(cursor.getString(5));

           listaRecordatorios.add(recordatorio);
       }
        obtenerLista();
    }

    public void consultarIngresos() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        Recordatorio recordatorio = null;

        listaRecordatorios = new ArrayList<Recordatorio>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+utilidades.TABLA_RECORDATORIOS+" WHERE "+utilidades.CAMPO_CATEGORIA+" ='Ingreso' "+" ORDER BY " +utilidades.CAMPO_FECHA+" ASC ",null);

        while (cursor.moveToNext()){
            recordatorio = new Recordatorio();

            recordatorio.setId(cursor.getInt(0));
            recordatorio.setNombre(cursor.getString(1));
            recordatorio.setDescripcion(cursor.getString(2));
            recordatorio.setCantidad(cursor.getDouble(3));
            recordatorio.setFecha(cursor.getString(4));
            recordatorio.setCategoria(cursor.getString(5));

            listaRecordatorios.add(recordatorio);
        }
        obtenerLista();
    }

    public void consultarGastos() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        Recordatorio recordatorio = null;

        listaRecordatorios = new ArrayList<Recordatorio>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+utilidades.TABLA_RECORDATORIOS+" WHERE "+utilidades.CAMPO_CATEGORIA+" ='Gasto' "+" ORDER BY " +utilidades.CAMPO_FECHA+" ASC ",null);

        while (cursor.moveToNext()){
            recordatorio = new Recordatorio();

            recordatorio.setId(cursor.getInt(0));
            recordatorio.setNombre(cursor.getString(1));
            recordatorio.setDescripcion(cursor.getString(2));
            recordatorio.setCantidad(cursor.getDouble(3));
            recordatorio.setFecha(cursor.getString(4));
            recordatorio.setCategoria(cursor.getString(5));

            listaRecordatorios.add(recordatorio);
        }
        obtenerLista();
    }

    public void consultarMetas() {

        SQLiteDatabase db = conexion.getReadableDatabase();
        Recordatorio recordatorio = null;

        listaRecordatorios = new ArrayList<Recordatorio>();

        Cursor cursor = db.rawQuery("SELECT * FROM "+utilidades.TABLA_RECORDATORIOS+" WHERE "+utilidades.CAMPO_CATEGORIA+" ='Meta' "+" ORDER BY " +utilidades.CAMPO_FECHA+" ASC ",null);

        while (cursor.moveToNext()){
            recordatorio = new Recordatorio();

            recordatorio.setId(cursor.getInt(0));
            recordatorio.setNombre(cursor.getString(1));
            recordatorio.setDescripcion(cursor.getString(2));
            recordatorio.setCantidad(cursor.getDouble(3));
            recordatorio.setFecha(cursor.getString(4));
            recordatorio.setCategoria(cursor.getString(5));

            listaRecordatorios.add(recordatorio);
        }
        obtenerLista();
    }


    //SUSTITUIR ESTE OBTENER LISTA CON LA IMAGEN COMENZANDO CON LIST ADAPTER
    public void obtenerLista() {

        listaInformacion = new ArrayList<String>();

        for (int i=0; i<listaRecordatorios.size();i++){

            double cant = 0;
            cant = listaRecordatorios.get(i).getCantidad();

            String cantidadT = convierteCifra(cant);

            listaInformacion.add(listaRecordatorios.get(i).getCategoria()+" | "+cantidadT+" | "+listaRecordatorios.get(i).getFecha()+" | "+listaRecordatorios.get(i).getNombre());
        }
    }
}
