package com.example.rickandmorty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

// channel and notification
// https://developer.android.com/training/notify-user/channels
// https://developer.android.com/training/notify-user/build-notification
public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "Episode Link";
    private Button button_character;
    private Button button_location;
    private Button button_episode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_character = findViewById(R.id.button_character);
        button_location = findViewById(R.id.button_location);
        button_episode = findViewById(R.id.button_episode);

        // generate a new character every time clicked
        button_character.setOnClickListener(v -> loadFragment(new CharacterFragment()));
        button_episode.setOnClickListener(v -> loadFragment(new EpisodeFragment()));

        LocationFragment locationFragment = new LocationFragment();
        button_location.setOnClickListener(v -> loadFragment(locationFragment));

        createNotificationChannel();
    }


    public void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // replace fragmentView with the fragment object
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.commit();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}