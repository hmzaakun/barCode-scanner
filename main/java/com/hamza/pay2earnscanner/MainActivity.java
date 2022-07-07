package com.hamza.pay2earnscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scanBtn;
    String nom;
    String prenom;
    String email;
    String ncarte;
    String status;
    String un = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        scanCode();
    }

    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if (result != null){
                if (result.getContents() != null){

                    RequestQueue queue = Volley.newRequestQueue(this);

                    String url = "https://www.pay2earn.store/api.php?ncarte=";
                    url += result.getContents();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        System.out.println(response);

                                        status = response.getString("status");
                                        if (status.equals(un)){
                                            builder.setMessage("❌ cet utilisateur n'existe pas ! ❌");
                                            builder.setTitle("Scanning Result");
                                        }else {
                                            nom = response.getString("nom");
                                            prenom = response.getString("prenom");
                                            email = response.getString("email");
                                            ncarte = response.getString("ncarte");

                                            builder.setMessage("Validé ! ✅\ncet utilisateur est :\n" + prenom + "\n" + nom + "\n" + "N° de carte: " + ncarte + "\n" + email);
                                            builder.setTitle("Scanning Result");
                                        }



                                        builder.setPositiveButton("Scan again", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                scanCode();
                                            }
                                        }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                finish();
                                            }
                                        });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error

                                }
                            });

                    queue.add(jsonObjectRequest);




                } else {
                    Toast.makeText(this,"No result", Toast.LENGTH_LONG).show();
                }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}