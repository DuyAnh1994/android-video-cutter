package com.mobile.videocutter.di

import com.mobile.videocutter.data.repo.LocalDataRepoImpl
import com.mobile.videocutter.domain.repo.ILocalDataRepo
import com.mobile.videocutter.domain.repo.IRatioDisplay
import com.mobile.videocutter.data.repo.RatioDisplayImpl

object RepositoryFactory {
    private val localDataRepoImpl = LocalDataRepoImpl()

    private val ratioDisplayImpl = RatioDisplayImpl()

    fun getLocalDataRepo(): ILocalDataRepo {
        return localDataRepoImpl
    }

    fun getRatioDisplay(): IRatioDisplay {
        return ratioDisplayImpl
    }
}
