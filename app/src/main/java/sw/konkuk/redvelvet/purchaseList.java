package sw.konkuk.redvelvet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

/**
 * Created by hizz on 2017-11-02.
 */

public class purchaseList extends AppCompatActivity {

    Vector<Product> list; // 구매 리스트 목록

    private ListView m_name;
    private ListView m_price;
    private ArrayAdapter<String> m_Adapter1;
    private ArrayAdapter<String> m_Adapter2;
    double sum;

    public purchaseList() {
        this.list = new Vector();
        this.sum = 100;
    }

    void registerProduct() {
        // 상품 등록 버튼

        // 그니까 여기서  m_Adapter.add(객체) 하면 됨
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchaselist);

        // Android에서 제공하는 String 문자열 하나를 출력 가능한 layout으로 어댑터 생성
        m_Adapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        m_Adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);

        // xml에서 추가한 ListView 연결
        m_name = (ListView) findViewById(R.id.productNameList);
        m_price = (ListView) findViewById(R.id.productPriceList);

        // ListView에 어댑터 연결
        m_name.setAdapter(m_Adapter1);
        m_price.setAdapter(m_Adapter2);

        // ListView 아이템 터치 시 이벤트 추가
        m_name.setOnItemClickListener(onClickListItem);
        DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
        ExchangeRate ex = new ExchangeRate();
        String unit = ex.unit(db.get_country());
        TextView unitt = (TextView)findViewById(R.id.unit);
        unitt.setText(unit);//화폐단위 맞춰주기
        String[] price = db.get_price();
        String[] item = db.get_item();
        for(int i=0;i<item.length;i++){
            m_Adapter1.add(item[i]);
            m_Adapter2.add(toNumFormat(Integer.parseInt(price[i])));
        }
        // ListView에 아이템 추가
    }

    // 아이템 터치 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 이벤트 발생 시 해당 아이템 위치의 텍스트를 출력
            Toast.makeText(getApplicationContext(), m_Adapter1.getItem(arg2), Toast.LENGTH_SHORT).show();
        }
    };
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }
    public void home(View v) {
        // home
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
}
