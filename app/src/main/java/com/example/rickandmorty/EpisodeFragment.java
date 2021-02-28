package com.example.rickandmorty;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

// get a random episode and display its information
// button clicked - give a notification that opens the browser with the link to the episode info

// start an activity from notification (used implicit intent)
// https://developer.android.com/training/notify-user/navigation#java
public class EpisodeFragment extends Fragment {

    private static final String api_url = "https://rickandmortyapi.com/api/episode";
    protected static AsyncHttpClient client = new AsyncHttpClient();

    private View view;
    private TextView textView_episode_number;
    private TextView textView_episode_name;
    private TextView textView_episode_air_date;
    private Button button_more_info;

    private String episode_number;
    private String episode_name;
    private String episode_url;

    private ArrayList<Character> characters;
    private RecyclerView recyclerView_characters;
    private CharacterAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_episode, container, false);

        textView_episode_number = view.findViewById(R.id.textView_episode_number);
        textView_episode_name = view.findViewById(R.id.textView_episode_name);
        textView_episode_air_date = view.findViewById(R.id.textView_episode_air_date);
        button_more_info = view.findViewById(R.id.button_more_info);
        // button onClick set after client got info

        recyclerView_characters = view.findViewById(R.id.recyclerView_characters);
        characters = new ArrayList<>();
        initializeRecycler(); // client later update data when it gets data

        getEpisodeInfo();

        return view;
    }

    // get a random character from the character api
    private void getEpisodeInfo(){
        client.get(api_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("api", new String(responseBody));
                    // get a random index among total count
                    // then get the corresponding page - third page only have 1 episode
                    Random rand = new Random();
                    JSONObject response = new JSONObject(new String(responseBody));
                    int total_count = Integer.parseInt(response.getJSONObject("info").getString("count"));
                    int index = rand.nextInt(total_count);
                    // get the corresponding index within the page
                    JSONArray episode_list = response.getJSONArray("results");
                    int page = index / episode_list.length() + 1;
                    int index_in_page = index % episode_list.length();
                    Log.d("api", "index "+ index + " index in page " + index_in_page);
                    // get characters on the page
                    client.get(api_url + "?page=" + page, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                Log.d("api", new String(responseBody));
                                JSONObject result = new JSONObject(new String(responseBody));
                                JSONObject episode = result.getJSONArray("results").getJSONObject(index_in_page);

                                episode_number = episode.getString("episode");
                                episode_name = episode.getString("name");
                                textView_episode_number.setText(episode_number);
                                textView_episode_name.setText(episode_name);
                                textView_episode_air_date.setText("Air data: " + episode.getString("air_date"));

                                // string would not be empty
                                button_more_info.setOnClickListener(v -> notifyLink(v));

                                // create horizontal recycler view for characters in the episode
                                JSONArray charactersArray = episode.getJSONArray("characters");
                                for (int i = 0; i < charactersArray.length() && i < 3; i++){
                                    String character_url = charactersArray.getString(i);
                                    client.get(character_url, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    try {
                                                        JSONObject character_info = new JSONObject(new String(responseBody));
                                                        Character character = new Character(character_info.getString("name"),
                                                                                            character_info.getString("image"));
                                                        characters.add(character);
                                                        adapter.notifyDataSetChanged();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    Toast toast = Toast.makeText(view.getContext(), R.string.character_api_fail, Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("api", "api request fail");
                            Toast toast = Toast.makeText(view.getContext(), R.string.api_fail, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("api", "api request fail");
                Toast toast = Toast.makeText(view.getContext(), R.string.api_fail, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    // give a notification that opens the browser with the link to the episode info
    // channel created in main activity
    private void notifyLink(View v){
        episode_url = "https://rickandmorty.fandom.com/wiki/" + episode_name.replaceAll(" ", "_");
        int NOTIFICATION_ID = 234;
        // send notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(view.getContext(), MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(episode_number + ": " + episode_name)
                .setContentText("To read more information about Episode " + episode_number +
                        ", please visit: " + episode_url + ".")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("To read more information about Episode " + episode_number +
                                ", please visit: " + episode_url +
                                "."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // implicit intent to browser
        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(episode_url));
        if (resultIntent.resolveActivity(view.getContext().getPackageManager()) != null){
            // send the intent
            // Create the TaskStackBuilder and add the intent, which inflates the back stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(view.getContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            NotificationManager notificationManager = view.getContext().getSystemService(NotificationManager.class);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
        // if not, log error
        else {
            Log.e("ImplicitIntent", "Cannot handle this intent");
            Toast toast = Toast.makeText(view.getContext(), R.string.link_fail, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void initializeRecycler(){
        // pass characters to adapter
        adapter = new CharacterAdapter(characters);
        recyclerView_characters.setAdapter(adapter);
        recyclerView_characters.setLayoutManager(
                new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView_characters.setHasFixedSize(true);
    }
}
