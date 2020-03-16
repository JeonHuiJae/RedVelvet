package sw.konkuk.redvelvet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by sw-48 on 2017-12-01.
 */

public class TripListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_list);

        // 어댑터 생성
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);

        // ListView에 어댑터 연결
        listView = (ListView) findViewById(R.id.triplist);

        // ListView에 어댑터 연결
        listView.setAdapter(adapter);

        // ListView 아이템 터치 시 이벤트 추가
        listView.setOnItemClickListener(onClickListItem);

        DBHelper db = new DBHelper(getApplicationContext(), "Trip.db", null, 1);

        int num = db.howManyTrips();
        String[] titles = new String[num];
        String[] countries = new String[num];
        String[] sDates = new String[num];
        String[] eDates = new String[num];

        db.getAllInfo(titles, countries, sDates, eDates);

        for(int i=0; i<titles.length; i++) {
            String tmp = "";
            switch(countries[i]) {
                case "0":
                    tmp = "일본";
                    break;
                case "1":
                    tmp = "유럽";
                    break;
                case "2":
                    tmp = "중국";
                    break;
                case "3":
                    tmp = "미국";
                    break;
            }
            String str = titles[i] + "\t" + tmp + "\t" + sDates[i] + "\t" + eDates[i];
            adapter.add(str);
        }
    }

    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 클릭하면~
            DBHelper db = new DBHelper(getApplicationContext(), "Trip.db", null, 1);
            db.setCur(position);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    };
    public void add_Trip(View v){
        Intent intent = new Intent(getApplicationContext(), User.class);
        startActivity(intent);
    }
}
