package everytasc.nineleaps.com.hackaton.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import everytasc.nineleaps.com.hackaton.DataModel.Option;
import everytasc.nineleaps.com.hackaton.R;
import everytasc.nineleaps.com.hackaton.Utility.Constans;
import everytasc.nineleaps.com.hackaton.Utility.VolleySingelton;
import everytasc.nineleaps.com.hackaton.Utility.VolleySingeltonUniv;

public class MainActivity extends AppCompatActivity implements VolleySingelton.APIStringResponseListener,View.OnClickListener{

    LinearLayout chatLayout, answerLayout;
    ScrollView scrollView;
    final String API_URL = "http://192.168.43.15/getQuestion.php";
    final String FAIL_URL="http://192.168.43.15/navi/pages/fail/fail.html";
    final String SORRY_TEXT="Sorry, I don't have any knowledge about it for now.. Please choose another..";
    int chatViewIndex=-1;
    boolean questionReAsk=false;
    ArrayList<View> chatBlocks = new ArrayList<>();
    View presentQuestionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chatLayout = (LinearLayout)findViewById(R.id.messageFromServer);
        answerLayout = (LinearLayout)findViewById(R.id.answerOptions);

        scrollView=(ScrollView)findViewById(R.id.scrollView);

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

        //getOrderInfoFromVolley();
        makeACall(1);
    }

    @Override
    public void onClick(View v) {
        removeChatBlock(v);

    }

    private void removeChatBlock(View v){
        Log.v("TAG", "Index: " + String.valueOf(v.getTag(R.string.tag_index).toString()));
        int clickIndex = Integer.parseInt(v.getTag(R.string.tag_index).toString()) ;
        int questionCode  =Integer.parseInt(v.getTag(R.string.tag_question_code).toString()) ;

        /*for(int i=clickIndex;i<chatViewIndex;i++){
            Log.i("ABC", "lajfla");
            //chatLayout.get
            chatLayout.removeViewAt(clickIndex);
        }*/

        Log.w("ChatViewIndex",String.valueOf(chatViewIndex));
        for(int i=chatViewIndex;i>=clickIndex;i--){
            Log.i("ABC", String.valueOf(i));
            chatLayout.removeViewAt(i);
        }
        chatViewIndex=clickIndex-1;
        makeACall(questionCode);
    }

    private void displayQA(String code,String question, String[] possibleAns){
        addMessageFromServer(code, question);
        for (int i = 0; i < possibleAns.length; i++) {
            Option ansObject = new Option(possibleAns[i]);
            showAnswerOptions(ansObject);
        }

        /*for (int i = 0; i < answers.length; i++) {
            showAnswerOptions(answers[i]);
        }*/
    }

    private void showAnswerOptions(final Option answer) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View block = layoutInflater.inflate(R.layout.answer_block, null);
        ImageView helpView = (ImageView) block.findViewById(R.id.iv_help);

        if(answer.helpButton==false){
            helpView.setVisibility(View.INVISIBLE);
        }

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(answer.getCode()!=0){ //END of It..
                    addAnswerToView(answer.getText());
                    makeACall(answer.getCode());
                }else{
                    Intent a = new Intent(MainActivity.this,AnswerDetailsActivity.class);
                    String endUrl = answer.getAllData().replace(":","-");
                    Log.i("ALL",endUrl);
                    a.putExtra("url","http://192.168.43.15/navi/pages/"+ endUrl+"/"+endUrl+".html");
                    startActivity(a);
                }

            }
        });

        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnswerDetailsActivity.class);
                String endUrl = answer.getAllData().replace(":","-");
                Log.i("ALL",endUrl);
                intent.putExtra("url", "http://192.168.43.15/navi/pages/" + endUrl + "/" + endUrl + ".html");
                startActivity(intent);
            }
        });


        TextView answerOptionsText= (TextView)block.findViewById(R.id.optionsFromServer);
        answerOptionsText.setText(answer.getText());

        CircularImageView wiki=(CircularImageView)findViewById(R.id.iv_help);
        answerLayout.addView(block);
    }

    private void addToChatView(View block){
        chatViewIndex++;
        block.setTag(R.string.tag_index, String.valueOf(chatViewIndex));
        chatLayout.addView(block);
        //chatBlocks.add(block);
    }




    private void addAnswerToView(String answer) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View block = layoutInflater.inflate(R.layout.chat_answer_block, null);
        LinearLayout parentLayout=(LinearLayout)block.findViewById(R.id.parentLayout);
        parentLayout.setGravity(Gravity.END);
        TextView answerText= (TextView)block.findViewById(R.id.answer);
        answerText.setText(answer);
        addToChatView(block);
        //chatLayout.addView(block);
    }

    private void addMessageFromServer(String code, String questionText) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View block = layoutInflater.inflate(R.layout.chat_block, null);

        TextView question=(TextView)block.findViewById(R.id.question);

        if(questionReAsk==false){
            question.setText(questionText);
        }else{
            question.setText(SORRY_TEXT + questionText);
        }

        addToChatView(block);

        //TODO: change to real question Index
        block.setTag(R.string.tag_question_code,code);
        block.setOnClickListener(this);
        presentQuestionView=block;
        //chatLayout.addView(block);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //-----------------------------------

    @Override
    public void onSuccessStringResponse(String responseString) {
        Log.i("API_RESPONSE", responseString);
        JSONObject responseObj=null;
        String code="",question="",options="";
        String response="";
        int xyz=0;

        try {
            responseObj = new JSONObject(responseString);
            response = responseObj.getString("response");

            if(response.equals("success")){
                xyz=1;
                code = responseObj.getString("code");
                question= responseObj.getString("question");
                options= responseObj.getString("options");
            }else if(response.equals("fail")){
                xyz=2;
            }else{

            }


        } catch (JSONException e) {
            Log.e("BURN_ERROR",String.valueOf(e));
        }

        if(xyz==1){
            Log.v("CODE", code);
            Log.v("QUESTION",question);
            Log.v("OPTIONS",options);
            String[] ansOptions = options.split(",");
            displayQA(code,question,ansOptions);
            questionReAsk=false;
        }else if(xyz==2){
            /*Intent intent = new Intent(this,AnswerDetailsActivity.class);
            intent.putExtra("url",FAIL_URL);
            startActivity(intent);*/
            questionReAsk=true;
            removeChatBlock(presentQuestionView);
        }

    }

    @Override
    public void onErrorStringResponse(VolleyError volleyError) {

    }


    //--------------

    private void makeACall(int code) {

        //messageFromServer.removeAllViews();
        answerLayout.removeAllViews();
        JSONObject jsonProfileObject=new JSONObject();
        try {
            jsonProfileObject.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //String urlString = "http://192.168.43.15/getQuestion.php";
        Log.d("urlstring", API_URL);
        VolleySingelton.getInstance().setStringRequestListener4(Request.Method.POST, jsonProfileObject, API_URL, this);
    }
}


