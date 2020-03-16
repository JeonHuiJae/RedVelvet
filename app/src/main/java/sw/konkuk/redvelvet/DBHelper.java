package sw.konkuk.redvelvet;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by sw-45 on 2017-11-03.
 */

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE PRODUCT (_id INTEGER PRIMARY KEY AUTOINCREMENT, item TEXT, price TEXT, belong_to INTEGER);");//물건(이름, 가격, 소속)
        db.execSQL("CREATE TABLE CHECKLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, item TEXT, price TEXT, belong_to INTEGER);");//체크리스트물건(이름, 가격, 소속)
        db.execSQL("CREATE TABLE TRIP (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT,country INTEGER, budget DOUBLE ,Sdate TEXT, Edate TEXT, cur INTEGER, sum DOUBLE);");//여행(제목, 국가, 예산, 남은예산, 출국, 입국, 현재사용중)
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
//<--------------------------데이터 저장---------------------------------->

    public void insertP(String item, String price) {//국가 예산 날짜 넣기
        int key=this.get_key();

        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO PRODUCT VALUES(null,'"+item+"', '" +price + "',"+key+");");
        db.close();
    }
    public void insertC(String item, String price) {//체크리스트에 국가 예산 날짜 넣기
        int key=this.get_key();

        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO CHECKLIST VALUES(null,'"+item+"', '" +price + "',"+key+");");
        db.close();
    }

    public void insertT(String name, int country, double budget, int[] Sdate, int[] Edate) {//국가 예산 날짜 넣기
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TRIP SET cur=0 WHERE cur=1;");
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO TRIP VALUES(null,'"+name+"'," + country + ", " + budget + ", '" + Sdate[0] + "-"+ (Sdate[1]+1) + "-"+ Sdate[2] +
                "','"+Edate[0]+"-"+ (Edate[1]+1) + "-"+ Edate[2] +"',1, 0);");
        db.close();
    }

    public void setCur(int position) {

        int n = position + 1;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TRIP SET cur=0 WHERE cur=1;");
        db.execSQL("UPDATE TRIP SET cur = 1 WHERE _id ="+n+";");
        db.close();
    }

//    public void updateP(String item, int price) {//물건 이름에따른 가격수정 수정은 아직 필요하진 않는ㄴ듯
//        SQLiteDatabase db = getWritableDatabase();
//        // 입력한 항목과 일치하는 행의 가격 정보 수정
//        db.execSQL("UPDATE PRODUCT SET price=" + price + " WHERE item='" + item + "';");
//        db.close();
//    }
//
//    public void updateS(String country, int budget) {//국가에따른 예산 수정
//        SQLiteDatabase db = getWritableDatabase();
//        // 입력한 항목과 일치하는 행의 가격 정보 수정
//        db.execSQL("UPDATE TRIP SET budget=" + budget + " WHERE country='" + country + "';");
//        db.close();
//    }

    public void updateSUM(double sum) {//구매금액 업데이트
        SQLiteDatabase db = getWritableDatabase();
      //  int key = get_key();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE TRIP SET sum=" + sum + " WHERE cur=" + 1 + ";");
        db.close();
    }



//<---------------------------데이터 삭제 함수---------------------------------->
    public void deleteP(String item) {//물건이름으로 구매물건 삭제
        int key = this.get_key();
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM PRODUCT WHERE item='" + item + "' AND belong_to="+key+";");
        db.close();
    }
    public void deleteC(String item) {//물건이름으로 체크리스트 삭제
        int key = this.get_key();
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM CHECKLIST WHERE item='" + item + "' AND belong_to="+key+";");
        db.close();
    }
    public void deleteT(String name) {//이름으로 여행정보 삭제
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM TRIP WHERE name='" + name +"';");
        db.close();
    }
    public int get_size(){
        int key = this.get_key();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PRODUCT WHERE belong_to="+key+";", null);
        int count=0;
        while (cursor.moveToNext()){
            count++;
        }
        return  count;
    }

    public int howManyTrips() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP;", null);
        int count = 0;
        while(cursor.moveToNext()) {
            count++;
        }
        return count;
    }
