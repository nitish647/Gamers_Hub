package com.nitish.gamershub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.Myviewholder>{
public  static String sharedpref_fav= "SHAREDPREF_FAV";
public ArrayList game_name;
public  ArrayList game_link ;
    public  ArrayList game_img ;
    public JSONArray jsonArray;
public JSONArray jsonArray_fav = new JSONArray();
    public String usage;
public Context context;

   ArrayMap arrays;
    public RecyclerviewAdapter(String usage, Context context, ArrayList game_name, ArrayList game_link, ArrayList game_img,JSONArray jsonArray)
{
this.usage=usage;
this.jsonArray=jsonArray;
this.game_link=game_link;
this.game_img = game_img;
this.game_name = game_name;
this.context=context;






}



    @NonNull
    @Override
    public Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_list_item1,parent,false);
        return new Myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myviewholder holder, final int position) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedpref_fav,MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
       String url_id = null;
         int img_file = 0;
        String name = null;


        try {
            final String json_obj = jsonArray.getJSONObject(position).toString();
            url_id= jsonArray.getJSONObject(position).getString("url");
        img_file = jsonArray.getJSONObject(position).getInt("img_file");
            name = jsonArray.getJSONObject(position).getString("name");
            holder.game_name.setText(name);
            holder.game_image.setImageResource(img_file);

       // holder.game_name.setText(String.valueOf( game_name.get(position)));
//        Picasso.get().load(String.valueOf(game_img.get(position))).into(holder.game_image);

        final String finalUrl_id = url_id;
            final int finalImg_file = img_file;
        final String finalName = name;





            holder.game_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Game_desc.class) ;
                intent.putExtra("url", finalUrl_id);
                intent.putExtra("json", json_obj);
                intent.putExtra("name", finalName);
                intent.putExtra("img", finalImg_file);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

    return jsonArray.length();

    }
    public static class Myviewholder extends RecyclerView.ViewHolder
    {
ImageView game_image;
TextView game_name;

        public Myviewholder(@NonNull View itemView) {

            super(itemView);
            game_image = (ImageView)itemView.findViewById(R.id.game_img);
            game_name = (TextView)itemView.findViewById(R.id.game_name);


        }
    }
    public void show_toast(String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
