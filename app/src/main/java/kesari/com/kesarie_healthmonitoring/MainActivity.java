package kesari.com.kesarie_healthmonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import kesari.com.kesarie_healthmonitoring.PulseOximeter.PulseOximeterActivity;
import kesari.com.kesarie_healthmonitoring.Utils.SharedPrefUtil;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {

    FancyButton pulseOximeter,bloodPressure,weightScale,glucoMeter,Thermometer;
    TextView Name,Dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pulseOximeter = (FancyButton) findViewById(R.id.pulseOximeter);
        bloodPressure = (FancyButton) findViewById(R.id.bloodPressure);
        weightScale = (FancyButton) findViewById(R.id.weightScale);
        glucoMeter = (FancyButton) findViewById(R.id.glucoMeter);
        Thermometer = (FancyButton) findViewById(R.id.Thermometer);

        Name = (TextView) findViewById(R.id.Name);
        Dept = (TextView) findViewById(R.id.Dept);

        Name.setText(SharedPrefUtil.getUser(MainActivity.this).getData().getF21_NAME());
        Dept.setText(SharedPrefUtil.getUser(MainActivity.this).getData().getF21_DEPT());

        pulseOximeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PulseOximeterActivity.class);
                startActivity(intent);
            }
        });

        bloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseActivity.class);
                intent.putExtra("value","BPM");
                startActivity(intent);
            }
        });

        weightScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseActivity.class);
                intent.putExtra("value","WM");
                startActivity(intent);
            }
        });

        glucoMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseActivity.class);
                intent.putExtra("value","GL");
                startActivity(intent);
            }
        });

        Thermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BaseActivity.class);
                intent.putExtra("value","TM");
                startActivity(intent);
            }
        });
    }
}
