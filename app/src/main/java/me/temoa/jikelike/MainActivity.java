package me.temoa.jikelike;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LikeView likeView = (LikeView) findViewById(R.id.like);
        likeView.setNumber(398);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeView.startAnim();
            }
        });

        final EditText editText = (EditText) findViewById(R.id.et);
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.parseInt(editText.getText().toString());
                likeView.setNumber(number);
            }
        });
    }
}
