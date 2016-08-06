package io.fabianterhorst.apiclient.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RepositoriesAdapter extends RecyclerView.Adapter<RepositoriesAdapter.ViewHolder> {

    private List<Repository> repositories;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.repository_name);
        }
    }

    public RepositoriesAdapter() {
        setHasStableIds(true);
        repositories = new ArrayList<>();
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return repositories.get(position).getId();
    }

    @Override
    public RepositoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repository, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(repositories.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return repositories.size();
    }
}
