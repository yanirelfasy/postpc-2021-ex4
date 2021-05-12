package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForCancel = null;
  private boolean isBtnEnabled = true;
  private boolean isTextEnabled = true;
  private int progressVisibility = View.GONE;
  protected String textInput = "";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ProgressBar progressBar = findViewById(R.id.progressBar);
    EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
    Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      public void onTextChanged(CharSequence s, int start, int before, int count) { }
      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        try {
          Long.parseLong(newText);
          textInput = newText;
          isBtnEnabled = !newText.isEmpty();
          buttonCalculateRoots.setEnabled(isBtnEnabled);
        }
        catch(Exception e){
          textInput = newText;
          isBtnEnabled = false;
          buttonCalculateRoots.setEnabled(isBtnEnabled);
        }
      }
    });

    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();
      long userInputLong = 0;
      try{
        userInputLong = Long.parseLong(userInputString);
        intentToOpenService.putExtra("number_for_service", userInputLong);
        startService(intentToOpenService);
        isBtnEnabled = false;
        isTextEnabled = false;
        progressVisibility = View.VISIBLE;
        buttonCalculateRoots.setEnabled(isBtnEnabled);
        editTextUserInput.setEnabled(isTextEnabled);
        progressBar.setVisibility(progressVisibility);

      }
      catch(Exception e){
        Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
      }
    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")) return;
        isBtnEnabled = true;
        isTextEnabled = true;
        progressVisibility = View.GONE;
        progressBar.setVisibility(progressVisibility);
        buttonCalculateRoots.setEnabled(isBtnEnabled);
        editTextUserInput.setEnabled(isTextEnabled);
        long originalNumber = incomingIntent.getLongExtra("original_number", 0);
        long root1 = incomingIntent.getLongExtra("root1", 0);
        long root2 = incomingIntent.getLongExtra("root2", 0);
        Intent intent = new Intent(context, SuccessActivity.class);
        intent.putExtra("original_number", originalNumber);
        intent.putExtra("root1", root1);
        intent.putExtra("root2", root2);
        context.startActivity(intent);
      }
    };
    registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));

    broadcastReceiverForCancel = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations")) return;
        isBtnEnabled = true;
        isTextEnabled = true;
        progressVisibility = View.GONE;
        progressBar.setVisibility(progressVisibility);
        buttonCalculateRoots.setEnabled(isBtnEnabled);
        editTextUserInput.setEnabled(isTextEnabled);
        int timeStopped = incomingIntent.getIntExtra("time_until_give_up_seconds", 0);
        String message = "calculation aborted after " + timeStopped + " seconds";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
      }
    };
    registerReceiver(broadcastReceiverForCancel, new IntentFilter("stopped_calculations"));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.unregisterReceiver(broadcastReceiverForSuccess);
    this.unregisterReceiver(broadcastReceiverForCancel);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("isBtnEnabled", isBtnEnabled);
    outState.putBoolean("isTextEnabled", isTextEnabled);
    outState.putInt("progressVisibility", progressVisibility);
    outState.putString("textInput", textInput);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    ProgressBar progressBar = findViewById(R.id.progressBar);
    EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
    Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    isBtnEnabled = savedInstanceState.getBoolean("isBtnEnabled");
    isTextEnabled = savedInstanceState.getBoolean("isTextEnabled");
    progressVisibility = savedInstanceState.getInt("progressVisibility");
    textInput = savedInstanceState.getString("textInput");

    progressBar.setVisibility(progressVisibility);
    buttonCalculateRoots.setEnabled(isBtnEnabled);
    editTextUserInput.setEnabled(isTextEnabled);
    editTextUserInput.setText(textInput);
  }
}

