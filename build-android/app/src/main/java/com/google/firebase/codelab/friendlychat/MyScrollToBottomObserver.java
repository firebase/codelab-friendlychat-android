package com.google.firebase.codelab.friendlychat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyScrollToBottomObserver extends RecyclerView.AdapterDataObserver {

    private RecyclerView mRecycler;
    private RecyclerView.Adapter<?> mAdapter;
    private LinearLayoutManager mManager;

    public MyScrollToBottomObserver(
            RecyclerView recycler,
            RecyclerView.Adapter<?> adapter,
            LinearLayoutManager manager) {
        this.mRecycler = recycler;
        this.mAdapter = adapter;
        this.mManager = manager;
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        int count = mAdapter.getItemCount();
        int lastVisiblePosition = mManager.findLastCompletelyVisibleItemPosition();
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.

        boolean loading = lastVisiblePosition == -1;
        boolean atBottom = positionStart >= (count - 1) && lastVisiblePosition == (positionStart - 1);
        if (loading || atBottom) {
            mRecycler.scrollToPosition(positionStart);
        }
    }

}
