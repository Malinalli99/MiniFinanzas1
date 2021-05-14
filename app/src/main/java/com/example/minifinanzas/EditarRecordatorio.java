package com.example.minifinanzas;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minifinanzas.utilidades.utilidades;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditarRecordatorio extends AppCompatActivity implements View.OnClickListener {

    ImageButton Calendario,Hora;
    Button Cancelar, Guardar;
    EditText Nombre, Descripcion, Cantidad;
    CheckBox Ingreso,Gasto,Meta;
    TextView Vcalendario;
    String horaS,fechaS,nombreS,descripcionS;
    String Catego;
    String fech;
    ConexionSQLiteHelper conexion;
    private int año, mes, dia, hora, minutos;

    int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_recordatorio);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Calendario = (ImageButton) findViewById(R.id.Calendario);
        Vcalendario = (TextView) findViewById(R.id.Vcalendario);
        Cancelar = (Button) findViewById(R.id.Cancelar);
        Guardar = (Button) findViewById(R.id.Guardar);

        Nombre = (EditText) findViewById(R.id.Nombre);
        Descripcion = (EditText) findViewById(R.id.Descripcion);
        Cantidad = (EditText) findViewById(R.id.Cantidad);

        Ingreso = (CheckBox) findViewById(R.id.Ingreso);
        Gasto = (CheckBox) findViewById(R.id.Gasto);
        Meta = (CheckBox) findViewById(R.id.Meta);

        Cancelar.setOnClickListener(this);
        Guardar.setOnClickListener(this);

        Calendario.setOnClickListener(this);



        conexion = new ConexionSQLiteHelper(getApplicationContext(),"bd_Recordatorios",null,1);

        mostrarDatos();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v == Calendario) {
            final Calendar calendar = Calendar.getInstance();
            año = calendar.get(Calendar.YEAR);
            mes = calendar.get(Calendar.MONTH);
            dia = calendar.get(Calendar.DAY_OF_MONTH);
            contador =1;

            final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR,year);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                    String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                    fech = df.format(calendar.getTime());
                    Vcalendario.setText(date);



                }
            },año,mes,dia);
            datePickerDialog.show();

        }

        if (v == Guardar){
            validacionDatos();
        }


        if (v == Cancelar){
            Borrar();
            Intent actualizar = new Intent(EditarRecordatorio.this, MainActivity.class);
            startActivity(actualizar);
            finish();
        }

    }



    public void Borrar(){
        Nombre.setText("");
        Descripcion.setText("");
        Cantidad.setText("");
        Vcalendario.setText("Fecha");
        if (Ingreso.isChecked()) {
            Ingreso.toggle();
        }
        if (Gasto.isChecked()) {
            Gasto.toggle();
        }
        if (Meta.isChecked()) {
            Meta.toggle();
        }
    }

    public void validacionDatos(){

        if (Nombre.getText().toString().equals("")&&Cantidad.getText().toString().equals("")&&Vcalendario.getText().toString().equals("Fecha")){
            Toast.makeText(getApplicationContext(),"Ingresa los datos del registro",Toast.LENGTH_SHORT).show();
        }else if (Nombre.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Ingresa el nombre del registro",Toast.LENGTH_SHORT).show();
        } else if (Cantidad.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Ingresa la cantidad del registro",Toast.LENGTH_SHORT).show();
        } else if (Vcalendario.getText().toString().equals("Fecha")){
            Toast.makeText(getApplicationContext(),"Agrega una fecha tocando el calendario",Toast.LENGTH_SHORT).show();
        } else{
            Categoria();
        }

    }

    public void mostrarDatos(){
        SQLiteDatabase con = conexion.getReadableDatabase();
        int id = recibirId();

        try{
            Cursor cursor = con.rawQuery("SELECT "+utilidades.CAMPO_NOMBRE+","+utilidades.CAMPO_DESCRIPCION+","+utilidades.CAMPO_CANTIDAD+","+utilidades.CAMPO_FECHA+","+utilidades.CAMPO_CATEGORIA+" FROM "+utilidades.TABLA_RECORDATORIOS+" WHERE "+utilidades.CAMPO_ID+"="+id,null);
            cursor.moveToFirst();
            Nombre.setText(cursor.getString(0));
            Descripcion.setText(cursor.getString(1));
            Cantidad.setText(cursor.getString(2));
            Vcalendario.setText(cursor.getString(3));

            String categoria = cursor.getString(4);
            if (categoria.equals("Ingreso")){
                Ingreso.setChecked(true);
            }else if (categoria.equals("Gasto")){
                Gasto.setChecked(true);
            }else if (categoria.equals("Meta")){
                Meta.setChecked(true);
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"El registro no existe",Toast.LENGTH_SHORT).show();
        }


    }

    private void EditarRegistro() {
        ConexionSQLiteHelper conexion = new ConexionSQLiteHelper (this,"bd_Recordatorios",null,1);

        int id = recibirId();

        SQLiteDatabase db = conexion.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(utilidades.CAMPO_NOMBRE,Nombre.getText().toString() );
        values.put(utilidades.CAMPO_DESCRIPCION,Descripcion.getText().toString());
        values.put(utilidades.CAMPO_CANTIDAD,Cantidad.getText().toString());
        if (contador==0) {
            values.put(utilidades.CAMPO_FECHA, Vcalendario.getText().toString());
        } else if (contador == 1){
            values.put(utilidades.CAMPO_FECHA, fech);
        }
        values.put(utilidades.CAMPO_CATEGORIA,Catego);
        db.update(utilidades.TABLA_RECORDATORIOS,values,utilidades.CAMPO_ID+"="+id,null);
        Toast.makeText(getApplicationContext(),"Registro editado correctamente",Toast.LENGTH_SHORT).show();
        Borrar();

        Intent actualizar = new Intent(EditarRecordatorio.this, MainActivity.class);
        startActivity(actualizar);
        finish();
    }

    public int recibirId(){
        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("id");
        return id;
    }

    public String Categoria (){
        int contador = 0;
        String seleccion = "";

        if (Ingreso.isChecked()==true){
            contador++;
            seleccion = "Ingreso";
        }

        if (Gasto.isChecked()==true){
            seleccion = "Gasto";
            contador++;
        }

        if (Meta.isChecked()==true){
            seleccion = "Meta";
            contador++;
        }

        if (contador>1){
            Toast.makeText(getApplicationContext(),"Solo puedes seleccionar una categoria",Toast.LENGTH_SHORT).show();

        } else{
            if (contador<1){
                Toast.makeText(getApplicationContext(),"Selecciona una categoria",Toast.LENGTH_SHORT).show();
            } else {
                Catego = seleccion;
                EditarRegistro();
            }
        }
        return Catego ;
    }

}

