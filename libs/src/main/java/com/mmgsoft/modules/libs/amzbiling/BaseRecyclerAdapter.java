package com.mmgsoft.modules.libs.amzbiling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoahihi on 1/2/2018.
 * SavvyCom
 * dangkhoait1989@gmail.com
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
	OnItemClickListener<T> mItemClickListener;
	public BaseRecyclerAdapter(OnItemClickListener<T> itemClickListener){
		this.mItemClickListener = itemClickListener;
	}
	protected List<T> mListItems = new ArrayList<>();

	public abstract int getItemLayoutResource(int viewType);

	public abstract BaseRecyclerViewHolder getViewHolder(View view);

	@Override
	public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutResource(viewType), parent, false);
		BaseRecyclerViewHolder vh = getViewHolder(view);
		vh.setmItemClickListener(mItemClickListener);
		return vh;
	}

	@Override
	public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {

		if (mListItems.size() > 0 && position < mListItems.size())
			holder.setmModel(mListItems.get(position));
		holder.setmAdapter(this);
		holder.bindDataToViewHolder(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mListItems.size();
	}

	public void resetAllData(List<T> list) {
		if (list == null)
			return;
		mListItems.clear();
		mListItems.addAll(list);
		notifyDataSetChanged();
	}

	public void addAllData(List<T> list) {
		if (list == null)
			return;
		mListItems.addAll(list);
		notifyDataSetChanged();
	}

	public void updateAllData(List<T> list) {
		if (list == null)
			return;
		mListItems = list;
		notifyDataSetChanged();
	}

	public List<T> getmListItems() {
		return mListItems;
	}
}
