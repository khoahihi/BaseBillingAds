package com.mmgsoft.modules.libs.amzbiling;

import android.view.View;

import com.mmgsoft.modules.libs.R;


public class ProductSkuAdapter extends BaseRecyclerAdapter<ProductItem> {

    public ProductSkuAdapter(OnItemClickListener<ProductItem> itemClickListener) {
        super(itemClickListener);
    }

    @Override
    public int getItemLayoutResource(int viewType) {
        if (viewType == IapViewType.SUB.ordinal()) {
            return R.layout.item_purchase_subs_default;
        }
        return R.layout.item_purchase_inapp_default;
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).iapViewType.ordinal();
    }

    @Override
    public BaseRecyclerViewHolder getViewHolder(View view) {
        return new ProductSkuViewHolder(view);
    }
}
