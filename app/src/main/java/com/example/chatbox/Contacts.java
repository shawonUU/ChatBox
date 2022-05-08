package com.example.chatbox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Contacts extends RecyclerView.Adapter<Contacts.mayViewHolder> {

    private static ClickListener clickListener;
    Context context;
    Vector<String> name = new Vector<String>();
    Vector<String> status = new Vector<String>();
    Vector<String> image = new Vector<String>();
    Vector<String> uid = new Vector<String>();


    public Contacts(Context context, Vector<String> name, Vector<String> status, Vector<String> image, Vector<String> uid) {
        this.context = context;
        this.name = name;
        this.status = status;
        this.image = image;
        this.uid = uid;
    }


    @NonNull
    @Override
    public mayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mView = layoutInflater.inflate(R.layout.find_friend_layout,parent,false);
        return new mayViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull mayViewHolder holder, int position) {
        holder.nameView.setText(name.get(position));
        holder.statusView.setText(status.get(position));
        if(image.get(position).equals("Male"))holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.defoult_image_male));
        else if(image.get(position).equals("Female"))holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.defoult_image_female));
        else Picasso.get().load(image.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class mayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView nameView,statusView;
        CircleImageView imageView;
        public mayViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.userNmaeID);
            statusView = itemView.findViewById(R.id.statusID);
            imageView = itemView.findViewById(R.id.profilePicID);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(),v);
            return false;
        }
    }
    public interface ClickListener{
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
    public void setOnItemClickListener(ClickListener clickListener){
        Contacts.clickListener = clickListener;
    }
}








              
























