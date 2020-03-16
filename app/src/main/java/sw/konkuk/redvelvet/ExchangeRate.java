package sw.konkuk.redvelvet;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.text.DecimalFormat;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.valueOf;

public class ExchangeRate extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange);
        TextView won = (TextView)findViewById(R.id.won);
        TextView text = (TextView)findViewById(R.id.rate);//
        DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
        switch (db.get_country()){
            case 0://일본
                break;
            case 1://유럽
                won.setText("€");
                break;
            case 2://중국
                won.setText("元");
                break;
            case 3://미국
                won.setText("$");
                break;
        }
        String rate = calculate(db.get_country());
        double drate = valueOf(rate);
        String rate2 = String.format("%.1f",drate);
        final SoundPool sound= new SoundPool(5,AudioManager.STREAM_MUSIC,0);
        text.setText(won.getText()+"1 =  약 "+rate2+"원");
        int country =0;
        switch (db.get_country()){
            case 0:
                country=R.raw.jap;
                break;
            case 1:
                country=R.raw.doi;
                break;
            case 2 :
                country=R.raw.chi;
                 break;
            case 3:
                country=R.raw.eng;
                break;
        }
        final int soundId = sound.load(this, country,1);
        Button btn = (Button)findViewById(R.id.expensive);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sound.play(soundId, 1f,1f,0,0,1f);
            }
        });
    }
    public void onClick(View v){
        try{
            EditText editText1 = (EditText) findViewById(R.id.before) ;
            TextView status1 = (TextView)findViewById(R.id.result);
            String before =editText1.getText().toString();
            DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
            String rate = calculate(db.get_country());
            double drate = valueOf(rate);
            String rate2 = String.format("%.1f",drate);
            double drate2 = valueOf(rate2);
            String result = String.format("%.0f",valueOf(before)*drate2);
            status1.setText(toNumFormat(Integer.parseInt(result))+" 원");}
        catch (Exception e){
            TextView status1 = (TextView)findViewById(R.id.result);
            status1.setText("얼마일까?");
            Toast.makeText(getApplicationContext(), "금액을 입력하세요!" ,Toast.LENGTH_SHORT).show();
        }
    }
    private RegistDialog2 dialog;
    public void registerProduct(View v) {
        // 상품 등록
        try{
            EditText editText1 = (EditText) findViewById(R.id.before) ;
            String before =editText1.getText().toString();
            DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
            String rate = calculate(db.get_country());
            double drate = valueOf(rate);
            String rate2 = String.format("%.1f",drate);
            double drate2 = valueOf(rate2);
            String result = String.format("%.0f",valueOf(before)*drate2);
            double temp = db.get_budget();
            double result2 = Double.parseDouble(result);
            if(temp < result2) {
                throw new Exception();}
            switch(v.getId()) {
                case R.id.regist:
                    dialog = new RegistDialog2(this, "[구매 하기]", toNumFormat(Integer.parseInt(result)),
                            leftListner, rightListner);

                    dialog.show();
                    break;
            }}
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "올바른 금액을 입력하세요" ,Toast.LENGTH_SHORT).show();
        }

    }
    private View.OnClickListener leftListner = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "상품이 등록되었습니다." ,Toast.LENGTH_SHORT).show();
            String sName = dialog.mEditName.getText().toString();
            String sPrice = dialog.mContentView.getText().toString();
            DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
            double drate = valueOf(calculate(db.get_country()));
            String rate2 = String.format("%.1f",drate);
            double drate2 = valueOf(rate2);
            double dPrice = valueOf(removeComma(sPrice));
            double iPrice = dPrice/drate2;
            String resultPrice = String.format("%.0f",iPrice);
            db.insertP(sName,resultPrice);

            double sum = db.get_sum(); // 지금까지 쓴 돈
            sum += dPrice; // 방금 산 물건 값을 더해주고요
            db.updateSUM(sum); // 디비를 업데이트 해줍니다..
            dialog.dismiss();
        }
    };

    private View.OnClickListener rightListner = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    };
    public String calculate(int num){
        StrictMode.enableDefaults();

        String result="입력해주세요";
        boolean initem = false,inName = false, inRate = false,inStatUpdateDatetime = false;
        String name = null, rate = null, statUpdateDatetime = null;
        String code="";
        switch (num){
            case 0://일본
                code="JPY";
                break;
            case 1://유럽
                code="EUR";
                break;
            case 2://중국
                code="CNY";
                break;
            case 3://미국
                code="USD";
                break;
        }
        try{
            URL url = new URL("https://api.manana.kr/exchange/rate/KRW/"+code+".xml"); //검색 URL부분

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);


            int parserEvent = parser.getEventType();
            System.out.println("파싱시작합니다.");

            while (parserEvent != XmlPullParser.END_DOCUMENT){

                switch(parserEvent){
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행

                        if(parser.getName().equals("name")){ //name 만나면 내용을 받을수 있게 하자
                            inName = true;
                        }
                        if(parser.getName().equals("rate")){ //rate 만나면 내용을 받을수 있게 하자
                            inRate = true;
                        }
                        if(parser.getName().equals("date")){ //mapy 만나면 내용을 받을수 있게 하자
                            inStatUpdateDatetime = true;
                        }
                        if(parser.getName().equals("message")){ //message 태그를 만나면 에러 출력
                            //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                        }
                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때


                        if(inName){ //isAddress이 true일 때 태그의 내용을 저장.
                            name = parser.getText();
                            inName = false;
                        }
                        if(inRate){ //isMapx이 true일 때 태그의 내용을 저장.
                            rate = parser.getText();
                            result = String.format("%.3f",  valueOf(rate));
                            inRate = false;
                        }
                        if(inStatUpdateDatetime){ //isMapy이 true일 때 태그의 내용을 저장.
                            statUpdateDatetime = parser.getText();
                            inStatUpdateDatetime = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item")){
                            //status1.setText(status1.getText() +"\n 국가: "+name+"\n 환율 : " + rate+
                            //"\n 갱신시각 : "+statUpdateDatetime+"\n");
                            initem = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
        }
        return result;
    }
    public String unit(int code){
        String result="";
        switch (code){
            case 0://일본
                result="￥";
                break;
            case 1://유럽
                result ="€";
                break;
            case 2://중국
                result="元";
                break;
            case 3://미국
                result="$";
                break;
        }
    return result;
    }

    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }
    public static String removeComma (String data){
        return data.replaceAll("\\,","");
    }

    public void home(View v) {
        // home
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void purchase(View v) {
        // 구매리스트
        Intent intent = new Intent(getApplicationContext(), purchaseList.class);
        startActivity(intent);
    }

    public void check(View v) {
        // 체크리스트
        Intent intent = new Intent(getApplicationContext(), checkList.class);
        startActivity(intent);
    }

}