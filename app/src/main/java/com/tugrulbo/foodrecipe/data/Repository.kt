package com.tugrulbo.foodrecipe.data

import com.tugrulbo.foodrecipe.data.local.LocalDataSource
import com.tugrulbo.foodrecipe.data.remote.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    val remoteDataSource = remoteDataSource
    val localDataSource = localDataSource
}