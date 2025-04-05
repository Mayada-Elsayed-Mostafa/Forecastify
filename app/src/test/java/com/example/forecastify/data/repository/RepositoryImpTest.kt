package com.example.forecastify.data.repository

import com.example.forecastify.data.local.FakeLocalDataSource
import com.example.forecastify.data.local.LocalDataSource
import com.example.forecastify.data.models.AlarmItem
import com.example.forecastify.data.remote.FakeRemoteDataSource
import com.example.forecastify.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RepositoryImpTest {
    private lateinit var fakeLocalDataSource: LocalDataSource
    private lateinit var fakeRemoteDataSource: RemoteDataSource

    private lateinit var repository: RepositoryImp

    @Before
    fun init() {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()
        repository = RepositoryImp( fakeRemoteDataSource, fakeLocalDataSource)
    }

}