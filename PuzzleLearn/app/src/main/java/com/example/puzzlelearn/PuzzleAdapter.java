package com.example.puzzlelearn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
//Permet la gestion de la recyclerView
public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.ViewHolder> implements Filterable {

    private ArrayList<Puzzle> listPuzzles ;
    private ArrayList<Puzzle> myArrayList;
    private Context context;
    private MySQLite myDatabase;


    public PuzzleAdapter(Context context, ArrayList<Puzzle> listPuzzles ) {
        this.listPuzzles = listPuzzles;
        this.myArrayList = listPuzzles;
        this.context =context;
        myDatabase = new MySQLite(context);


    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView urlImage;
        TextView imageName;
        ImageView mRemoveButton;

        //Represente la vue de chaque item dans la recyclerView
        public ViewHolder(View itemView) {
            super(itemView);
            urlImage = itemView.findViewById(R.id.tv_url_item_liste);
            imageName = itemView.findViewById(R.id.tv_item_liste);
            mRemoveButton = itemView.findViewById(R.id.ib_remove);
        }
    }

    //cree un viewHolder a partir du layout XML de chaque item de la recyclerView
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liste, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Permet de mettre a jour chaque item de la recyclerView(appuyer sur l'image pour changer d'activité, supprimer un item)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final  Puzzle puzzle = listPuzzles.get(position);
        //Glide permet de construire l'image à partir de l'url
        Glide.with(context)
                .load(puzzle.getUrl_puzzle())
                .into(holder.urlImage);

        holder.imageName.setText(puzzle.getNom_puzzle());
        //lorsque l'on clique sur l'image on accede a la classe LaucherPuzzle
        holder.urlImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Toast.makeText(context, puzzle.getNom_puzzle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LauncherPuzzle.class);
                intent.putExtra("image_url", puzzle.getUrl_puzzle());
                intent.putExtra("image_name", puzzle.getNom_puzzle());
                context.startActivity(intent);

            }
        });
        //Permet de supprimer l'item de la recyclerView et de la bdd
        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database
                myDatabase.deletePuzzle(puzzle.getId_puzzle());
                //refresh the activity page.
                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }
    //Permet de trouver l'item souhaité dans la recyclerView
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    listPuzzles = myArrayList;

                } else {
                    ArrayList<Puzzle> filteredList = new ArrayList<>();
                    for (Puzzle puzzle : myArrayList) {
                        if (puzzle.getNom_puzzle().toLowerCase().contains(charString)) {
                            filteredList.add(puzzle);
                        }
                    }
                    listPuzzles = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listPuzzles;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listPuzzles = (ArrayList<Puzzle>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return listPuzzles.size();
    }



}

