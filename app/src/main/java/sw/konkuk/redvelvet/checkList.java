package sw.konkuk.redvelvet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Double.valueOf;

/**
 * Created by hizz on 2017-11-02.
 */

public class checkList extends AppCompatActivity {

    Vector<Product> list; // 체크리스트 상품 목록

    private ListView m_nameList;
    private ListView m_priceList;
    private ArrayAdapter<String> m_Adapter1;
    private ArrayAdapter<String> m_Adapter2;

    ArrayList<String> nameArray;
    ArrayList<String> priceArray;
    ArrayList<Product> productArray;


    private RegistDialog dialog;

    public checkList() {
        this.list = new Vector();
    }

    public void registerProduct(View v) {
        // 상품 등록
        switch(v.getId()) {
            case R.id.regist:
                dialog = new RegistDialog(this, "[상품 등록]", "상품 정보를 입력하세요.",
                        leftListner, rightListner);
                ExchangeRate ex = new ExchangeRate();
                DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
                String unit = ex.unit(db.get_country());
                dialog.show();
                dialog.mUnit.setText(unit);
                break;
        }

    }

    private View.OnClickListener leftListner = new View.OnClickListener() {
        public void onClick(View v) {
            try{
                DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
            String sName = dialog.mEditName.getText().toString();
            String sPrice = dialog.mEditPrice.getText().toString();
//            double dPrice = Double.parseDouble(sPrice);
//            ExchangeRate ex = new ExchangeRate();
//            double rate = Double.parseDouble(ex.calculate(db.get_country()));
//            double ddPrice = dPrice*rate;
//            String ssPrice = Integer.toString((int)ddPrice);
            db.insertC(sName, sPrice);//데이터베이스 추가

            nameArray.add(sName);
            priceArray.add(toNumFormat(Integer.parseInt(sPrice)));
            //productArray.add(product);

            // 갱신되었음을 어댑터에 통보
            m_Adapter1.notifyDataSetChanged();
            m_Adapter2.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "상품이 등록되었습니다.",Toast.LENGTH_SHORT).show();
                // 합계 금액 출력
                TextView sumText = (TextView) findViewById(R.id.text_sum);
                ExchangeRate e = new ExchangeRate();
                String temp = e.calculate(db.get_country());
                double dTemp = Double.parseDouble(temp);
                int won = (int) db.getWishSum() * (int) dTemp;

                String str = "";
                switch(db.get_country()) {
                    case 0:
                        str = "￥";
                        break;
                    case 1:
                        str = "€";
                        break;
                    case 2:
                        str = "元";
                        break;
                    case 3:
                        str = "＄";
                        break;
                }
                sumText.setText("\n합계 금액 : " + (int) db.getWishSum()+str+ " ("+won+"￦)\t");
            dialog.dismiss();}
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "잘못된 값입니다" ,Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener rightListner = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        nameArray = new ArrayList<String>();
        priceArray = new ArrayList<String>();
        productArray = new ArrayList<Product>();

        // Android에서 제공하는 String 문자열 하나를 출력 가능한 layout으로 어댑터 생성
        m_Adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, nameArray);
        m_Adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_single_choice, priceArray);


        // xml에서 추가한 ListView 연결
        m_nameList = (ListView) findViewById(R.id.productNameList);
        m_priceList = (ListView) findViewById(R.id.productPriceList);
        m_priceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // ListView에 어댑터 연결
        m_nameList.setAdapter(m_Adapter1);
        m_priceList.setAdapter(m_Adapter2);

        // ListView 아이템 터치 시 이벤트 추가
        m_nameList.setOnItemClickListener(onClickListItem);


        DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
        String[] price = db.get_C_price();
        String[] item = db.get_C_item();
        ExchangeRate ex = new ExchangeRate();
        String unit = ex.unit(db.get_country());
        TextView unitt = (TextView)findViewById(R.id.unit);
        unitt.setText(unit);//화폐단위 맞춰주기
        for(int i=0;i<item.length;i++){
            m_Adapter1.add(item[i]);
            m_Adapter2.add(toNumFormat(Integer.parseInt(price[i])));
        }
        // ListView에 아이템 추가
        // 합계 금액 출력
        TextView sumText = (TextView) findViewById(R.id.text_sum);
        ExchangeRate e = new ExchangeRate();
        String temp = e.calculate(db.get_country());
        double dTemp = Double.parseDouble(temp);
        String stemp =String.format("%.1f",dTemp);
        double drate2 = valueOf(stemp);
        double won = (int) db.getWishSum() *drate2 ;

        String str = "";
        switch(db.get_country()) {
            case 0:
                str = "￥";
                break;
            case 1:
                str = "€";
                break;
            case 2:
                str = "元";
                break;
            case 3:
                str = "＄";
                break;
        }
        sumText.setText("\n합계 금액 : " + (int) db.getWishSum()+str+ " ("+(int)won+"￦)\t");
    }

    public void buyProduct(View v) {

        int position = m_priceList.getCheckedItemPosition();
        if(position >= 0 && position < priceArray.size()) {
        try {
            Toast.makeText(getApplicationContext(), m_Adapter1.getItem(position) + " 구매", Toast.LENGTH_SHORT).show();

            DBHelper db = new DBHelper(getApplicationContext(), "Trip.db", null, 1);

            double money = Double.parseDouble(removeComma(m_Adapter2.getItem(position))); // 지금 산 물건 가격
            double budget = db.get_budget();
            ExchangeRate ex = new ExchangeRate();
            double rate = Double.parseDouble(ex.calculate(db.get_country()));
            double ddPrice = money*rate;//환율계산
            String ssPrice = Integer.toString((int)ddPrice);
            double sum = db.get_sum(); // 지금까지 쓴 돈
            if (budget <ddPrice) {
                throw new Exception();
            }
            sum += ddPrice; // 방금 산 물건 값을 더해주고요
            db.updateSUM(sum); // 디비를 업데이트 해줍니다..

            db.insertP(m_Adapter1.getItem(position), removeComma(m_Adapter2.getItem(position)));//구매리스트에 추가
            db.deleteC(m_Adapter1.getItem(position));//체크리스트에서 삭제

            String data = nameArray.get(position);
            nameArray.remove(position);
            priceArray.remove(position);
            m_Adapter1.notifyDataSetChanged();
            m_Adapter2.notifyDataSetChanged();
        }
        catch(Exception e){
            Toast.makeText(checkList.this, "구매할 수 없습니다!", Toast.LENGTH_SHORT).show();
        }}else{
            Toast.makeText(checkList.this, "선택해 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteProduct(View v) {


        int pos = m_priceList.getCheckedItemPosition();
        if(pos >= 0 && pos < priceArray.size()) {
            DBHelper db = new DBHelper(getApplicationContext(), "Trip.db", null, 1);
            // 합계 금액 출력
            TextView sumText = (TextView) findViewById(R.id.text_sum);
            ExchangeRate e = new ExchangeRate();
            String temp = e.calculate(db.get_country());
            double dTemp = Double.parseDouble(temp);
            int won = (int) db.getWishSum() * (int) dTemp;

            String str = "";
            switch(db.get_country()) {
                case 0:
                    str = "￥";
                    break;
                case 1:
                    str = "€";
                    break;
                case 2:
                    str = "元";
                    break;
                case 3:
                    str = "＄";
                    break;
            }
            sumText.setText("\n합계 금액 : " + (int) db.getWishSum()+str+ " ("+won+"￦)\t");



            // DB에서도 삭제링
            db.deleteC(m_Adapter1.getItem(pos));//체크리스트에서 삭제
            Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
            nameArray.remove(pos);
            priceArray.remove(pos);
            m_Adapter1.notifyDataSetChanged();
            m_Adapter2.notifyDataSetChanged();
            // listView 선택 초기화
            m_priceList.clearChoices();
            m_nameList.clearChoices();
        }
        else{
            Toast.makeText(getApplicationContext(), "선택해 주세요", Toast.LENGTH_SHORT).show();
        }

    }


    // 아이템 터치 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
            // 이벤트 발생 시 해당 아이템 위치의 텍스트를 출력
            try {
                Toast.makeText(getApplicationContext(), m_Adapter1.getItem(position) + " 구매", Toast.LENGTH_SHORT).show();

                DBHelper db = new DBHelper(getApplicationContext(), "Trip.db", null, 1);

                double money = Double.parseDouble(m_Adapter2.getItem(position)); // 지금 산 물건 가격
                double budget = db.get_budget();
                double sum = db.get_sum(); // 지금까지 쓴 돈
                 if (budget < money) {
                throw new Exception();
                }
            sum += money; // 방금 산 물건 값을 더해주고요
            db.updateSUM(sum); // 디비를 업데이트 해줍니다..

            db.insertP(m_Adapter1.getItem(position), m_Adapter2.getItem(position));//구매리스트에 추가
            db.deleteC(m_Adapter1.getItem(position));//체크리스트에서 삭제

            String data = nameArray.get(position);
            nameArray.remove(position);
            priceArray.remove(position);
            m_Adapter1.notifyDataSetChanged();
            m_Adapter2.notifyDataSetChanged();
        }
        catch(Exception e){
            Toast.makeText(checkList.this, "구매할 수 없습니다!", Toast.LENGTH_SHORT).show();
        }


            // 구매목록으로 보내기 해야함

        }    };

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

    public void exchange(View v) {
        // 환전
        Intent intent = new Intent(getApplicationContext(), ExchangeRate.class);
        startActivity(intent);
    }
}
