package com.example.jay.githubprofileapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.Holder> {

    private static final String TAG = "RepoAdapter";
    private List<Repo> list;
    private List<Integer> selected = new ArrayList<>();

    public RepoAdapter() {
        list = new ArrayList<>();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public List<Repo> getList() {
        return list;
    }

    public void setList(List<Repo> newList) {
        list = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repo_item, viewGroup, false);
        Holder holder = new Holder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Repo item = list.get(i);
        holder.tvName.setText(item.getName());
        holder.tvLanguage.setText(item.getLanguage());
        holder.tvSize.setText(ExtensionsKt.addNum("Size", item.getSize()));
        holder.tvStarCount.setText(ExtensionsKt.addNum("Stars", item.getStargazers_count()));
        holder.tvWatchers.setText(ExtensionsKt.addNum("Watcher", item.getWatchers_count()));
        holder.tvForks.setText(ExtensionsKt.addNum("Forks", item.getForks_count()));

        if (item.getFork()) {
            holder.itemView.setBackgroundColor(Color.MAGENTA);
        } else {
            holder.itemView.setBackgroundColor(Color.GREEN);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvRepoName)
        TextView tvName;
        @BindView(R.id.tvSize)
        TextView tvSize;
        @BindView(R.id.tvRepoLanguage)
        TextView tvLanguage;
        @BindView(R.id.tvStarCount)
        TextView tvStarCount;
        @BindView(R.id.tvForkCount)
        TextView tvForks;
        @BindView(R.id.tvWatcher)
        TextView tvWatchers;



        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}