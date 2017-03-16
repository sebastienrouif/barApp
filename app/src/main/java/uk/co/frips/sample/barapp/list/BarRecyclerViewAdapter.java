package uk.co.frips.sample.barapp.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import uk.co.frips.sample.barapp.data.entity.Bar;
import uk.co.frips.sample.barapp.R;

import static com.google.common.base.Preconditions.checkNotNull;

public class BarRecyclerViewAdapter extends RecyclerView.Adapter<BarRecyclerViewAdapter.ViewHolder> {

    private List<Bar> mValues;
    private final OnListFragmentInteractionListener mListener;

    public BarRecyclerViewAdapter(List<Bar> items, OnListFragmentInteractionListener listener) {
        setList(items);
        mListener = listener;
    }

    public void replaceData(List<Bar> items) {
        setList(items);
        notifyDataSetChanged();
    }

    private void setList(List<Bar> items) {
        mValues = checkNotNull(items);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onBarClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnListFragmentInteractionListener {
        void onBarClicked(Bar item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Bar mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
