package com.safesoft.uk2015.restopro.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.safesoft.uk2015.restopro.Adapters.GridViewAdapter_Orders;
import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.Activities.Main_SafeSoft;
import com.safesoft.uk2015.restopro.PostData.PostData_orders;
import com.safesoft.uk2015.restopro.databases.SQLite.LoadDataFromSQLite;

import java.util.ArrayList;

/**
 * Created by UK2015 on 21/06/2016.
 */
public class OrdersFragment extends Fragment {

    RecyclerView recyclerView;
    GridViewAdapter_Orders adapter;
    LoadDataFromSQLite data;
    private Context mContext;
    private ArrayList<PostData_orders> _dataorders;
    private String _currentwaiter;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        _currentwaiter = ((Main_SafeSoft)getActivity()).getWaiter();
        mContext = getActivity();
        data = new LoadDataFromSQLite(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleView);
        adapter = new GridViewAdapter_Orders(getActivity(), data.getDataOrders(), _currentwaiter);
        recyclerView.setAdapter(adapter);
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.space)));
        return rootView;
    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;
        public SpacesItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = space/2;
            if (pos == 4)
                outRect.bottom = 2 * space;
        }
    }

    public void Refresh_Orders(){

        adapter = new GridViewAdapter_Orders(getActivity(), data.getDataOrders(), _currentwaiter);
        recyclerView.setAdapter(adapter);
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.space)));
    }
}
