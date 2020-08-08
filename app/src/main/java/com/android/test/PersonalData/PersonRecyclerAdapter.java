package com.android.test.PersonalData;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.test.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class PersonRecyclerAdapter extends RecyclerView.Adapter<PersonRecyclerAdapter.ViewHolder> implements Filterable{
    private ArrayList<PersonalData> filteredList, unfilteredList;
    private Filter listFilter;
    private Context context;
    private int filterFlag=0;
    public final static int FLAG_MALE = 0x001<<7; public final static int FLAG_FEMALE = 0x001<<6;
    public final static int FLAG_NA_SEX = 0x001<<5; public final static int FLAG_SINGLE = 0x010;
    public final static int FLAG_MARRIED = 0x001<<3; public final static int FLAG_DIVORCED = 0x001<<2;
    public final static int FLAG_HAVE_CHILD = 0x001<<1; public final static int FLAG_NO_CHILD = 0x001;

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
    PersonRecyclerAdapter(){ unfilteredList = filteredList = new ArrayList<>(); }
    PersonRecyclerAdapter(ArrayList<PersonalData> unfilteredList){ this.unfilteredList = filteredList =unfilteredList; }
    public void setList(ArrayList<PersonalData> unfilteredList){ this.unfilteredList = filteredList =unfilteredList; }//organizeList(); }
    public ArrayList<PersonalData> getList(){ return unfilteredList; }//{ organizeList(); return list; }

    @NonNull @Override
    public PersonRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerview_personal_data,parent,false);
        return (new PersonRecyclerAdapter.ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull PersonRecyclerAdapter.ViewHolder holder, int position) {
        PersonalData person = filteredList.get(position);
        holder.item_name.setText(person.getName());
        holder.item_age.setText(String.format(Locale.getDefault(),"%d",person.getAge()));
        holder.item_sex.setText(context.getResources().getString(person.getSex().getStrId()));
        holder.item_birthday.setText(person.birthday.getDate());
        holder.item_marriage.setText(context.getResources().getString(person.getMarriage().getStrId()));
        holder.item_children.setText(context.getResources().getString((person.getChildren()?R.string.HaveChildren:R.string.HaveNoChildren)));
    }

    @Override public int getItemCount() { return filteredList.size(); }

    public boolean addPerson(PersonalData person){
        if(unfilteredList.indexOf(person)!=-1) return false;
        unfilteredList.add(person);
        notifyItemInserted(unfilteredList.indexOf(person));
        organizeList(); return true;
    }
    public PersonalData deletePerson(int index){
        PersonalData backup = unfilteredList.get(index);
        unfilteredList.remove(index);
        notifyItemRemoved(index);
        return backup;
    }
    public void deletePerson(PersonalData person){
        int index = unfilteredList.indexOf(person);
        unfilteredList.remove(index);
        notifyItemRemoved(index);
    }

    public boolean editPerson(PersonalData original, PersonalData person){
        int index = unfilteredList.indexOf(original);
        if(index==-1) return false;
        unfilteredList.set(index,person);
        notifyItemChanged(index);
        organizeList(); return true;
    }

    /*public void queryPerson(String query){
        ArrayList<PersonalData> answer = new ArrayList<>();
        list.addAll(backup);
        backup.clear();
        for(PersonalData pd : list) {
            if (pd.query(query) && checkFlag(pd)) answer.add(pd);
            else backup.add(pd);
        }
        list=answer;
        //notifyDataSetChanged();
        organizeList();
    }*/
    public void setFilterFlag(int flag,boolean set){
        if(set) filterFlag |= flag;
        else filterFlag &= ~flag;
    }
    private boolean checkFlag(PersonalData pd){
        switch(filterFlag&(FLAG_MALE|FLAG_FEMALE|FLAG_NA_SEX)){
            case FLAG_MALE: if(pd.getSex()!=PersonalData.Sex.Male) return false; break;
            case FLAG_FEMALE: if(pd.getSex()!=PersonalData.Sex.Female) return false; break;
            case FLAG_NA_SEX: if(pd.getSex()!=PersonalData.Sex.NA) return false; break;
            case FLAG_MALE|FLAG_FEMALE: if(pd.getSex()==PersonalData.Sex.NA) return false; break;
            case FLAG_FEMALE|FLAG_NA_SEX: if(pd.getSex()==PersonalData.Sex.Male) return false; break;
            case FLAG_NA_SEX|FLAG_MALE: if(pd.getSex()==PersonalData.Sex.Female) return false; break;
            case 0: case (FLAG_MALE|FLAG_FEMALE|FLAG_NA_SEX): break;
        }
        switch(filterFlag&(FLAG_SINGLE|FLAG_MARRIED|FLAG_DIVORCED)){
            case FLAG_SINGLE: if(pd.getMarriage()!=PersonalData.Marriage.Single) return false; break;
            case FLAG_MARRIED: if(pd.getMarriage()!=PersonalData.Marriage.Married) return false; break;
            case FLAG_DIVORCED: if(pd.getMarriage()!=PersonalData.Marriage.Divorced) return false; break;
            case FLAG_SINGLE|FLAG_MARRIED: if(pd.getMarriage()==PersonalData.Marriage.Divorced) return false; break;
            case FLAG_MARRIED|FLAG_DIVORCED: if(pd.getMarriage()==PersonalData.Marriage.Single) return false; break;
            case FLAG_DIVORCED|FLAG_SINGLE: if(pd.getMarriage()==PersonalData.Marriage.Married) return false; break;
            case 0: case (FLAG_SINGLE|FLAG_MARRIED|FLAG_DIVORCED): break;
        }
        switch(filterFlag&(FLAG_HAVE_CHILD|FLAG_NO_CHILD)){
            case FLAG_HAVE_CHILD: if(!pd.getChildren()) return false; break;
            case FLAG_NO_CHILD: if(pd.getChildren()) return false; break;
            case 0: case (FLAG_HAVE_CHILD|FLAG_NO_CHILD): break;
        }
        return true;
    }

    /*public void restoreListFromBackup(){
        if(backup.isEmpty()) return;
        list.addAll(backup);
        backup.clear();
        //notifyDataSetChanged();
        organizeList();
    }*/

    public void organizeList(){
        Collections.sort(filteredList);
        notifyDataSetChanged();
    }

    @Override public Filter getFilter() {
        if(listFilter==null) listFilter = new ListFilter();
        return listFilter;
    }
    private class ListFilter extends Filter{
        @Override protected FilterResults performFiltering(@Nullable CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null){ //|| constraint.length() ==0 ){
                results.values = unfilteredList;
                results.count = unfilteredList.size();
            }else{
                ArrayList<PersonalData> filteringList = new ArrayList<PersonalData>();
                for(PersonalData pd : unfilteredList)
                    if(pd.query(constraint.toString()) && checkFlag(pd))
                        filteringList.add(pd);
                results.values = filteringList;
                results.count = filteringList.size();
            }
            return results;
        }
        @Override protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<PersonalData>) results.values;
            Collections.sort(filteredList);
            notifyDataSetChanged();
        }
    }

}
