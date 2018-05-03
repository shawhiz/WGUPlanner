package com.smoo182.wguplanner.view.activities;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smoo182.wguplanner.PlannerApplication;
import com.smoo182.wguplanner.R;
import com.smoo182.wguplanner.data.datatypes.Term;
import com.smoo182.wguplanner.logic.TermListViewModel;

import java.util.List;

import javax.inject.Inject;


public class TermListActivity extends BasePrimaryActivity {
    private static final String EXTRA_TERM_ID = "EXTRA_TERM_ID";

    private List<Term> listOfTerms;
    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    TermListViewModel termListViewModel;


    @Override
    int getContentViewId() {
        return R.layout.activity_term_list;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.action_terms;
    }

    @Override
    void populateScreen() {
        FloatingActionButton addFab = findViewById(R.id.fab_add_term);
        addFab.setOnClickListener(view -> startDetailActivity(null, view));

        recyclerView = (RecyclerView) findViewById(R.id.rv_term_list);
        layoutInflater = getLayoutInflater();

        ((PlannerApplication) getApplication()).getApplicationComponent().inject(this);
        termListViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TermListViewModel.class);

        termListViewModel.getListOfTerms().observe(this, new Observer<List<Term>>() {
            @Override
            public void onChanged(@Nullable List<Term> terms) {
                if (listOfTerms == null) {
                    setListTerms(terms);
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startDetailActivity(String termId, View viewRoot) {
        Intent i = new Intent(this, TermDetailActivity.class);
        i.putExtra(EXTRA_TERM_ID, termId);

        if (termId != null) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this,
                            new Pair<View, String>(viewRoot.findViewById(R.id.list_item_title),
                                    "title"),
                            new Pair<View, String>(viewRoot.findViewById(R.id.list_item_subtitle)
                                    , "subtitle"));
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }}


        public void setListTerms (List < Term > listOfTerms) {
            this.listOfTerms = listOfTerms;
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CustomAdapter();
            recyclerView.setAdapter(adapter);

            DividerItemDecoration itemDecoration = new DividerItemDecoration(
                    recyclerView.getContext(),
                    layoutManager.getOrientation()
            );

            recyclerView.addItemDecoration(itemDecoration);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        public void onClick (View view){

        }


        private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
            public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int
                    viewType) {
                View v = layoutInflater.inflate(R.layout.list_item_term, parent, false);
                return new CustomViewHolder(v);
            }

            public void onBindViewHolder(CustomAdapter.CustomViewHolder holder, int position) {
                Term currentItem = listOfTerms.get(position);

                holder.title.setText(currentItem.getTitle());

                holder.subTitle.setText("from " + currentItem.getStartDate() + " to " +
                        currentItem.getEndDate());
            }

            @Override
            public int getItemCount() {
                return listOfTerms.size();
            }


            class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

                private TextView title;
                private TextView subTitle;
                private ViewGroup container;

                public CustomViewHolder(View itemView) {
                    super(itemView);
                    this.title = (TextView) itemView.findViewById(R.id.list_item_title);
                    this.subTitle = (TextView) itemView.findViewById(R.id.list_item_subtitle);
                    this.container = (ViewGroup) itemView.findViewById(R.id.root_list_item);
                    this.container.setOnClickListener(this);
                }

                public void onClick(View v) {
                    Term listTerm = listOfTerms.get(this.getAdapterPosition());
                    startDetailActivity(listTerm.getId().toString(), v);
                }
            }
        }

        private ItemTouchHelper.Callback createHelperCallback () {
            ItemTouchHelper.SimpleCallback simpleItemCouchCallback = new ItemTouchHelper
                    .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                        viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                }
            };
            return simpleItemCouchCallback;
        }
    }
