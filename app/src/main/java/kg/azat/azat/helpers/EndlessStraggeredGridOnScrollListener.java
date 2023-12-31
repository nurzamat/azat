package kg.azat.azat.helpers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by nurzamat on 8/30/16.
 */
public abstract class EndlessStraggeredGridOnScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessStraggeredGridOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private StaggeredGridLayoutManager mStaggeredLayoutManager;

    public EndlessStraggeredGridOnScrollListener(StaggeredGridLayoutManager _mStaggeredLayoutManager) {
        this.mStaggeredLayoutManager = _mStaggeredLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mStaggeredLayoutManager.getItemCount();
        int[] firstVisibleItems = null;
        firstVisibleItems = mStaggeredLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);

        if(firstVisibleItems != null && firstVisibleItems.length > 0) {
            pastVisibleItems = firstVisibleItems[0];
        }

        if (loading) {
            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (pastVisibleItems + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }

    }

    public abstract void onLoadMore(int current_page);
}
