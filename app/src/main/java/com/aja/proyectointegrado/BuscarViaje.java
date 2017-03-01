package com.aja.proyectointegrado;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public class BuscarViaje extends Fragment implements AdapterView.OnItemClickListener{
    private final String TAG = BuscarViaje.class.getSimpleName();
    public static final String LIST = "list";
    private ListView lista;
    private Spinner spinner;
    private EditText busqueda;
    private Button Buscar;
    private ArrayList<Viaje> viajes = new ArrayList<>();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private ChildEventListener childEvent;
    private Viaje viaje;
    private Coche coche;
    private Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_buscar_viaje, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        Buscar = (Button) view.findViewById(R.id.Buscar);
        lista = (ListView) view.findViewById(R.id.list);
        busqueda = (EditText) view.findViewById(R.id.busqueda);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();

        query = database.getReference("Viajes");

        childEvent = new ChildEventListener() {
            ListAdapter adapt;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                viaje = dataSnapshot.getValue(Viaje.class);
                viajes.add(viaje);
                adapt = new ListAdapter(getContext(), viajes);
                lista.setAdapter(adapt);
                //Log.d(TAG, "Funciona");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                viaje = dataSnapshot.getValue(Viaje.class);
                viajes.add(viaje);
                adapt = new ListAdapter(getContext(), viajes);
                lista.setAdapter(adapt);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Failed to load Viaje.", Toast.LENGTH_SHORT).show();
            }
        };
        query.addChildEventListener(childEvent);

        ListAdapter adapter = new ListAdapter(getContext(), viajes);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(this);
        String [] busca;
        busca = new String[7];
        busca[0] = new String("origen");
        busca[1] = new String("destino");
        busca[2] = new String("fecha");
        busca[3] = new String("hora");
        busca[4] = new String("precio");
        busca[5] = new String("plazas");
        busca[6] = new String("usuario");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, busca);
        spinner.setAdapter(adapter2);
        Buscar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String opcion = spinner.getSelectedItem().toString();
                Log.d(TAG, opcion);
                viajes.clear();
                lista.getAdapter();
                if(busqueda.getText().toString().equals("")){
                    query = database.getReference("Viajes");
                }else {
                    query = database.getReference("Viajes").orderByChild(opcion).equalTo(busqueda.getText().toString());
                }
                childEvent = new ChildEventListener() {
                    ListAdapter adapt;
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        viaje = dataSnapshot.getValue(Viaje.class);
                        viajes.add(viaje);
                        adapt=  new ListAdapter(getContext(),viajes);
                        lista.setAdapter(adapt);
                        //Log.d(TAG, "Funciona");
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        viaje = dataSnapshot.getValue(Viaje.class);
                        viajes.add(viaje);
                        adapt =  new ListAdapter(getContext(),viajes);
                        lista.setAdapter(adapt);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                        Toast.makeText(getActivity(), "Failed to load Viaje.", Toast.LENGTH_SHORT).show();
                    }
                };
                query.addChildEventListener(childEvent);

                ListAdapter adapter = new ListAdapter(getContext(),viajes);
                lista.setAdapter(adapter);
                //lista.setOnItemClickListener(this);
            }

        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        if (viajes.get(position).getPlazas() < 1) {
            Toast.makeText(getActivity(), "El viaje esta Completo", Toast.LENGTH_SHORT).show();
        } else if(viajes.get(position).getUidConductor().equals(uid)) {
            Toast.makeText(getActivity(), "NO puedes reservar un viaje creado por ti", Toast.LENGTH_SHORT).show();
        }else {
            Intent in = new Intent();
            in.putExtra(LIST, viajes.get(position));
            showChangeLangDialog(in);
        }
    }

    public void showChangeLangDialog(Intent in) {
        viaje = in.getParcelableExtra(BuscarViaje.LIST);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_aceptar_viaje, null);
        dialogBuilder.setView(dialogView);
        final EditText etReserva = (EditText) dialogView.findViewById(R.id.etReserva);
        final TextView usuario = (TextView) dialogView.findViewById(R.id.usuario);
        final TextView txtOrigen = (TextView) dialogView.findViewById(R.id.txtOrigen);
        final TextView txtDestino = (TextView) dialogView.findViewById(R.id.txtDestino);
        final TextView txtMarca = (TextView) dialogView.findViewById(R.id.txtMarca);
        final TextView txtAno = (TextView) dialogView.findViewById(R.id.txtAno);
        dialogBuilder.setTitle("Reservar Viaje");
        dialogBuilder.setMessage("Introduzca las plazas deseadas");
        usuario.setText("El publicador del viaje es : "+viaje.usuario.getNombre());
        txtOrigen.setText("Origen Viaje: "+viaje.getOrigen());
        txtDestino.setText("Destino Viaje: "+viaje.getDestino());
        txtMarca.setText("Marca Coche: "+viaje.coche.getMarca());
        txtAno.setText("Año Coche: "+viaje.coche.getAnoCoche());
        dialogBuilder.setPositiveButton("Hecho", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (etReserva.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Tienes que poner algun asiento para reservar", Toast.LENGTH_SHORT).show();
                } else if (viaje.getPlazas() <= 0) {
                    Toast.makeText(getActivity(), "Debes tener algun asiento disponible", Toast.LENGTH_SHORT).show();
                } else if (viaje.getPlazas() - Integer.parseInt(etReserva.getText().toString()) < 0) {
                    Toast.makeText(getActivity(), "No puedes reservas tantos asientos", Toast.LENGTH_SHORT).show();
                }else{
                    android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿ Acepta la Reservacion de este viaje?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            viaje.setPlazas(viaje.getPlazas() - Integer.parseInt(etReserva.getText().toString()));
                            String key = viaje.getKeyViaje();
                            database.getReference("Viajes").child(key).setValue(viaje);
                            viaje.setPlazas(Integer.parseInt(etReserva.getText().toString()));
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            myRef = database.getInstance().getReference();
                            String keyR = myRef.child("posts").push().getKey();
                            String uidReserva = user.getUid();
                            viaje.setUidReserva(uidReserva);
                            database.getReference("Reservas").child(keyR).setValue(viaje);
                            notificacion();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getActivity(), "Se ha cancelado la operación", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void notificacion() {
        NotificationManager nManager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Reserva Viaje")
                .setContentText("Se ha reservado el viaje.")
                .setWhen(System.currentTimeMillis());

        Intent targetIntent = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0,
                targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        builder.setAutoCancel(true);

        nManager.notify(123456, builder.build());
    }

}
