package com.example.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String GPAY_PACKAGE_NAME="com.google.android.apps.nbu.paisa.user";
    EditText name,upiid,amount,note;
    TextView msg;
    Button pay;
    Uri uri;
    public static String payerName,upiID,msgNote,sendAmount,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        name=findViewById(R.id.name);
        upiid=findViewById(R.id.upi_id);
        amount=findViewById(R.id.amount);
        note=findViewById(R.id.t_note);
        pay=findViewById(R.id.pay);
        msg=findViewById(R.id.status);


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                payerName=name.getText().toString();
                upiID=upiid.getText().toString();
                msgNote=msg.getText().toString();
                sendAmount=amount.getText().toString();

                if (!payerName.equals("")&& !upiID.equals("") && !msgNote.equals("")&& !sendAmount.equals(""))
                {
                    uri= getUpiPaymenturi(payerName,upiID,msgNote,sendAmount);
                    payWithGpay(GPAY_PACKAGE_NAME);
                }
            }
        });

    }

    private static Uri getUpiPaymenturi(String name,String upiId,String note, String amount)
    {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();
    }
private void payWithGpay(String packageName)
{
   if (isAppInstalled(this,packageName))
   {
       Intent intent = new Intent (Intent.ACTION_VIEW);
       intent.setData(uri);
       intent.setPackage(packageName);
       startActivityForResult(intent,0);
   }else
   {
       Toast.makeText(MainActivity.this,"Google Pay is not installed",Toast.LENGTH_SHORT).show();
   }
}

@SuppressLint("SetTextI18n")
public void onActivityResult(int requestCode, int resultCode, Intent data)
{
    super.onActivityResult(requestCode,resultCode,data);
 //   Log.d("data",data.toString());
    if (data != null)
    {
        status = data.getStringExtra("status").toLowerCase();
    }
    if((RESULT_OK==resultCode)&& status.equals("success"))
    {
        Toast.makeText(MainActivity.this,"Transaction succesful",Toast.LENGTH_SHORT).show();
        msg.setText("Transaction Sucessful of rupees "+sendAmount);
        msg.setTextColor(Color.GREEN);
    }else
    {
        Toast.makeText(MainActivity.this,"Transaction failed",Toast.LENGTH_SHORT).show();
        msg.setText("Transaction failed of rupees "+sendAmount);
        msg.setTextColor(Color.RED);
    }


}

    public static boolean isAppInstalled(Context context, String  packageName)
    {
        try{
                context.getPackageManager().getApplicationInfo(packageName,0);
                return true;

        }catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }


}
