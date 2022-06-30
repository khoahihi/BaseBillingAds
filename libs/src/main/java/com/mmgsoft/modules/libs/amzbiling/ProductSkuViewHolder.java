package com.mmgsoft.modules.libs.amzbiling;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.mmgsoft.modules.libs.R;
import com.mmgsoft.modules.libs.R2;

import butterknife.BindView;

public class ProductSkuViewHolder extends BaseRecyclerViewHolder<ProductItem> {
    @BindView(R2.id.viewBlur)
    View viewBlur;

    @BindView(R2.id.tvTitle)
    TextView tvTitle;

    @BindView(R2.id.tvDescription)
    TextView tvDescription;

    @BindView(R2.id.tvPrice)
    TextView tvPrice;

    @BindView(R2.id.cardParent)
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
