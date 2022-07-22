package com.mmgsoft.modules.libs.amzbiling;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.mmgsoft.modules.libs.R;


public class ProductSkuViewHolder extends BaseRecyclerViewHolder<ProductItem> {
    View viewBlur;

    TextView tvTitle;

    TextView tvDescription;

    TextView tvPrice;

    MaterialCardView cardParent;

    public ProductSkuViewHolder(View itemView) {
        super(itemView);
        viewBlur = itemView.findViewById(R.id.viewBlur);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        cardParent = itemView.findViewById(R.id.cardParent);
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
