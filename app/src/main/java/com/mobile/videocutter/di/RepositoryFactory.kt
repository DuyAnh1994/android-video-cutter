package com.mobile.videocutter.di

import com.mobile.videocutter.data.repo.LocalDataRepoImpl
import com.mobile.videocutter.domain.repo.ILocalDataRepo

object RepositoryFactory {
    private val localDataRepoImpl = LocalDataRepoImpl()

    fun getLocalDataRepo(): ILocalDataRepo {
        return localDataRepoImpl
    }
}
