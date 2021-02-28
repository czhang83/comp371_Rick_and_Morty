package com.example.rickandmorty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {
    private List<Character> characters;

    //pass this list into the constructor of the adapter
    public CharacterAdapter(List<Character> characters) {
        this.characters = characters;
    }

    @NonNull
    @Override
    public CharacterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate the customer layout
        View characterView = inflater.inflate(R.layout.item_character, parent, false);
        return new CharacterAdapter.ViewHolder(characterView);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterAdapter.ViewHolder holder, int position) {
        // populate data into the item through holder
        Character character = characters.get(position);

        Picasso.get().load(character.getImage_url()).into(holder.imageView_item_character);
        holder.textView_item_character_name.setText(character.getName());
    }

    @Override
    public int getItemCount() {
        // return the total number of items in the list
        return characters.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_item_character;
        TextView textView_item_character_name;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView_item_character = itemView.findViewById(R.id.imageView_item_character);
            textView_item_character_name = itemView.findViewById(R.id.textView_item_character_name);
        }

    }
}
