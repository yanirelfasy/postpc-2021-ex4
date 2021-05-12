package exercise.find.roots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        TextView originalNumberText = findViewById(R.id.original_number);
        TextView root1Text = findViewById(R.id.root1_number);
        TextView root2Text = findViewById(R.id.root2_number);

        Intent incoming = getIntent();
        long originalNumber = incoming.getLongExtra("original_number", 0);
        long root1 = incoming.getLongExtra("root1", 0);
        long root2 = incoming.getLongExtra("root2", 0);

        originalNumberText.setText(String.valueOf(originalNumber));
        root1Text.setText(String.valueOf(root1));
        root2Text.setText(String.valueOf(root2));
    }
}