package com.example.shubham_240326.repository

import com.example.shubham_240326.R
import com.example.shubham_240326.model.ProductModel

class ProductRepoImpl : ProductRepo {
    override fun getPopularProducts(callback: (List<ProductModel>) -> Unit) {
        val items = listOf(
            ProductModel("1", "Coffee", "Rich & Aromatic", "$4.50", R.drawable.coffee),
            ProductModel("2", "Cake", "Sweet & Fluffy", "$5.20", R.drawable.cake),
            ProductModel("3", "Donut", "Glazed & Tasty", "$3.00", R.drawable.donut),
            ProductModel("4", "Tea", "Fresh & Calming", "$3.50", R.drawable.tea)
        )
        callback(items)
    }
}
