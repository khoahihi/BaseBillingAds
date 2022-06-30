package com.mmgsoft.modules.libs.amzbiling;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.mmgsoft.modules.libs.R;

import butterknife.BindView;

public class ProductSkuViewHolder extends BaseRecyclerViewHolder<ProductItem> {
    @BindView(R.id.viewBlur)
    View viewBlur;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    @BindView(R.id.cardParent)
    MaterialCardView cardParent;

    public ProductSkuViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindDataToViewHolder(int position) {
        ProductItem productItem = getmModel();
        tvTitle.setText(productItem.title);
        cardParent.setEnabled(!productItem.isBuy);
        viewBlur.setVisibility(productItem.isBuy ? View.VISIBLE : View.GONE);
        tvDescription.setText(productItem.description);
        tvPrice.setText(productItem.price);
        cardParent.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClicked(productItem);
            }
        });
    }

}
