package kesari.com.kesarie_healthmonitoring.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import kesari.com.kesarie_healthmonitoring.MainActivity;
import kesari.com.kesarie_healthmonitoring.R;
import kesari.com.kesarie_healthmonitoring.Utils.FireToast;
import kesari.com.kesarie_healthmonitoring.Utils.IOUtils;
import kesari.com.kesarie_healthmonitoring.Utils.SharedPrefUtil;
import mehdi.sakout.fancybuttons.FancyButton;

public class CheckProfileActivity extends AppCompatActivity {

    EditText rsID;
    FancyButton proceed;
    Gson gson;
    ProfileMain profileMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_profile);

        rsID = (EditText) findViewById(R.id.rsID);
        proceed = (FancyButton) findViewById(R.id.proceed);

        gson = new Gson();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String RSID = rsID.getText().toString().trim();

                if(!RSID.isEmpty())
                {
                    checkGuest(RSID);
                }
                else
                {
                    FireToast.customDefiniteSnackbar(CheckProfileActivity.this,"Enter RSID","");
                }
            }
        });

    }

    public void checkGuest(String rsID)
    {
        String url = "http://192.168.1.220:3000/route/F21_SLMS/getEhealthData/" + rsID;

        IOUtils ioUtils = new IOUtils();

        ioUtils.getGETStringRequest(CheckProfileActivity.this,url, new IOUtils.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                getGuestResponse(result);
            }
        }, new IOUtils.VolleyFailureCallback() {
            @Override
            public void onFailure(String result) {

            }
        });
    }

    public void getGuestResponse(String response)
    {
        profileMain = gson.fromJson(response, ProfileMain.class);

        if(profileMain.getData() != null)
        {
            SharedPrefUtil.setUser(CheckProfileActivity.this,response);
            Intent intent = new Intent(CheckProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            FireToast.customDefiniteSnackbar(CheckProfileActivity.this,"Enter Proper RSID!!","");
        }
    }
}
