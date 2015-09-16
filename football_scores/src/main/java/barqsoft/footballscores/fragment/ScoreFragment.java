package barqsoft.footballscores.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.adapter.RecyclerScoresAdapter;
import barqsoft.footballscores.dao.DatabaseContract;
import barqsoft.footballscores.view.MyRecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScoreFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MyRecyclerView.MyRecylerCallbacks{
    public static final int SCORES_LOADER = 0;
    public RecyclerScoresAdapter mAdapter;

    private static final String EXTRA_DATE = "barqsoft.footbalscores.date" ;

    @Bind(R.id.recycler_view)
    MyRecyclerView mRecyclerView;
    @Bind(R.id.tv_empty)
    TextView mEmptyTextView;
    @Bind(android.R.id.empty)
    RelativeLayout mEmptyView;
    @Bind(R.id.progress_container)
    LinearLayout mProgressContainer;
    private String fragmentDate;

    public static ScoreFragment newInstance(String date){
        ScoreFragment fragment = new ScoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DATE, date);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ScoreFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args ;
        if (savedInstanceState == null){
            args = getArguments();
        }else{
            args = savedInstanceState;
        }

        fragmentDate = args.getString(EXTRA_DATE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_DATE, fragmentDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scores, container, false);
        ButterKnife.bind(this, rootView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setEmptyView(mEmptyView);

        mAdapter = new RecyclerScoresAdapter(getActivity(), null);
        mAdapter.setCallbacks(this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.detailMatchId = MainActivity.sSelectedMatchId;

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                DatabaseContract.ScoresEntry.buildScoreWithDate(),
                null,
                null,
                new String[]{fragmentDate},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void OnItemClick(View view, int position) {
        RecyclerScoresAdapter.ViewHolder selected = (RecyclerScoresAdapter.ViewHolder) view.getTag();
        mAdapter.detailMatchId = selected.matchId;
        MainActivity.sSelectedMatchId = (int) selected.matchId;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnLongItemClick(View view, int position) {

    }
}
