package sw.konkuk.redvelvet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Double.valueOf;

public class MainActivity extends AppCompatActivity {

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DB 받아오기
//        TextView status1 = (TextView)findViewById(R.id.result);
        TextView title = (TextView)findViewById(R.id.title);
        TextView th = (TextView)findViewById(R.id.th);
        TextView date_ = (TextView)findViewById(R.id.date);
        final DBHelper dbHelper = new DBHelper(getApplicationContext(),"Trip.db",null,1);
        String country = "";
        switch(dbHelper.get_country()){
            case 0: country="일본"; break;
            case 1: country="유럽"; break;
            case 2: country="중국"; break;
            case 3: country="미국"; break;

        }
        title.setText(": "+dbHelper.get_title());//제목

        th.setText(today());//몇일째
        date_.setText(dbHelper.get_sdate()+" ~ "+dbHelper.get_edate());
//       status1.setText(dbHelper.get_key()+"번째,"+country+"여행 : "+dbHelper.get_title()+'\n'+"예산: "+(int)dbHelper.get_budget()+"원"+'\n'+"시작: "+dbHelper.get_sdate()+'\n'+"끝: "+dbHelper.get_edate());
        user = new User();


        TextView date_left = (TextView)findViewById(R.id.date_left);
        TextView budget_left = (TextView)findViewById(R.id.budget_left);
        TextView exchange = (TextView)findViewById(R.id.exchange);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);//오늘날짜
        String sdate = dbHelper.get_sdate();//시작날짜
        String edate = dbHelper.get_edate();//종료날짜
        try {
            Date today = sdf.parse(getTime);
            Date s = sdf.parse(sdate);
            Date e = sdf.parse(edate);

            long total = e.getTime()-s.getTime();//전체 일수
            long diff = e.getTime()-today.getTime();//남은날
            double per = (double)diff/(double)total*100;//퍼센트화
            ProgressBar progress = (ProgressBar) findViewById(R.id.date_progress); // 프로그레스 바 아이디 갖고와
            progress.setProgress((int)per); // 셋 프로그레스
            date_left.setText("남은 날: "+diff/ (24 * 60 * 60 * 1000)+"일");
        } catch (ParseException e) {}


        ProgressBar progress = (ProgressBar) findViewById(R.id.budget_progress); // 프로그레스 바 아이디 갖고와서
        double dif = dbHelper.get_budget()-dbHelper.get_sum();
        double percent = dbHelper.get_sum()/dbHelper.get_budget() * 100; // 퍼센트 계산
        progress.setProgress(100-(int)percent); // 셋 프로그레스
        ExchangeRate ex = new ExchangeRate();
        double rate = valueOf(ex.calculate(dbHelper.get_country()));//환율
        int result = (int)dif/(int)rate;
        double budget =valueOf(dbHelper.get_budget());
        budget_left.setText("남은 돈: "+ toNumFormat((int)dif)+"원 / "+toNumFormat((int)budget)+"원");
        exchange.setText(toNumFormat(result)+ex.unit(dbHelper.get_country()));
    }

    public String  today(){
        final DBHelper dbHelper = new DBHelper(getApplicationContext(),"Trip.db",null,1);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);//오늘날짜
        String sdate = dbHelper.get_sdate();//시작날짜
        try {
            Date today = sdf.parse(getTime);
            Date s = sdf.parse(sdate);

            long diff = today.getTime()-s.getTime();//남은날
            long result = diff/(24 * 60 * 60 * 1000)+1;
            if(result<1){
                return "D"+Long.toString(result+1);
            }
            else{
            return Long.toString(result);}
        } catch (ParseException e) {}
        return"오류";
    }
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
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
    public void exchange(View v) {
        // 환전
        Intent intent = new Intent(getApplicationContext(), ExchangeRate.class);
        startActivity(intent);
    }

    public void showMyList(View v) {
        // 여행 목록 보기
        Intent intent = new Intent(getApplicationContext(), TripListActivity.class);
        startActivity(intent);
    }
}
