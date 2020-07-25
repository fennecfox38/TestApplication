package com.android.test.PersonalData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.test.R;

import java.util.ArrayList;
import java.util.Locale;

public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder> {
    private ArrayList<PersonalData> list;
    private Context context;

    public interface OnItemClickListener{ void onItemClick(View view, int position); }
    public interface OnItemLongClickListener{ void onItemLongClick(View view, int position); }
    private OnItemClickListener itemClickListener = null;
    private OnItemLongClickListener itemLongClickListener = null;
    public void setOnItemClickListener(OnItemClickListener listener){ itemClickListener = listener; }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){ itemLongClickListener=listener; }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView item_name,item_age,item_sex,item_birthday,item_marriage,item_children;
        ViewHolder(View itemView){
            super(itemView);
            item_name=itemView.findViewById(R.id.item_name);
            item_age=itemView.findViewById(R.id.item_age);
            item_sex=itemView.findViewById(R.id.item_sex);
            item_birthday=itemView.findViewById(R.id.item_birthday);
            item_marriage=itemView.findViewById(R.id.item_marriage);
            item_children=itemView.findViewById(R.id.item_children);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        if(itemClickListener != null){
                            itemClickListener.onItemClick(v,position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        if(itemLongClickListener != null){
                            itemLongClickListener.onItemLongClick(v,position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
    PersonRecyclerAdapter(){list= new ArrayList<>();}
    PersonRecyclerAdapter(ArrayList<PersonalData> list){ this.list=list; }
    public void setList(ArrayList<PersonalData> list){ this.list=list; }
    public ArrayList<PersonalData> getList(){ return list; }

    @NonNull @Override
    public PersonRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_personal_data,parent,false);
        return (new PersonRecyclerAdapter.ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull PersonRecyclerAdapter.ViewHolder holder, int position) {
        PersonalData person = list.get(position);
        holder.item_name.setText(person.getName());
        holder.item_age.setText(String.format(Locale.getDefault(),"%d",person.getAge()));
        holder.item_sex.setText(context.getResources().getString(person.getSex().getStrId()));
        holder.item_birthday.setText(person.birthday.getDate());
        holder.item_marriage.setText(context.getResources().getString(person.getMarriage().getStrId()));
        holder.item_children.setText(context.getResources().getString((person.getChildren()?R.string.HaveChildren:R.string.HaveNoChildren)));
    }

    @Override public int getItemCount() { return list.size(); }

    public void addPerson(PersonalData person){
        list.add(person);
        notifyItemInserted(list.indexOf(person));
    }
    public PersonalData deletePerson(int index){
        PersonalData backup = list.get(index);
        list.remove(index);
        notifyItemRemoved(index);
        return backup;
    }
    public PersonalData deletePerson(PersonalData person){ return deletePerson(list.indexOf(person)); }

    public PersonalData editPerson(int index, PersonalData person){
        PersonalData original = list.get(index);
        list.set(index,person);
        notifyItemChanged(index);
        return original;
    }

    /*public ArrayList<PersonalData> queryPerson(String query){
        ArrayList<PersonalData> answer = new ArrayList<>();

        return answer;
    }*/
}
