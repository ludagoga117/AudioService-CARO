package velez.carolina.mp3player;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Context context;

    //Para reproducir el audio
    Intent intent;
    boolean playing=false;
    ImageView play, next, prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        intent = new Intent(MainActivity.this, BackgroundAudioService.class);

        play = (ImageView) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playing==false){
                    playing = true;
                    play.setImageResource(R.mipmap.pause);
                    intent.setAction("velez.carolina.mp3player.BackgroundAudioService.start");
                    startService(intent);
                }else{
                    playing = false;
                    play.setImageResource(R.mipmap.play);
                    intent.setAction("velez.carolina.mp3player.BackgroundAudioService.pause");
                    startService(intent);
                }

            }
        });

        prev = (ImageView) findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction("velez.carolina.mp3player.BackgroundAudioService.previous");
                startService(intent);
            }
        });

        next = (ImageView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setAction("velez.carolina.mp3player.BackgroundAudioService.next");
                startService(intent);
            }
        });

    }

}
