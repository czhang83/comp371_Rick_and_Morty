package com.example.rickandmorty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cz.msebera.android.httpclient.Header;

// display information of a random character from the api
public class CharacterFragment extends Fragment {
    private static final String api_url = "https://rickandmortyapi.com/api/character";
    protected static AsyncHttpClient client = new AsyncHttpClient();

    private View view;
    private ImageView imageView_character;
    private TextView textView_name;
    private TextView textView_status;
    private TextView textView_species;
    private TextView textView_gender;
    private TextView textView_origin_name;
    private TextView textView_location_name;
    private TextView textView_episode_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_character, container, false);

        imageView_character = view.findViewById(R.id.imageView_character);
        textView_name = view.findViewById(R.id.textView_name);
        textView_status = view.findViewById(R.id.textView_status);
        textView_species = view.findViewById(R.id.textView_species);
        textView_gender = view.findViewById(R.id.textView_gender);
        textView_origin_name = view.findViewById(R.id.textView_origin_name);
        textView_location_name = view.findViewById(R.id.textView_location_name);
        textView_episode_list = view.findViewById(R.id.textView_episode_list);

        getCharacterInfo();
        return view;
    }

    // get a random character from the character api
    private void getCharacterInfo(){
        client.get(api_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("api", new String(responseBody));
                    // get a random page, then get a random character on that page
                    Random rand = new Random();
                    JSONObject response = new JSONObject(new String(responseBody));
                    int total_pages = Integer.parseInt(response.getJSONObject("info").getString("pages"));
                    int page = rand.nextInt(total_pages + 1);


                    // get characters on the page
                    client.get(api_url + "?page=" + page, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                Log.d("api", new String(responseBody));
                                JSONObject result = new JSONObject(new String(responseBody));
                                JSONArray character_lists = result.getJSONArray("results");
                                int index = rand.nextInt(character_lists.length());

                                JSONObject character = character_lists.getJSONObject(index);
                                Picasso.get().load(character.getString("image")).into(imageView_character);
                                textView_name.setText("Name: " + character.getString("name"));
                                textView_status.setText("Status: " + character.getString("status"));
                                textView_species.setText("Species: " + character.getString("species"));
                                textView_gender.setText("Gender: " + character.getString("gender"));
                                textView_origin_name.setText("Origin Name: " + character.getJSONObject("origin").getString("name"));
                                textView_location_name.setText("Location Name: " + character.getJSONObject("location").getString("name"));

                                // api gives a list of urls, extract episode number from them
                                JSONArray episodes = character.getJSONArray("episode");
                                String episode_list = "";
                                for (int i = 0; i < episodes.length(); i++){
                                    String[] url = episodes.getString(i).split("/");
                                    Log.d("api", Arrays.toString(url));
                                    if (i == 0){
                                        episode_list = episode_list.concat(url[url.length - 1]);
                                    } else {
                                        episode_list = episode_list.concat(", " + url[url.length - 1]);
                                    }
                                }
                                textView_episode_list.setText("Appeared in Episodes: " + episode_list);
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
}
