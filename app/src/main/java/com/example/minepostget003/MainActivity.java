
package com.example.minepostget003;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    //POST送信用の設定
    private TextView textView;
    private EditText editText,addressText;
    private Button post;
    //String sendData;
    RequestBody formBody;
    JSONObject json;
    String post_URL="http://windy-saiki-0663.greater.jp/pass_check.php";

    //GET受信用の設定
    Button get_btn;
    ListView listView;

    String urlHTML="http://windy-saiki-0663.greater.jp/pass_check.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //POST送信用の設定
        editText=findViewById(R.id.uriname);
        addressText=findViewById(R.id.text_address);
        post=findViewById(R.id.post);

        post.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //TextEditから得られたnameとaddressをJSON化する
                String name=editText.getText().toString();
                String address=addressText.getText().toString();

                // create json
                json =new  JSONObject();
                try {
                    json.put("name",name);
                    json.put("address", address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                postTest();

                 }
        });

        //GET受信用の設定
        get_btn=findViewById(R.id.get_button);
        listView=findViewById(R.id.listView);


        get_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //httpリクエスト
                try{
                    //okhttpを利用する
                    httpRequest("http://windy-saiki-0663.greater.jp/mine_sample.json");
                }catch(Exception e){
                    Log.d("Exception",e.getMessage());
                }
            }
        });

    }

    //◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇
    //POST用のメソッド
    private void postTest() {

        OkHttpClient client = new OkHttpClient.Builder().build();
        RequestBody postBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request request  = new Request.Builder().url(post_URL).post(postBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                runOnUiThread(new Runnable() {
                    public void run() {
                       Log.d("RESPONSE",res);
                    }
                });
            }
        });

    }


    //◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇◆◇
    //GET用のメソッド
    private void httpRequest(String url) throws Exception {

        ArrayList<String> list=new ArrayList<String>();

        //OKHTTPClient生成
        OkHttpClient client=new OkHttpClient();

        //request生成
        Request request=new Request.Builder().url(url).build();

        //非同期リクエスト
        client.newCall(request).enqueue(new Callback(){

            //正常の時
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                //responseの取り出し
                final String jsonStr=response.body().string();
                Log.d("Response=","jsonStr"+jsonStr);

                //JSON処理
                try{
                    JSONArray array=new JSONArray(jsonStr);

                    for(int i=0;i<array.length();i++){
                        JSONObject json=array.getJSONObject(i);

                        Log.d("NAME",json.getString("name"));

                        list.add(json.getString("name"));
                    }


                    //親スレッドの更新
                    Handler mainHandler=new Handler(Looper.getMainLooper());

                    mainHandler.post(new Runnable(){
                        public void run(){
                            ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,list);

                            listView.setAdapter(arrayAdapter);
                        }

                    });
                }catch(Exception e){
                    Log.d("ArryFailure",e.getMessage());
                }
            }

            //エラーの時
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Failure",e.getMessage());
            }
        });
    }


}