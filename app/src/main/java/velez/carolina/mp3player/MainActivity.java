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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private String nombre[] = {"Afterlife", "Back in black", "Closer"};


    //Para reproducir el audio
    Intent intent;
    boolean playing=false, circulando=true, fin=false;
    ImageView play, next, prev, circ;
    TextView Titulo_cancion, time;

    private SeekBar sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        sb = (SeekBar) findViewById(R.id.SeekBar01);

        intent = new Intent(MainActivity.this, BackgroundAudioService.class);

        Titulo_cancion = (TextView) findViewById(R.id.titulo);
        Titulo_cancion.setText(nombre[0]);
        time = (TextView) findViewById(R.id.tiempo);

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
/*
        circ=(ImageView) findViewById(R.id.circulacion);
        circ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(circulando==true){
                    circulando = false;
                    circ.setImageResource(R.mipmap.sync);
                    intent.setAction("velez.carolina.mp3player.BackgroundAudioService.nocirc");
                    startService(intent);
                }else{
                    circulando = true;
                    circ.setImageResource(R.mipmap.sync_off);
                    intent.setAction("velez.carolina.mp3player.BackgroundAudioService.circ");
                    startService(intent);
                }
            }
        });*/
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
                }else if( status.equals("segundos") ){
                    int top=intent.getIntExtra("top",0);
                    int segundos=intent.getIntExtra("segundos",0);
                    sb.setMax(top);
                    sb.setProgress(segundos);
                    //System.out.println("seg="+segundos);
                    time.setText(milliSecondsToTimer(segundos*1000));
                    if(segundos>=top){
                        //System.out.println("fin");
                        Intent intent1=new Intent(context, BackgroundAudioService.class);
                        intent1.setAction("velez.carolina.mp3player.BackgroundAudioService.next");
                        context.startService(intent1);
                    }
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
    public  String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

}
