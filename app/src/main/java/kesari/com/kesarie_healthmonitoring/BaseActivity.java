package kesari.com.kesarie_healthmonitoring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import kesari.com.kesarie_healthmonitoring.Utils.IOUtils;
import kesari.com.kesarie_healthmonitoring.Utils.SharedPrefUtil;
import mehdi.sakout.fancybuttons.FancyButton;

public class BaseActivity extends AppCompatActivity {

    TextView flag,Systolic_High,Systolic_Low,Diastolic_High,Diastolic_Low,Map_High,Map_Low,Pulse_Rate_High,Pulse_Rate_Low,User_ID,Meas_Status_High,Meas_Status_Low,weight,glucose,time;
    TextView Celcius,Farenheit;
    LinearLayout BPM_HOLDER,Weight_HOLDER,Glucose_HOLDER,Thermometer_HOLDER;

    private BroadcastReceiver bpmReceiver = new BPMReceiver();
    private BroadcastReceiver weightReceiver = new WeightReceiver();
    private BroadcastReceiver glucoseReceiver = new GlucoseReceiver();
    private BroadcastReceiver thermometerReceiver = new ThermometerReceiver();
    String Value;
    FancyButton save,clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        flag = (TextView) findViewById(R.id.flag);
        Systolic_High = (TextView) findViewById(R.id.Systolic_High);
        Systolic_Low = (TextView) findViewById(R.id.Systolic_Low);
        Diastolic_High = (TextView) findViewById(R.id.Diastolic_High);
        Diastolic_Low = (TextView) findViewById(R.id.Diastolic_Low);
        Map_High = (TextView) findViewById(R.id.Map_High);
        Map_Low = (TextView) findViewById(R.id.Map_Low);
        Pulse_Rate_High = (TextView) findViewById(R.id.Pulse_Rate_High);
        Pulse_Rate_Low = (TextView) findViewById(R.id.Pulse_Rate_Low);
        User_ID = (TextView) findViewById(R.id.User_ID);
        Meas_Status_High = (TextView) findViewById(R.id.Meas_Status_High);
        Meas_Status_Low = (TextView) findViewById(R.id.Meas_Status_Low);
        weight = (TextView) findViewById(R.id.weight);
        glucose = (TextView) findViewById(R.id.glucose);
        time = (TextView) findViewById(R.id.time);
        Celcius = (TextView) findViewById(R.id.Celcius);
        Farenheit = (TextView) findViewById(R.id.Farenheit);

        BPM_HOLDER = (LinearLayout) findViewById(R.id.BPM_HOLDER);
        Weight_HOLDER = (LinearLayout) findViewById(R.id.Weight_HOLDER);
        Glucose_HOLDER = (LinearLayout) findViewById(R.id.Glucose_HOLDER);
        Thermometer_HOLDER = (LinearLayout) findViewById(R.id.Thermometer_HOLDER);

        save = (FancyButton) findViewById(R.id.save);
        clear = (FancyButton) findViewById(R.id.clear);

        Value = getIntent().getStringExtra("value");

