package sw.konkuk.redvelvet;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RegistDialog2 extends Dialog {

    private TextView mTitleView;
    private TextView mNameView;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    EditText mEditName;
    TextView mContentView;

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.regist_product_2);

        mTitleView = (TextView) findViewById(R.id.txt_title);
        mNameView = (TextView) findViewById(R.id.txt_name);
        mLeftButton = (Button) findViewById(R.id.btn_left);
        mRightButton = (Button) findViewById(R.id.btn_right);
        mEditName = (EditText) findViewById(R.id.editName);
        mContentView = (TextView) findViewById(R.id.txt_price);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        mContentView.setText(mContent);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public RegistDialog2(Context context, String title,
                         View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mLeftClickListener = singleListener;
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public RegistDialog2(Context context, String title,
                         String content, View.OnClickListener leftListener,
                         View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }
}