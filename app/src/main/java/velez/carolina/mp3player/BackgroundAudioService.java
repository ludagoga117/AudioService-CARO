package velez.carolina.mp3player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class BackgroundAudioService extends Service {

    int num_song;
    private String nombre[] = {"Afterlife", "Back in black", "Closer"};
    private Integer songid[] = {R.raw.afterlife, R.raw.back_in_black,R.raw.closer
    };

    MediaPlayer mediaPlayer;
    int length;

    Intent notificationIntent;
    PendingIntent pendingIntent;

    Intent previousIntent;
    PendingIntent ppreviousIntent;

    Intent playIntent;
    PendingIntent pplayIntent;

    Intent nextIntent;
    PendingIntent pnextIntent;

    Intent pauseIntent;
    PendingIntent ppauseIntent;

    private actualizarBarra actualizador;

    @Override
    public void onCreate() {
        super.onCreate();

        length = 0;
        num_song=0;
        mediaPlayer = MediaPlayer.create(this, songid[num_song]);
        mediaPlayer.setLooping(true);

        actualizador = new actualizarBarra(this);


        notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction("velez.carolina.mp3player.BackgroundAudioService.main");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        previousIntent = new Intent(this, BackgroundAudioService.class);
        previousIntent.setAction("velez.carolina.mp3player.BackgroundAudioService.previous");
        ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        playIntent = new Intent(this, BackgroundAudioService.class);
        playIntent.setAction("velez.carolina.mp3player.BackgroundAudioService.play");
        pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        nextIntent = new Intent(this, BackgroundAudioService.class);
        nextIntent.setAction("velez.carolina.mp3player.BackgroundAudioService.next");
        pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        pauseIntent = new Intent(this, BackgroundAudioService.class);
        pauseIntent.setAction("velez.carolina.mp3player.BackgroundAudioService.pause");
        ppauseIntent = PendingIntent.getService(this, 0,
                pauseIntent, 0);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent.getAction().equals("velez.carolina.mp3player.BackgroundAudioService.start") ) {
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(length);
                mediaPlayer.start();
                actualizador.execute();

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("MP3 player")
                        .setTicker("MP3 player")
                        .setContentText(nombre[num_song])
                        .setSmallIcon(R.mipmap.musica)
                        .setOngoing(true)
                        .addAction(R.mipmap.left, ""/*"prev"*/, ppreviousIntent)
                        .addAction(R.mipmap.pause, ""/*"Pausar"*/, ppauseIntent)
                        .addAction(R.mipmap.rigth, ""/*"next"*/, pnextIntent)
                        .build();

                startForeground(9999,notification);
            }

        }else if( intent.getAction().equals("velez.carolina.mp3player.BackgroundAudioService.pause") ){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
                actualizador.cancel(true);
                actualizador = new actualizarBarra(this);

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("MP3 player")
                        .setTicker("MP3 player")
                        .setContentText(nombre[num_song])
                        .setSmallIcon(R.mipmap.musica)
                        .setOngoing(true)
                        .addAction(R.mipmap.left, ""/*"prev"*/, ppreviousIntent)
                        .addAction(R.mipmap.play, ""/*"Reanudar"*/, pplayIntent)
                        .addAction(R.mipmap.rigth, ""/*"next"*/, pnextIntent)
                        .build();

                startForeground(9999,notification);
            }
            // Si se le dio pausa a la notificacion, entonces hay que poner el botón de pausa en la actividad
            Intent i = new Intent("android.intent.action.actualizarEstado").putExtra("newstatus", "pause");
            this.sendBroadcast(i);

        }else if( intent.getAction().equals("velez.carolina.mp3player.BackgroundAudioService.play") ){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(length);
                mediaPlayer.start();
                actualizador.execute();

                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("MP3 player")
                        .setTicker("MP3 player")
                        .setContentText(nombre[num_song])
                        .setSmallIcon(R.mipmap.musica)
                        .setOngoing(true)
                        .addAction(R.mipmap.left, ""/*"prev"*/, ppreviousIntent)
                        .addAction(R.mipmap.pause, ""/*"Pausar"*/, ppauseIntent)
                        .addAction(R.mipmap.rigth, ""/*"next"*/, pnextIntent)
                        .build();
                startForeground(9999,notification);
            }

            // Si se le dio play a la notificacion, entonces hay que poner el botón de play en la actividad
            Intent i = new Intent("android.intent.action.actualizarEstado").putExtra("newstatus", "play");
            this.sendBroadcast(i);

            //next song
        } else if( intent.getAction().equals("velez.carolina.mp3player.BackgroundAudioService.next") ){
            mediaPlayer.release();
            switch (num_song){
                case 0:
                    mediaPlayer = MediaPlayer.create(this, songid[1]);
                    //seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    num_song=1;
                    break;
                case 1:
                    mediaPlayer = MediaPlayer.create(this, songid[2]);
                    //seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    num_song=2;
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(this, songid[0]);
                    //seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    num_song=0;
                    break;
            }

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("MP3 player")
                    .setTicker("MP3 player")
                    .setContentText(nombre[num_song])
                    .setSmallIcon(R.mipmap.musica)
                    .setOngoing(true)
                    .addAction(R.mipmap.left, ""/*"prev"*/, ppreviousIntent)
                    .addAction(R.mipmap.pause, ""/*"Pausar"*/, ppauseIntent)
                    .addAction(R.mipmap.rigth, ""/*"next"*/, pnextIntent)
                    .build();
            startForeground(9999,notification);


            Intent i = new Intent("android.intent.action.actualizarEstado").putExtra("newstatus", nombre[num_song]);
            this.sendBroadcast(i);


        }else if( intent.getAction().equals("velez.carolina.mp3player.BackgroundAudioService.previous") ){
            mediaPlayer.release();
            switch (num_song){
                case 0:
                    mediaPlayer = MediaPlayer.create(this, songid[2]);
                    //seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    num_song=2;
                    break;
                case 1:
                    mediaPlayer = MediaPlayer.create(this, songid[0]);
                    //seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    num_song=0;
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(this, songid[1]);
                    //seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    num_song=1;
                    break;
            }

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("MP3 player")
                    .setTicker("MP3 player")
                    .setContentText(nombre[num_song])
                    .setSmallIcon(R.mipmap.musica)
                    .setOngoing(true)
                    .addAction(R.mipmap.left, ""/*"prev"*/, ppreviousIntent)
                    .addAction(R.mipmap.pause, ""/*"Pausar"*/, ppauseIntent)
                    .addAction(R.mipmap.rigth, ""/*"next"*/, pnextIntent)
                    .build();
            startForeground(9999,notification);

            Intent i = new Intent("android.intent.action.actualizarEstado").putExtra("newstatus", nombre[num_song]);
            this.sendBroadcast(i);

        }
        return START_STICKY;
    }

    private class actualizarBarra extends AsyncTask<Void, Void, Integer>{

        private Context context;

        public actualizarBarra(Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            while( mediaPlayer.isPlaying() ){
                try {
                    new Thread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if( mediaPlayer.isPlaying() ) {
                    publishProgress();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            int segundosActuales = mediaPlayer.getCurrentPosition()/1000;
            int top = mediaPlayer.getDuration()/1000;
            Intent i = new Intent("android.intent.action.actualizarEstado")
                    .putExtra("newstatus", "segundos")
                    .putExtra("top",top)
                    .putExtra("segundos",segundosActuales);
            context.sendBroadcast(i);
        }
    }
}