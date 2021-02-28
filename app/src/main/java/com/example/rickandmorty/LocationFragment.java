package com.example.rickandmorty;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cz.msebera.android.httpclient.Header;

// gets the locations from the first page, display in recycler view
public class LocationFragment extends Fragment {
    private static final String api_url = "https://rickandmortyapi.com/api/location";
    protected static AsyncHttpClient client = new AsyncHttpClient();

    private View view;
    private ArrayList<Location> locations;
    private RecyclerView recyclerView_location;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);
        super.onCreate(savedInstanceState);

        // look up the recycler view in the activity xml
        recyclerView_location = view.findViewById(R.id.recyclerView_location);
        locations = new ArrayList<>();

        client.get(api_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("api", new String(responseBody));
                    JSONObject response = new JSONObject(new String(responseBody));
                    JSONArray locationsArray = response.getJSONArray("results");

                    for (int i = 0; i < locationsArray.length(); i++){
                        JSONObject locationObject = locationsArray.getJSONObject(i);
                        Location location = new Location(locationObject.getString("name"),
                                locationObject.getString("type"),
                                locationObject.getString("dimension"));
                        // add it to the arrayList
                        locations.add(location);
                    }

                    // pass locations to adapter
                    LocationAdapter adapter = new LocationAdapter(locations);
                    recyclerView_location.setAdapter(adapter);
                    recyclerView_location.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    recyclerView_location.setHasFixedSize(true);
                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                            view.getContext(), DividerItemDecoration.VERTICAL);
                    recyclerView_location.addItemDecoration(itemDecoration);
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


        return view;
    }

}
