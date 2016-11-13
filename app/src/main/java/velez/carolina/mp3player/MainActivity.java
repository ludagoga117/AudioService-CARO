package velez.carolina.mp3player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private String nombre[] = {"Afterlife", "Back in black", "Closer"};


    //Para reproducir el audio
    Intent intent;
    boolean playing=false;
    ImageView play, next, prev;
    TextView Titulo_cancion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        intent = new Intent(MainActivity.this, BackgroundAudioService.class);

        Titulo_cancion = (TextView) findViewById(R.id.titulo);
        Titulo_cancion.setText(nombre[0]);

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


    private BroadcastReceiver ReceivefromService = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String status=intent.getStringExtra("newstatus");

            if( ! MainActivity.this.isFinishing() ){

                if( status.equals("play") ){
                    playing = true;
                    play.setImageResource(R.mipmap.pause);
                }else if( status.equals("pause") ){
                    playing = false;
                    play.setImageResource(R.mipmap.play);
                }else{
                    Titulo_cancion.setText(status);
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(ReceivefromService);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this,"Problemas soltando el broadcast receiver", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.actualizarEstado");
        registerReceiver(ReceivefromService, filter);
    }

}
