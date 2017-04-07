package everytasc.nineleaps.com.hackaton.Activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import everytasc.nineleaps.com.hackaton.R;

public class StartActivity extends AppCompatActivity {

    Activity activity;
    Button startNow;
    TextView tagLine, introText;
    CircularImageView propic;
    TextInputLayout nameLayout, ageLayout, locationLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity=this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.black));
        }

        nameLayout = (TextInputLayout)findViewById(R.id.input_layout_name);
        ageLayout = (TextInputLayout)findViewById(R.id.input_layout_age);
        locationLayout = (TextInputLayout)findViewById(R.id.input_layout_location);
        propic = (CircularImageView)findViewById(R.id.profilePicContact);
        tagLine = (TextView)findViewById(R.id.tagLine);
        introText = (TextView)findViewById(R.id.introText);

        startNow=(Button)findViewById(R.id.startNow);
        startNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick: ", String.valueOf(startNow.getText()));
                if ("Start Now!".equals(startNow.getText().toString())){
                    setEditTextView();

                }else {
                    Intent i = new Intent(StartActivity.this,MainActivity.class);
                    startActivity(i);
                }


            }
        });



    }

    private void setEditTextView() {
        tagLine.animate().alpha(0.0f).setDuration(1000);
        startNow.animate().alpha(0.0f).setDuration(1000);

        introText.animate().alpha(0.0f)
                .setDuration(1000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startNow.setText("Lets go");


                        nameLayout.setAlpha(0f);
                        nameLayout.setVisibility(View.VISIBLE);


                        nameLayout.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setListener(null);

                        ageLayout.setAlpha(0f);
                        ageLayout.setVisibility(View.VISIBLE);


                        ageLayout.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setListener(null);

                        locationLayout.setAlpha(0f);
                        locationLayout.setVisibility(View.VISIBLE);


                        locationLayout.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setListener(null);

                        startNow.animate()
                                .alpha(1f)
                                .setDuration(1000)
                                .setListener(null);


                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

}
