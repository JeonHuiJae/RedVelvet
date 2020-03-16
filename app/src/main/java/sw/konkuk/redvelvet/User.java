package sw.konkuk.redvelvet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Vector;


/**
 * Created by sw-48 on 2017-11-06.
 */

public class User extends Activity {
    int year, month, day, flag;
    String country;
    int[] tempStart, tempEnd;
    int tempCode;
    double tempMoney;
    Vector<Trip> trips; // 여행 배열

    public User() {
        this.trips = new Vector();
        tempStart = new int[3];
        tempEnd = new int[3];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        //<--------------국가 입력------------------->
        Spinner s = (Spinner)findViewById(R.id.contry);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                country = (String)parent.getItemAtPosition(position); //국가명 입력받음

                if(country.equals("일본")) {
                    tempCode = 0;
                }
                else if(country.equals("유럽")) {
                    tempCode = 1;
                }
                else if(country.equals("중국")) {
                    tempCode = 2;
                }
                else if(country.equals("미국")) {
                    tempCode = 3;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //<-------------날짜 입력--------------->
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        int sum = year+month+day;
        findViewById(R.id.date_pick1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag=1;
                new DatePickerDialog(User.this, dateSetListener, year, month, day).show();
            }
        });
        findViewById(R.id.date_pick2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag=2;
                new DatePickerDialog(User.this, dateSetListener, year, month, day).show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            GregorianCalendar calendar = new GregorianCalendar();
//            if(year*10000+monthOfYear*100+dayOfMonth<(calendar.get(Calendar.YEAR)*10000+calendar.get(Calendar.MONTH)*100+calendar.get(Calendar.DAY_OF_MONTH))){
//                year = calendar.get(Calendar.YEAR);monthOfYear=calendar.get(Calendar.MONTH);dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
//                Toast.makeText(User.this, "날짜선택은 오늘부터 가능합니다", Toast.LENGTH_SHORT).show();
//            }
            if(flag==1){//날짜 예외처리
                if(tempStart[0]!=0){
                    if(year*10000+monthOfYear*100+dayOfMonth>(tempEnd[0]*10000+tempEnd[1]*100+tempEnd[2])){
                        Toast.makeText(User.this, "입국일 이전으로 선택해주세요", Toast.LENGTH_SHORT).show();
                        year = tempEnd[0];monthOfYear = tempEnd[1];dayOfMonth = tempEnd[2];
                    }
                }
            }
            if(flag==2){
                if(tempStart[0]!=0){
                    if(year*10000+monthOfYear*100+dayOfMonth<(tempStart[0]*10000+tempStart[1]*100+tempStart[2])){
                        Toast.makeText(User.this, "출국일 이후로 선택해주세요", Toast.LENGTH_SHORT).show();
                        year = tempStart[0];monthOfYear = tempStart[1];dayOfMonth = tempStart[2];
                    }
                }
            }
            String msg = String.format("%d / %d / %d", year,monthOfYear+1, dayOfMonth); //년, 월, 일 '/'로 구분해놓음. 출국 입국 각각 년, 월, 일 따로 저장하는게 좋을듯함

            if(flag==1){ //flag 가 1 이면 출국일
                TextView status1 = (TextView)findViewById(R.id.date1);
                status1.setText(msg);

                tempStart[0] = year;
                tempStart[1] = monthOfYear;
                tempStart[2] = dayOfMonth;

            }
            else{ //2 이면 입국일
                TextView status2 = (TextView)findViewById(R.id.date2);
                status2.setText(msg);

                tempEnd[0] = year;
                tempEnd[1] = monthOfYear;
                tempEnd[2] = dayOfMonth;
            }
        }
    };

    public void save_user_info(View v) {
        try{
            if(tempStart[0] == 0||tempEnd[0] == 0)
            {throw new Exception();}
        EditText budget = (EditText) findViewById(R.id.budget) ; //예산 입력받은것
        String str = budget.getText().toString();
        tempMoney = Double.parseDouble(str);
        EditText title = (EditText) findViewById(R.id.title) ;
        String tempTitle= title.getText().toString();
        // 메인으로
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        //db저장
        DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
            db.insertT(tempTitle, tempCode,tempMoney, tempStart, tempEnd);
        this.trips.add(new Trip(tempTitle, tempStart, tempEnd, tempMoney, tempCode)); // 객체 생성해서 넣음
        Toast.makeText(User.this, "여행정보 등록완료!", Toast.LENGTH_SHORT).show();}
        catch (Exception e){
            Toast.makeText(User.this, "모두 입력해주세요!", Toast.LENGTH_SHORT).show();
        }
    }

}