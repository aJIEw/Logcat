package kale.debug.logcat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import kale.debug.log.ui.LogActivity;
import kale.debug.logcat.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        printLog();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LogActivity.class));
            }
        });
    }

    private void printLog() {
        Log.v(TAG, "Verbose log.");
        Log.d(TAG, "Debug log");
        Log.i(TAG, "Info log");
        Log.w(TAG, "Warn log", new RuntimeException("Handshake"));
        Log.e(TAG, "Large Data:"
                + "\nNever give up,"
                + "\nNever lose hope."
                + "\nAlways have faith,"
                + "\nIt allows you to cope."
                + "\nTrying times will pass,"
                + "\nAs they always do."
                + "\nJust have patience,"
                + "\nYour dreams will come true."
                + "\nSo put on a smile,"
                + "\nYou'll live through your pain."
                + "\nKnow it will pass,"
                + "\nAnd strength you will gain.");
    }

}
