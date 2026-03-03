package com.example.shubham_240326.repository

import com.example.shubham_240326.model.ProductModel

interface ProductRepo {
    fun getPopularProducts(callback: (List<ProductModel>) -> Unit)
}