////<----------------------------물건정보 보기---------------------------------->
    public String[] get_item(){
        String[] result= new String[this.get_size()];
        int key = this.get_key();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PRODUCT WHERE belong_to="+key+";", null);
        int count=0;
        while (cursor.moveToNext()){
        result[count] = cursor.getString(1);//이름
            count++;
            }
            db.close();
        return result;
    }
    public String[] get_price(){
        String[] result= new String[this.get_size()];
        int key = this.get_key();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PRODUCT WHERE belong_to="+key+";", null);
        int count=0;
        while (cursor.moveToNext()){
            result[count] = cursor.getString(2);//가격
            count++;
        }
            db.close();
        return result;
    }

    ////<----------------------------체크리스트 물건정보 보기---------------------------------->///////////////////////////////////////////////////////////////////////////////////////////////////////여기
    public int get_C_size(){
        int key = this.get_key();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CHECKLIST WHERE belong_to="+key+";", null);
        int count=0;
        while (cursor.moveToNext()){
            count++;
        }
        return  count;
    }
    public double getWishSum() {
        int key = this.get_key();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CHECKLIST WHERE belong_to="+key+";", null);

        double sum = 0;

        while(cursor.moveToNext()) {
            String str = cursor.getString(2);
            sum += Double.parseDouble(str);
        }
        return sum;
    }
    public String[] get_C_item(){
        String[] result= new String[this.get_C_size()];
        int key = this.get_key();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CHECKLIST WHERE belong_to="+key+";", null);
        int count=0;
        while (cursor.moveToNext()){
            result[count] = cursor.getString(1);//이름
            count++;
        }
        db.close();
        return result;
    }
    public String[] get_C_price(){
        String[] result= new String[this.get_C_size()];
        int key = this.get_key();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CHECKLIST WHERE belong_to="+key+";", null);
        int count=0;
        while (cursor.moveToNext()){
            result[count] = cursor.getString(2);//가격
            count++;
        }
        db.close();
        return result;
    }

    public void getAllInfo(String[] titles, String[] countries, String[] sDates, String[] eDates) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP;", null);

        int count = 0;
        while(cursor.moveToNext()) {
            titles[count] = cursor.getString(1);
            countries[count] = cursor.getString(2);
            sDates[count] = cursor.getString(4);
            eDates[count] = cursor.getString(5);
            count++;
        }

    }

    //////<-----------------------------여행일정 데이터를 읽어오는 함수들---------------------------------->
    public int get_key(){
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
        result = cursor.getInt(0);//key
        db.close();
        return result;
    }
    public String get_title(){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
        result = cursor.getString(1);//제목
        db.close();
        return result;
    }
    public int get_country(){
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
            result = cursor.getInt(2);//국가
        db.close();
        return result;
    }
    public double get_budget(){
        SQLiteDatabase db = getReadableDatabase();
        double result =0;
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
        result += cursor.getDouble(3);//예산
        db.close();
        return result;
    }
    public String get_sdate(){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
        result += cursor.getString(4);//출국일
        db.close();
        return result;
    }

    public String get_edate(){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
        result += cursor.getString(5);//입국일
        db.close();
        return result;
    }

    public double get_sum() {
        SQLiteDatabase db = getReadableDatabase();
        double sum = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
        cursor.moveToNext();
        sum = cursor.getDouble(7);// 전체 금액
        db.close();
        return sum;

    }

    public String getResultT() {//여행정보 열기
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM TRIP WHERE cur=1", null);
            result += cursor.getString(0)//이름
                    + " : "
                    + cursor.getString(1)//국가
                    + " | "
                    + cursor.getDouble(2)//예산
                    + "원\n"
                    + cursor.getString(3)//출국일
                    + " ~ "
                    + cursor.getInt(4);//입국일
        db.close();
        return result;
    }
}