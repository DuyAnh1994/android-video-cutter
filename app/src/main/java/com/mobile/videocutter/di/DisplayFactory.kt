package com.mobile.videocutter.di

import com.mobile.videocutter.data.repo.FilterRepoImpl
import com.mobile.videocutter.domain.repo.IFilterRepo

object DisplayFactory {
    private val filterRepoImpl = FilterRepoImpl()

    fun getListFilter(): IFilterRepo {
        return filterRepoImpl
    }
}
