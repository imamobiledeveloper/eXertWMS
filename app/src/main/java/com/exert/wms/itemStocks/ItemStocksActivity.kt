package com.exert.wms.itemStocks

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.exert.wms.BR
import com.exert.wms.R
import com.exert.wms.databinding.ActivityItemStocksBinding
import com.exert.wms.itemStocks.status.ItemStockStatusActivity
import com.exert.wms.mvvmbase.BaseActivity
import com.exert.wms.utils.hide
import com.exert.wms.utils.show
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ItemStocksActivity : BaseActivity<ItemStocksViewModel, ActivityItemStocksBinding>() {

    override val title = R.string.item_stocks

    override val showHomeButton: Int = 1

    override fun getLayoutID(): Int = R.layout.activity_item_stocks

    override val mViewModel by lazy {
        getViewModel<ItemStocksViewModel>()
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun onBindData(binding: ActivityItemStocksBinding) {
        binding.viewModel = mViewModel
    }

    override val coordinateLayout: CoordinatorLayout
        get() = binding.coordinateLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(title)
        observeViewModel()
    }

    private fun observeViewModel() {
        binding.statusButton.setOnClickListener {
            mViewModel.checkItemStock(
                binding.itemPartCodeSerialNoLayout.itemPartCodeEditText.text.toString(),
                binding.itemPartCodeSerialNoLayout.itemSerialNoEditText.text.toString()
            )
        }

        mViewModel.isLoadingData.observe(this, Observer { status ->
            if(status){
                binding.progressBar.show()
            }else{
                binding.progressBar.hide()
            }
        })

        mViewModel.errorItemSelectionMessage.observe(this, Observer {
            if (it) {
                disableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                )
            } else {
                enableErrorMessage(
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditTextLayout,
                    binding.itemPartCodeSerialNoLayout.itemPartCodeEditText,
                    getString(R.string.error_item_partcode_serial_no_empty_message)
                )
            }
        })

        mViewModel.itemStockStatus.observe(this, Observer {
            if (it) {
                startActivity<ItemStockStatusActivity>()
            } else {
                showBriefToastMessage(getString(R.string.error_get_items_message), coordinateLayout)
            }
        })

    }

}