        if(Value.equalsIgnoreCase("BPM"))
        {
            BPM_HOLDER.setVisibility(View.VISIBLE);
            Weight_HOLDER.setVisibility(View.GONE);
            Glucose_HOLDER.setVisibility(View.GONE);
            Thermometer_HOLDER.setVisibility(View.GONE);
        }
        else if(Value.equalsIgnoreCase("WM"))
        {
            BPM_HOLDER.setVisibility(View.GONE);
            Weight_HOLDER.setVisibility(View.VISIBLE);
            Glucose_HOLDER.setVisibility(View.GONE);
            Thermometer_HOLDER.setVisibility(View.GONE);
        }
        else if(Value.equalsIgnoreCase("GL"))
        {
            BPM_HOLDER.setVisibility(View.GONE);
            Weight_HOLDER.setVisibility(View.GONE);
            Glucose_HOLDER.setVisibility(View.VISIBLE);
            Thermometer_HOLDER.setVisibility(View.GONE);
        }
        else if(Value.equalsIgnoreCase("TM"))
        {
            BPM_HOLDER.setVisibility(View.GONE);
            Weight_HOLDER.setVisibility(View.GONE);
            Glucose_HOLDER.setVisibility(View.GONE);
            Thermometer_HOLDER.setVisibility(View.VISIBLE);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Value.equalsIgnoreCase("BPM"))
                {
                    String Flag_Txt =  flag.getText().toString();
                    String Systolic_High_Txt = Systolic_High.getText().toString();
                    String Systolic_Low_Txt = Systolic_Low.getText().toString();
                    String Diastolic_High_Txt =  Diastolic_High.getText().toString();
                    String Diastolic_Low_Txt = Diastolic_Low.getText().toString();
                    String Map_High_Txt = Map_High.getText().toString();
                    String Map_Low_Txt = Map_Low.getText().toString();
                    String Pulse_Rate_High_Txt = Pulse_Rate_High.getText().toString();
                    String Pulse_Rate_Low_Txt = Pulse_Rate_Low.getText().toString();
                    String User_ID_Txt = User_ID.getText().toString();
                    String Meas_Status_High_Txt = Meas_Status_High.getText().toString();
                    String Meas_Status_Low_Txt = Meas_Status_Low.getText().toString();

                    if(!Systolic_High_Txt.isEmpty())
                    {
                        post_BPM_Data("http://192.168.1.220:3000/route/F21_SLMS/setEhealthData",Flag_Txt,Systolic_High_Txt,Systolic_Low_Txt,Diastolic_High_Txt,Diastolic_Low_Txt,Map_High_Txt,Map_Low_Txt,Pulse_Rate_High_Txt,Pulse_Rate_Low_Txt,User_ID_Txt,Meas_Status_High_Txt,Meas_Status_Low_Txt);
                    }
                    else
                    {
                        Toast.makeText(BaseActivity.this, "Please wait till details are fetched!!", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(Value.equalsIgnoreCase("WM"))
                {
                    String Weight = weight.getText().toString();
                    if(!Weight.isEmpty())
                    {
                        post_WM_Data("http://192.168.1.220:3000/route/F21_SLMS/setEhealthData",Weight);
                    }
                    else
                    {
                        Toast.makeText(BaseActivity.this, "Please wait till details are fetched!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(Value.equalsIgnoreCase("GL"))
                {
                    String Glucose = glucose.getText().toString();
                    if(!Glucose.isEmpty())
                    {
                        post_GL_Data("http://192.168.1.220:3000/route/F21_SLMS/setEhealthData",Glucose);
                    }
                    else
                    {
                        Toast.makeText(BaseActivity.this, "Please wait till details are fetched!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(Value.equalsIgnoreCase("TM"))
                {
                    String CelciusTxt = Celcius.getText().toString();
                    String FarenheitTxt = Farenheit.getText().toString();

                    if(!CelciusTxt.isEmpty())
                    {
                        post_TM_Data("http://192.168.1.220:3000/route/F21_SLMS/setEhealthData",CelciusTxt,FarenheitTxt);
                    }
                    else
                    {
                        Toast.makeText(BaseActivity.this, "Please wait till details are fetched!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValues();
            }
        });

        BLEFragmentBase product_fragment = new BLEFragmentBase();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_holder, product_fragment);
        transaction.commit();
    }

    private void post_BPM_Data(String link,String Flag_Txt,String Systolic_High_Txt,String Systolic_Low_Txt,String Diastolic_High_Txt , String Diastolic_Low_Txt,String Map_High_Txt, String Map_Low_Txt,  String Pulse_Rate_High_Txt, String Pulse_Rate_Low_Txt , String User_ID_Txt , String Meas_Status_High_Txt , String Meas_Status_Low_Txt )
    {
        try {

            Log.i("JsonLink", link);

            JSONObject jsonParam = new JSONObject();

            try {

                jsonParam.put("rsid", SharedPrefUtil.getUser(BaseActivity.this).getData().getF21_SRNO());

                JSONObject jsonObject = new JSONObject();
                JSONObject BPM = new JSONObject();

                BPM.put("FLAG",Flag_Txt);
                BPM.put("SYSTOLICHIGH",Systolic_High_Txt);
                BPM.put("SYSTOLICLOW",Systolic_Low_Txt);
                BPM.put("DIASTOLICHIGH",Diastolic_High_Txt);
                BPM.put("DIASTOLICLOW",Diastolic_Low_Txt);
                BPM.put("MAPHIGH",Map_High_Txt);
                BPM.put("MAPLOW",Map_Low_Txt);
                BPM.put("PULSERATEHIGH",Pulse_Rate_High_Txt);
                BPM.put("PULSERATELOW",Pulse_Rate_Low_Txt);
                BPM.put("USERID",User_ID_Txt);
                BPM.put("MEASSTATUSHIGH",Meas_Status_High_Txt);
                BPM.put("MEASSTATUSLOW",Meas_Status_Low_Txt);

                jsonObject.put("BPM",BPM);
                jsonParam.put("EHEALTH",jsonObject);

                Log.d("json created", jsonParam.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(BaseActivity.this, link, jsonParam, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.d("RESPONSE*******", result.toString());
                    getEhealth_DataResponse(result);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void post_WM_Data(String link,String Weight)
    {
        try {

            Log.i("JsonLink", link);

            JSONObject jsonParam = new JSONObject();

            try {

                jsonParam.put("rsid", SharedPrefUtil.getUser(BaseActivity.this).getData().getF21_SRNO());

                JSONObject jsonObject = new JSONObject();
                JSONObject WM = new JSONObject();

                WM.put("WEIGHT",Weight);

                jsonObject.put("WM",WM);
                jsonParam.put("EHEALTH",jsonObject);

                Log.d("json created", jsonParam.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(BaseActivity.this, link, jsonParam, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.d("RESPONSE*******", result.toString());
                    getEhealth_DataResponse(result);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post_TM_Data(String link,String CELCIUS,String FARENHEIT)
    {
        try {

            Log.i("JsonLink", link);

            JSONObject jsonParam = new JSONObject();

            try {

                jsonParam.put("rsid", SharedPrefUtil.getUser(BaseActivity.this).getData().getF21_SRNO());

                JSONObject jsonObject = new JSONObject();
                JSONObject TM = new JSONObject();

                TM.put("CELCIUS",CELCIUS);
                TM.put("FARENHEIT",FARENHEIT);

                jsonObject.put("TM",TM);
                jsonParam.put("EHEALTH",jsonObject);

                Log.d("json created", jsonParam.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(BaseActivity.this, link, jsonParam, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.d("RESPONSE*******", result.toString());
                    getEhealth_DataResponse(result);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post_GL_Data(String link,String Glucose)
    {
        try {

            Log.i("JsonLink", link);

            JSONObject jsonParam = new JSONObject();

            try {

                jsonParam.put("rsid", SharedPrefUtil.getUser(BaseActivity.this).getData().getF21_SRNO());

                JSONObject jsonObject = new JSONObject();
                JSONObject GL = new JSONObject();

                GL.put("GLUCOSE",Glucose);
                GL.put("CHOLESTROL","");

                jsonObject.put("GL",GL);
                jsonParam.put("EHEALTH",jsonObject);

                Log.d("json created", jsonParam.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(BaseActivity.this, link, jsonParam, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    Log.d("RESPONSE*******", result.toString());
                    getEhealth_DataResponse(result);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEhealth_DataResponse(String Response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("SAVE"))
            {
                Toast.makeText(BaseActivity.this, "Saved!!!", Toast.LENGTH_SHORT).show();
                clearValues();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void clearValues()
    {
        flag.setText("");
        Systolic_High.setText("");
        Systolic_Low.setText("");
        Diastolic_High.setText("");
        Diastolic_Low.setText("");
        Map_High.setText("");
        Map_Low.setText("");
        Pulse_Rate_High.setText("");
        Pulse_Rate_Low.setText("");
        User_ID.setText("");
        Meas_Status_High.setText("");
        Meas_Status_Low.setText("");
        weight.setText("");
        glucose.setText("");
        time.setText("");
        Celcius.setText("");
        Farenheit.setText("");
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(Value.equalsIgnoreCase("BPM"))
        {
            IntentFilter filter = new IntentFilter("BloodPressure");
            registerReceiver(bpmReceiver, filter);
        }
        else if(Value.equalsIgnoreCase("WM"))
        {
            IntentFilter filter = new IntentFilter("WeightScale");
            registerReceiver(weightReceiver, filter);
        }
        else if(Value.equalsIgnoreCase("GL"))
        {
            IntentFilter filter = new IntentFilter("GlucoseScale");
            registerReceiver(glucoseReceiver, filter);
        }
        else if(Value.equalsIgnoreCase("TM"))
        {
            IntentFilter filter = new IntentFilter("Thermometer");
            registerReceiver(thermometerReceiver, filter);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(Value.equalsIgnoreCase("BPM"))
        {
            unregisterReceiver(this.bpmReceiver);
        }
        else if(Value.equalsIgnoreCase("WM"))
        {
            unregisterReceiver(this.weightReceiver);
        }
        else if(Value.equalsIgnoreCase("GL"))
        {
            unregisterReceiver(this.glucoseReceiver);
        }
        else if(Value.equalsIgnoreCase("TM"))
        {
            unregisterReceiver(this.thermometerReceiver);
        }
    }

    public class BPMReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            flag.setText(intent.getStringExtra("Flag"));
            Systolic_High.setText(intent.getStringExtra("Systolic_High"));
            Systolic_Low.setText(intent.getStringExtra("Systolic_Low"));
            Diastolic_High.setText(intent.getStringExtra("Diastolic_High"));
            Diastolic_Low.setText(intent.getStringExtra("Diastolic_Low"));
            Map_High.setText(intent.getStringExtra("Map_High"));
            Map_Low.setText(intent.getStringExtra("Map_Low"));
            Pulse_Rate_High.setText(intent.getStringExtra("Pulse_Rate_High"));
            Pulse_Rate_Low.setText(intent.getStringExtra("Pulse_Rate_Low"));
            User_ID.setText(intent.getStringExtra("User_ID"));
            Meas_Status_High.setText(intent.getStringExtra("Meas_Status_High"));
            Meas_Status_Low.setText(intent.getStringExtra("Meas_Status_Low"));

        }
    }


    public class WeightReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            weight.setText(intent.getStringExtra("Weight"));

        }
    }

    public class GlucoseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle dataBundle = intent.getBundleExtra("data");
            if (dataBundle != null) {
                double Glucose = dataBundle.getDouble(BluetoothLeService.GLUCOSE, -1.0d);
                double Cholestrol = dataBundle.getDouble(BluetoothLeService.CHOLESTROL, -1.0d);
                Date measurementDate = Calendar.getInstance().getTime();
                glucose.setText(String.format("%.2f", new Object[]{Double.valueOf(Glucose)}) + " mg/dL");
                time.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", measurementDate).toString());
            }

        }
    }

    public class ThermometerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Celcius.setText(intent.getStringExtra("Celcius"));
            Farenheit.setText(intent.getStringExtra("Farenheit"));

        }
    }
}
