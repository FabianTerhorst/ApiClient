package io.fabianterhorst.apiclient.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

public class MainActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.repositories);
        RepositoriesAdapter adapter = new RepositoriesAdapter();
        if(recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
        //Github github = Github.getInstance(); is also working, but does not un subscribe on orientation change for example
        Github github = Github.getInstance(bindToLifecycle());
        github.getRepositories("realm").subscribe(adapter::setRepositories);
    }
}
