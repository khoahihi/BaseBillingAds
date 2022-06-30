package com.mmgsoft.modules.libs.amzbiling;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazon.device.iap.PurchasingService;
import com.mmgsoft.modules.libs.R;
import com.mmgsoft.modules.libs.R2;
import com.mmgsoft.modules.libs.customview.SpacesItemDecoration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;

public class AmazonIapActivity extends BaseIapAmzActivity implements OnItemClickListener<ProductItem> {

    @BindView(R2.id.imBack)
    public AppCompatImageView imBack;

    @BindView(R2.id.rcvSkuAvai)
    public RecyclerView rcvSkuAvai;

    private ProductSkuAdapter mSkuAdapter;


    @Override
    public Set<String> getAllSkus() {
        Set<String> listSkus = new HashSet<>();
        listSkus.addAll(Arrays.asList(AppConstant.ITEM_ENTITLE, AppConstant.ITEM_SUB_TERM_1));
        return listSkus;
    }

    @Override
    public Integer getResLayout() {
        return R.layout.purchase_product_layout;
    }

    @Override
    public void initData() {
        imBack.setOnClickListener(v -> {
            super.onBackPressed();
        });
        initRecycler();
    }

    @Override
    public void notifyUpdateListView() {
        mSkuAdapter.notifyDataSetChanged();
    }

    private void initRecycler() {
        mSkuAdapter = new ProductSkuAdapter(this);
        rcvSkuAvai.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvSkuAvai.setItemAnimator(new DefaultItemAnimator());
        rcvSkuAvai.addItemDecoration(new SpacesItemDecoration(10, true));
        rcvSkuAvai.setHasFixedSize(true);
        rcvSkuAvai.setAdapter(mSkuAdapter);
        mSkuAdapter.updateAllData(productItems);
    }

    @Override
    public void onItemClicked(ProductItem item) {
        PurchasingService.purchase(item.sku);
    }
}
