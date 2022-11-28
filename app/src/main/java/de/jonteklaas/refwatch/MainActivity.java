package de.jonteklaas.refwatch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import de.jonteklaas.refwatch.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private ActivityMainBinding binding;
    private LocalDateTime now;

    int minutesPassed = 1;
    int secondsPassed = 0;
    String extraMinutes = "0";
    String extraSeconds = "0";

    String minutesLeft;
    String secondsLeft;

    boolean gameStarted;
    boolean gameRunning;
    boolean gameOver;
    boolean secondHalf;
    int halfOverAt = 45;

    int runColor = Color.WHITE;

    String paragraphsOver = "\n       ";
    String paragraphsExtra = "\n\n\n\nNachspielzeit ";
    String paragraphsLeft = "\n\n";
    String paragraphsExtraRunning = "\n\n\n\n\n\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView currentTime = findViewById(R.id.currentTime);
        TextView over = findViewById(R.id.timeOver);
        TextView left = findViewById(R.id.timeLeft);
        TextView extraRunning = findViewById(R.id.extraTimeRunning);
        TextView extra = findViewById(R.id.extraTime);

        over.setText(paragraphsOver + "0");
        left.setText(paragraphsLeft + halfOverAt);
        extraRunning.setText(paragraphsExtraRunning + "00:00");
        extra.setText(paragraphsExtra + "00:00");
        over.setTextColor(Color.WHITE);
        left.setTextColor(Color.WHITE);
        extraRunning.setTextColor(Color.WHITE);
        extra.setTextColor(Color.WHITE);

        Window window = getWindow();

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final VibrationEffect vibrationEffect1;
        vibrationEffect1 = VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_TICK);

        Timer game = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!gameOver) {

                    if (secondsPassed == 60) {
                        secondsPassed = 0;
                        minutesPassed++;
                    }
                    minutesLeft = (halfOverAt - minutesPassed) + "";
                    if (halfOverAt - minutesPassed < 10) {
                        minutesLeft = "0" + (halfOverAt - minutesPassed);
                    }
                    secondsLeft = (59 - secondsPassed) + "";
                    if (59 - secondsPassed < 10) {
                        secondsLeft = "0" + (59 - secondsPassed);
                    }
                    secondsPassed++;

                    if (minutesPassed == halfOverAt && secondsPassed == 60) {
                        gameRunning = false;
                        gameOver = true;
                        secondHalf = true;
                        minutesPassed = 46;
                        secondsPassed = 0;
                        extraMinutes = "0";
                        extraSeconds = "0";
                        halfOverAt = 90;
                    }

                    paragraphsOver = "\n       ";
                    if (minutesPassed < 10) {
                        paragraphsOver += "  ";
                    }

                    runColor = Color.RED;
                    if (!gameRunning) {
                        extraSeconds = (Integer.parseInt(extraSeconds) + 1) + "";
                        if (extraSeconds.equals("60")) {
                            extraMinutes = (Integer.parseInt(extraMinutes) + 1) + "";
                            extraSeconds = "0";
                        }
                        runColor = Color.WHITE;
                        if (secondsPassed % 2 == 0) {
                            vibrator.vibrate(vibrationEffect1);
                        }
                    } else {
                        vibrator.cancel();
                    }

                    if (extraMinutes.length() == 1) {
                        extraMinutes = "0" + extraMinutes;
                    }
                    if (extraSeconds.length() == 1) {
                        extraSeconds = "0" + extraSeconds;
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            over.setText(paragraphsOver + minutesPassed + ".");
                            left.setText(paragraphsLeft + (minutesLeft) + ":" + (secondsLeft));
                            extraRunning.setText(paragraphsExtraRunning + extraMinutes + ":" + extraSeconds);
                            extra.setText(paragraphsExtra + extraMinutes + ":" + extraSeconds);
                            if (gameRunning) {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            } else {
                                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            }
                        }
                    });
                }
            }
        };

        left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (gameRunning) {
                    gameRunning = false;
                } else {
                    gameRunning = true;
                }
                if (!gameStarted) {
                    game.schedule(timerTask, 0, 1000);
                    gameStarted = true;
                }
                left.setTextColor(runColor);
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                now = LocalDateTime.now();
                currentTime.setText(now.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }, 0, 1000);
    }
}