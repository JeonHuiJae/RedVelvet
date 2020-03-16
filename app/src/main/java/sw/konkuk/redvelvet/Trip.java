package sw.konkuk.redvelvet;

import android.view.View;

/**
 * Created by sw-48 on 2017-11-06.
 */

public class Trip {
    String name;
    int[] start = new int[3]; // 시작 날짜 년-월-일로 저장
    int[] end = new int[3]; // 끝 날짜 년-월-일로 저장
    double money; // 예산
    AccountBook book; // 가계부
    int code; // 국가 코드
    purchaseList pList; // 구매 리스트
    checkList cList; // 체크 리스트

    public Trip(String name, int[] start, int[] end, double money, int code) {
        // 객체 생성시 초기화
        this.name = name;
        for(int i=0; i<start.length; i++) {
            this.start[i] = start[i];
            this.end[i] = end[i];
        }
        this.money = money;
        this.code = code;

        this.pList = new purchaseList();
        this.cList = new checkList();
    }
}
