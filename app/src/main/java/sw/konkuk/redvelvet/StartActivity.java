package sw.konkuk.redvelvet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by sw-45 on 2017-11-27.
 */

public class StartActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button button1 = (Button) findViewById(R.id.button1) ;
        Button button2 = (Button) findViewById(R.id.button2) ;

        ImageView back  = (ImageView) findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(back);
        Glide.with(this).load(R.drawable.back).into(gifImage);

        button1.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User.class);
                startActivity(intent);
                }
             });

        button2.setOnClickListener(new Button.OnClickListener() {
            @Override public void onClick(View view) {
                try{
                    DBHelper db = new DBHelper(getApplicationContext(),"Trip.db",null,1);
                    if(db.get_country()!=0&&db.get_country()!=1&&db.get_country()!=2&&db.get_country()!=3)
                    {throw new Exception();}
                    // 메인으로
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    //db저장

            } catch (Exception e) {
                    Toast.makeText(StartActivity.this, "저장되어있는 가계부가 없습니다!", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}
